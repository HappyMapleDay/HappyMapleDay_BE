package com.happymapleday.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    
    @Value("${encryption.secret-key:happy-maple-day-encryption-key-12345}")
    private String secretKey;
    
    // 암호화
    public String encrypt(String plainText) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                secretKey.substring(0, 16).getBytes(), ALGORITHM
            );
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            
            byte[] encryptedData = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("암호화 처리 중 오류가 발생했습니다.", e);
        }
    }
    
    // 복호화
    public String decrypt(String encryptedText) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                secretKey.substring(0, 16).getBytes(), ALGORITHM
            );
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedData);
        } catch (Exception e) {
            throw new RuntimeException("복호화 처리 중 오류가 발생했습니다.", e);
        }
    }
} 