package com.happymapleday.user.dto;

import java.time.LocalDateTime;

public class SignupResponseDto {
    
    private Long userId;
    private String mainCharacterName;
    private LocalDateTime createdAt;
    private String message;
    
    // 기본 생성자
    public SignupResponseDto() {}
    
    // 생성자
    public SignupResponseDto(Long userId, String mainCharacterName, LocalDateTime createdAt, String message) {
        this.userId = userId;
        this.mainCharacterName = mainCharacterName;
        this.createdAt = createdAt;
        this.message = message;
    }
    
    // 성공 응답 생성 정적 메서드
    public static SignupResponseDto success(Long userId, String mainCharacterName, LocalDateTime createdAt) {
        return new SignupResponseDto(userId, mainCharacterName, createdAt, "회원가입이 성공적으로 완료되었습니다.");
    }
    
    // Getter 메서드들
    public Long getUserId() {
        return userId;
    }
    
    public String getMainCharacterName() {
        return mainCharacterName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public String getMessage() {
        return message;
    }
    
    // Setter 메서드들
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public void setMainCharacterName(String mainCharacterName) {
        this.mainCharacterName = mainCharacterName;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
} 