package com.happymapleday.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.happymapleday.user.dto.NexonCharacterSummaryDto;
import com.happymapleday.user.entity.User;
import com.happymapleday.user.repository.UserRepository;
import com.happymapleday.user.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
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

    @Autowired
    public NexonProxyService(UserRepository userRepository,
                             EncryptionService encryptionService,
                             NexonApiService nexonApiService,
                             CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.nexonApiService = nexonApiService;
        this.cacheManager = cacheManager;
    }

    public List<NexonCharacterSummaryDto> getUserCharacters(boolean forceRefresh) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String cacheKey = CACHE_KEY_USER_CHAR_LIST_PREFIX + userId;
        Cache cache = cacheManager.getCache("nexon-cache");

        if (!forceRefresh) {
            List<NexonCharacterSummaryDto> cached = getCacheValue(cache, cacheKey);
            if (cached != null) {
                return cached;
            }
        } else {
            ensureRefreshCooldown("user:" + userId);
        }

        String decryptedKey = encryptionService.decrypt(user.getNexonApiKey());
        List<String> ocids = nexonApiService.getUserCharacterOcids(decryptedKey);

        List<NexonCharacterSummaryDto> results = new ArrayList<>();
        for (String ocid : ocids) {
            JsonNode basic = nexonApiService.getCharacterBasic(decryptedKey, ocid);
            results.add(NexonCharacterSummaryDto.from(basic, ocid));
            // 캐릭터 기본정보 캐시도 같이 채움
            putCacheValue(cache, CACHE_KEY_CHAR_BASIC_PREFIX + ocid, basic);
        }

        putCacheValue(cache, cacheKey, results);
        return results;
    }

    // 개별 캐릭터 기본 정보는 현재 제공하지 않음

    private void ensureRefreshCooldown(String key) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime prev = refreshCooldownMap.get(key);
        if (prev != null && prev.plusSeconds(REFRESH_COOLDOWN_SECONDS).isAfter(now)) {
            long remain = java.time.Duration.between(now, prev.plusSeconds(REFRESH_COOLDOWN_SECONDS)).getSeconds();
            throw new IllegalArgumentException("리프레시 쿨타임이 아직 남았습니다. " + remain + "초 후에 다시 시도해주세요.");
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


