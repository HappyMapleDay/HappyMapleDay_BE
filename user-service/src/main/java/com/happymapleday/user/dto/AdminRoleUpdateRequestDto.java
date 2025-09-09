package com.happymapleday.user.dto;

import com.happymapleday.user.entity.UserRole;
import jakarta.validation.constraints.NotNull;

public class AdminRoleUpdateRequestDto {
    
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
    
    @NotNull(message = "사용자 권한은 필수입니다.")
    private UserRole role;
    
    // 기본 생성자
    public AdminRoleUpdateRequestDto() {}
    
    // 생성자
    public AdminRoleUpdateRequestDto(Long userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }
    
    // Getter 메서드들
    public Long getUserId() {
        return userId;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    // Setter 메서드들
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
}
