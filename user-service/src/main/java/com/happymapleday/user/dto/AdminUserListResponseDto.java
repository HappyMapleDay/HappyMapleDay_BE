package com.happymapleday.user.dto;

import com.happymapleday.user.entity.UserRole;
import java.time.LocalDateTime;
import java.util.List;

public class AdminUserListResponseDto {
    
    private List<UserSummary> users;
    private int totalCount;
    
    // 기본 생성자
    public AdminUserListResponseDto() {}
    
    // 생성자
    public AdminUserListResponseDto(List<UserSummary> users, int totalCount) {
        this.users = users;
        this.totalCount = totalCount;
    }
    
    // 정적 팩토리 메서드
    public static AdminUserListResponseDto of(List<UserSummary> users) {
        return new AdminUserListResponseDto(users, users.size());
    }
    
    // Getter 메서드들
    public List<UserSummary> getUsers() {
        return users;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    // Setter 메서드들
    public void setUsers(List<UserSummary> users) {
        this.users = users;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    // 내부 클래스
    public static class UserSummary {
        private Long id;
        private String mainCharacterName;
        private UserRole role;
        private LocalDateTime createdAt;
        
        public UserSummary() {}
        
        public UserSummary(Long id, String mainCharacterName, UserRole role, LocalDateTime createdAt) {
            this.id = id;
            this.mainCharacterName = mainCharacterName;
            this.role = role;
            this.createdAt = createdAt;
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
        
        // 편의 메서드
        public boolean isAdmin() {
            return role != null && role.isAdmin();
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
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
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}
