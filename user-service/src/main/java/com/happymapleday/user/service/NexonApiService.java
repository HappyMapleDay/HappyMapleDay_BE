package com.happymapleday.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import reactor.core.publisher.Mono;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NexonApiService {
    
    private static final String NEXON_API_BASE_URL = "https://open.api.nexon.com/maplestory/v1";
    private static final String API_KEY_HEADER = "x-nxopen-api-key";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;
    private final WebClient webClient;
    
    // 마스터키 설정 (환경변수에서 가져옴)
    @Value("${nexon.api.master-key:}")
    private String masterApiKey;
    
    // 무효한 키 임시 저장소 (30분 만료)
    private final ConcurrentHashMap<String, CachedResult> invalidKeysCache = new ConcurrentHashMap<>();
    
    @Autowired
    public NexonApiService(RestTemplate restTemplate, ObjectMapper objectMapper, CacheManager cacheManager, WebClient webClient) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.cacheManager = cacheManager;
        this.webClient = webClient;
    }
    
    /**
     * 넥슨 API Key 유효성 검증 (조건부 캐시 적용)
     * - 유효한 키: 1시간 캐시 (Spring Cache)
     * - 무효한 키: 30분 캐시 (메모리 Map)
     * - 마스터키가 설정된 경우 마스터키 검증 우선
     * @param apiKey 검증할 API Key
     * @return 유효한 API Key인지 여부와 관련 정보
     */
    public ApiKeyValidationResult validateApiKey(String apiKey) {
        // 마스터키가 설정되어 있고, 입력된 키가 마스터키와 일치하면 즉시 유효 처리
        if (isMasterKeyEnabled() && apiKey.equals(masterApiKey)) {
            return new ApiKeyValidationResult(true, 15, null);
        }
        
        // 1. 유효한 키 캐시에서 먼저 확인 (1시간)
        Cache validCache = cacheManager.getCache("apiKeyValidation");
        if (validCache != null) {
            Cache.ValueWrapper cachedValid = validCache.get(apiKey);
            if (cachedValid != null) {
                return (ApiKeyValidationResult) cachedValid.get();
            }
        }
        
        // 2. 무효한 키 캐시에서 확인 (30분)
        CachedResult cachedInvalid = invalidKeysCache.get(apiKey);
        if (cachedInvalid != null && !cachedInvalid.isExpired()) {
            return cachedInvalid.getResult();
        } else if (cachedInvalid != null && cachedInvalid.isExpired()) {
            invalidKeysCache.remove(apiKey); // 만료된 항목 제거
        }
        
        // 3. 캐시에 없으면 실제 API 호출
        ApiKeyValidationResult result = callNexonApi(apiKey);
        
        // 4. 결과에 따라 다른 캐시에 저장
        if (result.isValid()) {
            // 유효한 키는 Spring Cache에 저장 (1시간 - application.yml 설정대로)
            if (validCache != null) {
                validCache.put(apiKey, result);
            }
        } else {
            // 무효한 키는 메모리 맵에 저장 (30분)
            invalidKeysCache.put(apiKey, new CachedResult(result, LocalDateTime.now().plusMinutes(30)));
        }
        
        return result;
    }
    
    /**
     * 마스터키가 설정되어 있는지 확인
     */
    private boolean isMasterKeyEnabled() {
        return masterApiKey != null && !masterApiKey.trim().isEmpty();
    }
    
    /**
     * 실제 넥슨 API 호출
     */
    private ApiKeyValidationResult callNexonApi(String apiKey) {
        try {
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set(API_KEY_HEADER, apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 캐릭터 목록 조회 API로 유효성 검증 (API Key만 필요)
            String url = NEXON_API_BASE_URL + "/character/list";
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            // 응답이 성공적이면 유효한 API Key
            if (response.getStatusCode().is2xxSuccessful()) {
                // 실제 캐릭터 개수 조회는 복잡하므로 임시로 기본값 반환
                return new ApiKeyValidationResult(true, 15, null);
            } else {
                return new ApiKeyValidationResult(false, 0, "API Key 검증에 실패했습니다.");
            }
            
        } catch (Exception e) {
            // HTTP 상태 코드별 처리
            String message = e.getMessage();
            if (message != null) {
                if (message.contains("401")) {
                    // 401 Unauthorized = 무효한 API Key
                    return new ApiKeyValidationResult(false, 0, "인증에 실패했습니다. API Key를 확인해주세요.");
                } else if (message.contains("403")) {
                    // 403 Forbidden = API 접근 거부
                    return new ApiKeyValidationResult(false, 0, "API 접근이 거부되었습니다.");
                } else if (message.contains("404")) {
                    // 404 Not Found = 유효한 API Key, 캐릭터만 존재하지 않음
                    return new ApiKeyValidationResult(true, 15, null);
                } else if (message.contains("429")) {
                    // 429 Too Many Requests = API 호출 한도 초과
                    return new ApiKeyValidationResult(false, 0, "API 호출 한도를 초과했습니다.");
                }
            }
            
            // 기타 에러는 무효한 API Key로 처리
            return new ApiKeyValidationResult(false, 0, "유효하지 않은 API Key입니다.");
        }
    }
    
    /**
     * 사용자 키로 캐릭터 OCID 목록 조회
     */
    public Mono<List<String>> getUserCharacterOcids(String apiKey) {
        String url = NEXON_API_BASE_URL + "/character/list";
        return webClient.get()
                .uri(url)
                .header(API_KEY_HEADER, apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> {
                    try {
                        JsonNode root = objectMapper.readTree(body);
                        List<String> ocids = new ArrayList<>();
                        if (root.isArray()) {
                            for (JsonNode node : root) {
                                JsonNode ocidNode = node.get("ocid");
                                if (ocidNode != null && !ocidNode.isNull()) {
                                    ocids.add(ocidNode.asText());
                                }
                            }
                        } else {
                            JsonNode listNode = root.get("characters");
                            if (listNode != null && listNode.isArray()) {
                                for (JsonNode node : listNode) {
                                    JsonNode ocidNode = node.get("ocid");
                                    if (ocidNode != null && !ocidNode.isNull()) {
                                        ocids.add(ocidNode.asText());
                                    }
                                }
                            } else {
                                Iterator<String> fieldNames = root.fieldNames();
                                while (fieldNames.hasNext()) {
                                    String field = fieldNames.next();
                                    JsonNode candidate = root.get(field);
                                    if (candidate != null && candidate.isArray()) {
                                        for (JsonNode node : candidate) {
                                            JsonNode ocidNode = node.get("ocid");
                                            if (ocidNode != null && !ocidNode.isNull()) {
                                                ocids.add(ocidNode.asText());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return ocids;
                    } catch (Exception e) {
                        throw new RuntimeException("넥슨 캐릭터 목록 파싱 중 오류가 발생했습니다.", e);
                    }
                });
    }

    /**
     * 캐릭터 기본 정보 조회 (닉네임/직업/월드/레벨/이미지 등)
     */
    public Mono<JsonNode> getCharacterBasic(String apiKey, String ocid) {
        String url = NEXON_API_BASE_URL + "/character/basic?ocid=" + ocid;
        return webClient.get()
                .uri(url)
                .header(API_KEY_HEADER, apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> {
                    try {
                        return objectMapper.readTree(body);
                    } catch (Exception e) {
                        throw new RuntimeException("넥슨 캐릭터 기본 정보 파싱 중 오류가 발생했습니다.", e);
                    }
                });
    }

    /**
     * API Key 검증 결과를 담는 클래스
     */
    public static class ApiKeyValidationResult {
        private final boolean valid;
        private final int characterCount;
        private final String errorMessage;
        
        public ApiKeyValidationResult(boolean valid, int characterCount, String errorMessage) {
            this.valid = valid;
            this.characterCount = characterCount;
            this.errorMessage = errorMessage;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public int getCharacterCount() {
            return characterCount;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
    
    /**
     * 무효한 키 캐시용 래퍼 클래스
     */
    private static class CachedResult {
        private final ApiKeyValidationResult result;
        private final LocalDateTime expiredAt;
        
        public CachedResult(ApiKeyValidationResult result, LocalDateTime expiredAt) {
            this.result = result;
            this.expiredAt = expiredAt;
        }
        
        public ApiKeyValidationResult getResult() {
            return result;
        }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiredAt);
        }
    }
} 