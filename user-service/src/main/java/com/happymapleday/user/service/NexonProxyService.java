package com.happymapleday.user.service;

import com.happymapleday.user.dto.NexonCharacterSummaryDto;
import com.happymapleday.user.entity.User;
import com.happymapleday.user.repository.UserRepository;
import com.happymapleday.user.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class NexonProxyService {

    private static final String CACHE_KEY_USER_CHAR_LIST_PREFIX = "nexon:charlist:"; // + userId
    private static final String CACHE_KEY_CHAR_BASIC_PREFIX = "nexon:charbasic:"; // + ocid
    private static final long REFRESH_COOLDOWN_SECONDS = 60L;

    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final NexonApiService nexonApiService;
    private final CacheManager cacheManager;

    // 사용자별 리프레시 쿨타임 관리 (단일 인스턴스 기준)
    private final Map<String, LocalDateTime> refreshCooldownMap = new ConcurrentHashMap<>();

    // 사용자의 api key로 캐릭터 목록 조회 - ocid를 받아옴
    public List<NexonCharacterSummaryDto> getUserCharacters(boolean forceRefresh) {
        Long userId = SecurityUtil.getCurrentUserId(); // jwt access token에서 받아온 id 사용
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String cacheKey = CACHE_KEY_USER_CHAR_LIST_PREFIX + userId; // 메모리에 캐시 할 때 사용할 캐시 키 생성
        Cache cache = cacheManager.getCache("nexon-cache");

        if (!forceRefresh) {
            // 메모리에 해당 키로 캐시되어있는 데이터가 있다면 그걸 사용
            List<NexonCharacterSummaryDto> cached = getCacheValue(cache, cacheKey);
            if (cached != null) {
                return cached;
            }
        } else {
            ensureRefreshCooldown("user:" + userId);
        }

        String decryptedKey = encryptionService.decrypt(user.getNexonApiKey()); // 복호화 키 생성
        List<String> ocids = nexonApiService.getUserCharacterOcids(decryptedKey).block(); // 복호화한 키로 ocid 받아옴

        List<NexonCharacterSummaryDto> results = Flux.fromIterable(ocids)
                .flatMap(ocid -> nexonApiService.getCharacterBasic(decryptedKey, ocid)
                        .map(basic -> {
                            putCacheValue(cache, CACHE_KEY_CHAR_BASIC_PREFIX + ocid, basic);
                            return NexonCharacterSummaryDto.from(basic, ocid);
                        })
                        .onErrorResume(ex -> Mono.empty()),
                        6)
                .collectList()
                .block();

        putCacheValue(cache, cacheKey, results);
        return results; // 캐릭터 정보 리턴
    }

    // 리프레시 쿨타임 계산기
    private void ensureRefreshCooldown(String key) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime prev = refreshCooldownMap.get(key);
        if (prev != null && prev.plusSeconds(REFRESH_COOLDOWN_SECONDS).isAfter(now)) {
            long remain = java.time.Duration.between(now, prev.plusSeconds(REFRESH_COOLDOWN_SECONDS)).getSeconds();
            throw new IllegalArgumentException("다시 불러오기 쿨타임이 아직 남았습니다. " + remain + "초 후에 다시 시도해주세요.");
        }
        refreshCooldownMap.put(key, now);
    }

    @SuppressWarnings("unchecked")
    private <T> T getCacheValue(Cache cache, String key) {
        if (cache == null) return null;
        Cache.ValueWrapper wrapper = cache.get(key);
        return wrapper != null ? (T) wrapper.get() : null;
    }

    private void putCacheValue(Cache cache, String key, Object value) {
        if (cache != null) cache.put(key, value);
    }
}


