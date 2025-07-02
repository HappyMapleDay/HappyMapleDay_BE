package com.happymapleday.user.dto;

public class RefreshTokenResponseDto {
    
    private String accessToken;
    private String refreshToken;
    
    // 기본 생성자
    public RefreshTokenResponseDto() {}
    
    // 생성자
    public RefreshTokenResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    
    // 정적 팩토리 메서드
    public static RefreshTokenResponseDto of(String accessToken, String refreshToken) {
        return new RefreshTokenResponseDto(accessToken, refreshToken);
    }
    
    // Getter 메서드들
    public String getAccessToken() {
        return accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    // Setter 메서드들
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
} 