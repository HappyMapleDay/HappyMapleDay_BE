package com.happymapleday.user.dto;

import com.happymapleday.user.entity.UserRole;

public class AdminRoleUpdateResponseDto {
    
    private boolean success;
    private String message;
    private Long userId;
    private String mainCharacterName;
    private UserRole role;
    
    // 기본 생성자
    public AdminRoleUpdateResponseDto() {}
    
    // 생성자
    public AdminRoleUpdateResponseDto(boolean success, String message, Long userId, String mainCharacterName, UserRole role) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.mainCharacterName = mainCharacterName;
        this.role = role;
    }
    
    // 정적 팩토리 메서드
    public static AdminRoleUpdateResponseDto success(Long userId, String mainCharacterName, UserRole role) {
        String message = role.isAdmin() ? 
            "어드민 권한이 부여되었습니다." : 
            "일반 사용자 권한으로 변경되었습니다.";
        return new AdminRoleUpdateResponseDto(true, message, userId, mainCharacterName, role);
    }
    
    public static AdminRoleUpdateResponseDto failure(String message) {
        return new AdminRoleUpdateResponseDto(false, message, null, null, null);
    }
    
    // Getter 메서드들
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getMainCharacterName() {
        return mainCharacterName;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    // 편의 메서드
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }
    
    // Setter 메서드들
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public void setMainCharacterName(String mainCharacterName) {
        this.mainCharacterName = mainCharacterName;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
}
