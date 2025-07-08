package com.happymapleday.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class NexonApiServiceCacheTest {

    @Autowired
    private NexonApiService nexonApiService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void validateApiKey_ValidKey_CacheTest() {
        // Given - 유효한 API Key
        String validApiKey = "test_valid_cache_key_12345";
        ResponseEntity<String> mockResponse = ResponseEntity.ok("mock response");
        when(restTemplate.exchange(contains("/character/list"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        // When - 첫 번째 호출
        NexonApiService.ApiKeyValidationResult result1 = nexonApiService.validateApiKey(validApiKey);
        
        // Then - 첫 번째 호출 결과 확인
        assertTrue(result1.isValid());
        assertEquals(15, result1.getCharacterCount());
        
        // When - 두 번째 호출 (유효한 키 캐시에서 조회)
        NexonApiService.ApiKeyValidationResult result2 = nexonApiService.validateApiKey(validApiKey);
        
        // Then - 두 번째 호출 결과 확인
        assertTrue(result2.isValid());
        assertEquals(15, result2.getCharacterCount());
        
        // 실제 API는 한 번만 호출되어야 함 (유효한 키 캐시 적용)
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
        
        // 유효한 키 캐시에 저장되었는지 확인
        assertNotNull(cacheManager.getCache("apiKeyValidation"));
        assertNotNull(cacheManager.getCache("apiKeyValidation").get(validApiKey));
    }

    @Test
    void validateApiKey_InvalidKey_CacheTest() {
        // Given - 무효한 API Key
        String invalidApiKey = "test_invalid_cache_key_12345";
        when(restTemplate.exchange(contains("/character/list"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("401 Unauthorized"));

        // When - 첫 번째 호출
        NexonApiService.ApiKeyValidationResult result1 = nexonApiService.validateApiKey(invalidApiKey);
        
        // Then - 첫 번째 호출 결과 확인
        assertFalse(result1.isValid());
        assertEquals("인증에 실패했습니다. API Key를 확인해주세요.", result1.getErrorMessage());
        
        // When - 두 번째 호출 (무효한 키 메모리 캐시에서 조회)
        NexonApiService.ApiKeyValidationResult result2 = nexonApiService.validateApiKey(invalidApiKey);
        
        // Then - 두 번째 호출 결과 확인
        assertFalse(result2.isValid());
        assertEquals("인증에 실패했습니다. API Key를 확인해주세요.", result2.getErrorMessage());
        
        // 실제 API는 한 번만 호출되어야 함 (무효한 키 메모리 캐시 적용)
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
        
        // 무효한 키는 Spring Cache가 아닌 메모리 맵에 저장되므로 Spring Cache에는 없어야 함
        assertNull(cacheManager.getCache("apiKeyValidation").get(invalidApiKey));
    }

    @Test
    void validateApiKey_MixedKeys_SeparateCache() {
        // Given
        String validApiKey = "test_valid_mixed_key";
        String invalidApiKey = "test_invalid_mixed_key";
        
        // 유효한 키는 성공 응답 - /character/list 엔드포인트에 대해
        when(restTemplate.exchange(contains("/character/list"), eq(HttpMethod.GET), 
                argThat(entity -> entity.getHeaders().get("x-nxopen-api-key").contains(validApiKey)), 
                eq(String.class)))
                .thenReturn(ResponseEntity.ok("mock valid response"));
        
        // 무효한 키는 401 에러 - /character/list 엔드포인트에 대해
        when(restTemplate.exchange(contains("/character/list"), eq(HttpMethod.GET), 
                argThat(entity -> entity.getHeaders().get("x-nxopen-api-key").contains(invalidApiKey)), 
                eq(String.class)))
                .thenThrow(new RuntimeException("401 Unauthorized"));

        // When - 각각 다른 키로 호출
        NexonApiService.ApiKeyValidationResult validResult = nexonApiService.validateApiKey(validApiKey);
        NexonApiService.ApiKeyValidationResult invalidResult = nexonApiService.validateApiKey(invalidApiKey);
        
        // Then - 각각 올바른 결과
        assertTrue(validResult.isValid());
        assertFalse(invalidResult.isValid());
        
        // 서로 다른 키이므로 API가 2번 호출되어야 함
        verify(restTemplate, times(2)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
        
        // 유효한 키는 Spring Cache에, 무효한 키는 메모리 맵에 저장
        assertNotNull(cacheManager.getCache("apiKeyValidation").get(validApiKey));
        assertNull(cacheManager.getCache("apiKeyValidation").get(invalidApiKey));
    }
} 