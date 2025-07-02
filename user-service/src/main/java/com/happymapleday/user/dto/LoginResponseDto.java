package com.happymapleday.user.dto;

public class LoginResponseDto {
    
    private String token;          // Access Token
    private String refreshToken;   // Refresh Token
    private UserInfo user;
    
    // 기본 생성자
    public LoginResponseDto() {}
    
    // 생성자
    public LoginResponseDto(String token, String refreshToken, UserInfo user) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }
    
    // 정적 팩토리 메서드
    public static LoginResponseDto of(String accessToken, String refreshToken, Long userId, String mainCharacterName) {
        return new LoginResponseDto(accessToken, refreshToken, new UserInfo(userId, mainCharacterName));
    }
    
    // Getter 메서드들
    public String getToken() {
        return token;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    // Setter 메서드들
    public void setToken(String token) {
        this.token = token;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
    }
    
    // 내부 클래스
    public static class UserInfo {
        private Long id;
        private String mainCharacterName;
        
        public UserInfo() {}
        
        public UserInfo(Long id, String mainCharacterName) {
            this.id = id;
            this.mainCharacterName = mainCharacterName;
        }
        
        // Getter 메서드들
        public Long getId() {
            return id;
        }
        
        public String getMainCharacterName() {
            return mainCharacterName;
        }
        
        // Setter 메서드들
        public void setId(Long id) {
            this.id = id;
        }
        
        public void setMainCharacterName(String mainCharacterName) {
            this.mainCharacterName = mainCharacterName;
        }
    }
} 