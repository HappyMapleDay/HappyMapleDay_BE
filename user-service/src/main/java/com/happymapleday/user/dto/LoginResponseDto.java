package com.happymapleday.user.dto;

import com.happymapleday.user.entity.UserRole;

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
    
    // JWT에 role이 포함되어 있으므로 클라이언트 라우팅을 위해 role 정보 제공
    // 실제 권한 검증은 서버에서 JWT 토큰 검증으로 수행
    public static LoginResponseDto of(String accessToken, String refreshToken, Long userId, String mainCharacterName, UserRole role) {
        return new LoginResponseDto(accessToken, refreshToken, new UserInfo(userId, mainCharacterName, role));
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
        private UserRole role;
        
        public UserInfo() {}
        
        public UserInfo(Long id, String mainCharacterName) {
            this.id = id;
            this.mainCharacterName = mainCharacterName;
            this.role = UserRole.NORMAL; // 기본값
        }
        
        public UserInfo(Long id, String mainCharacterName, UserRole role) {
            this.id = id;
            this.mainCharacterName = mainCharacterName;
            this.role = role;
        }
        
        // Getter 메서드들
        public Long getId() {
            return id;
        }
        
        public String getMainCharacterName() {
            return mainCharacterName;
        }
        
        public UserRole getRole() {
            return role;
        }
        
        // 편의 메서드: 어드민 권한 체크 (클라이언트 라우팅용)
        public boolean isAdmin() {
            return role != null && role.isAdmin();
        }
        
        // Setter 메서드들
        public void setId(Long id) {
            this.id = id;
        }
        
        public void setMainCharacterName(String mainCharacterName) {
            this.mainCharacterName = mainCharacterName;
        }
        
        public void setRole(UserRole role) {
            this.role = role;
        }
    }
} 