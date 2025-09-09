package com.happymapleday.user.dto;

public class AdminVerificationResponseDto {
    
    private boolean isAdmin;
    private String adminLevel; // SUPER_ADMIN, ADMIN, MODERATOR 등
    private long tokenValidUntil; // 토큰 만료 시간
    
    // 기본 생성자
    public AdminVerificationResponseDto() {}
    
    // 생성자
    public AdminVerificationResponseDto(boolean isAdmin, String adminLevel, long tokenValidUntil) {
        this.isAdmin = isAdmin;
        this.adminLevel = adminLevel;
        this.tokenValidUntil = tokenValidUntil;
    }
    
    // 정적 팩토리 메서드
    public static AdminVerificationResponseDto admin(String level, long validUntil) {
        return new AdminVerificationResponseDto(true, level, validUntil);
    }
    
    public static AdminVerificationResponseDto notAdmin() {
        return new AdminVerificationResponseDto(false, null, 0);
    }
    
    // Getter 메서드들
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public String getAdminLevel() {
        return adminLevel;
    }
    
    public long getTokenValidUntil() {
        return tokenValidUntil;
    }
    
    // Setter 메서드들
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
    
    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }
    
    public void setTokenValidUntil(long tokenValidUntil) {
        this.tokenValidUntil = tokenValidUntil;
    }
}

