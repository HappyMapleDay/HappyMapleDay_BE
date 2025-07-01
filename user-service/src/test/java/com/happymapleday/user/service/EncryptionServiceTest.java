package com.happymapleday.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
        // 테스트용 secret key 설정
        ReflectionTestUtils.setField(encryptionService, "secretKey", "test-secret-key-for-encryption");
    }

    @Test
    @DisplayName("문자열 암호화 성공")
    void encrypt_Success() {
        // given
        String plainText = "test-api-key-12345";

        // when
        String encryptedText = encryptionService.encrypt(plainText);

        // then
        assertThat(encryptedText).isNotNull();
        assertThat(encryptedText).isNotEqualTo(plainText);
        assertThat(encryptedText).isNotEmpty();
    }

    @Test
    @DisplayName("문자열 복호화 성공")
    void decrypt_Success() {
        // given
        String plainText = "test-api-key-12345";
        String encryptedText = encryptionService.encrypt(plainText);

        // when
        String decryptedText = encryptionService.decrypt(encryptedText);

        // then
        assertThat(decryptedText).isEqualTo(plainText);
    }

    @Test
    @DisplayName("암호화 후 복호화 원본 데이터 일치")
    void encryptAndDecrypt_ReturnOriginalData() {
        // given
        String originalText = "nexon-api-key-abcdef123456";

        // when
        String encrypted = encryptionService.encrypt(originalText);
        String decrypted = encryptionService.decrypt(encrypted);

        // then
        assertThat(decrypted).isEqualTo(originalText);
        assertThat(encrypted).isNotEqualTo(originalText);
    }

    @Test
    @DisplayName("빈 문자열 암호화/복호화")
    void encryptDecrypt_EmptyString() {
        // given
        String emptyString = "";

        // when
        String encrypted = encryptionService.encrypt(emptyString);
        String decrypted = encryptionService.decrypt(encrypted);

        // then
        assertThat(decrypted).isEqualTo(emptyString);
    }

    @Test
    @DisplayName("잘못된 암호화 텍스트 복호화 실패")
    void decrypt_InvalidEncryptedText_ThrowsException() {
        // given
        String invalidEncryptedText = "invalid-encrypted-text";

        // when & then
        assertThatThrownBy(() -> encryptionService.decrypt(invalidEncryptedText))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("복호화 처리 중 오류가 발생했습니다.");
    }
} 