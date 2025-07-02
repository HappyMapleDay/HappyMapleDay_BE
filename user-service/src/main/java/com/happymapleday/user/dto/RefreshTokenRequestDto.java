package com.happymapleday.user.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequestDto {
    
    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
    
    // 기본 생성자
    public RefreshTokenRequestDto() {}
    
    // 생성자
    public RefreshTokenRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    // Getter
    public String getRefreshToken() {
        return refreshToken;
    }
    
    // Setter
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
} 