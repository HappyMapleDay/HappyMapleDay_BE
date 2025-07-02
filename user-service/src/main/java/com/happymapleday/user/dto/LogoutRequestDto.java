package com.happymapleday.user.dto;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequestDto {
    
    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
    
    // 기본 생성자
    public LogoutRequestDto() {}
    
    // 생성자
    public LogoutRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    // Getter 메서드
    public String getRefreshToken() {
        return refreshToken;
    }
    
    // Setter 메서드
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
} 