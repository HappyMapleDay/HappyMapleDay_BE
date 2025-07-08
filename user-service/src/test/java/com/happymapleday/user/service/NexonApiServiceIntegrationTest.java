package com.happymapleday.user.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class NexonApiServiceIntegrationTest {
    
    @Autowired
    private NexonApiService nexonApiService;
    
    @Test
    //@Disabled("실제 API 호출 테스트 - 필요시 활성화")
    void testActualApiKeyValidation() {
        // given
        String actualApiKey = "test_0dc1c607d11d74ec08d34f023f1b9507bd146cfaf1bfdcdc1037134a908f13f1efe8d04e6d233bd35cf2fabdeb93fb0d";
        
        // when
        NexonApiService.ApiKeyValidationResult result = nexonApiService.validateApiKey(actualApiKey);
        
        // then
        System.out.println("API Key Validation Result:");
        System.out.println("Valid: " + result.isValid());
        System.out.println("Character Count: " + result.getCharacterCount());
        System.out.println("Error Message: " + result.getErrorMessage());
        
        // 결과 검증
        assertThat(result).isNotNull();
        if (result.isValid()) {
            assertThat(result.getCharacterCount()).isGreaterThanOrEqualTo(0);
            assertThat(result.getErrorMessage()).isNull();
        } else {
            assertThat(result.getErrorMessage()).isNotNull();
        }
    }
    
    @Test
    @Disabled("잘못된 API 키 테스트 - 필요시 활성화")
    void testInvalidApiKeyValidation() {
        // given
        String invalidApiKey = "invalid_api_key_12345";
        
        // when
        NexonApiService.ApiKeyValidationResult result = nexonApiService.validateApiKey(invalidApiKey);
        
        // then
        System.out.println("Invalid API Key Test Result:");
        System.out.println("Valid: " + result.isValid());
        System.out.println("Character Count: " + result.getCharacterCount());
        System.out.println("Error Message: " + result.getErrorMessage());
        
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrorMessage()).isNotNull();
    }
} 