package com.happymapleday.user.entity;

/**
 * 사용자 권한 역할을 정의하는 ENUM
 * 확장성을 고려하여 추후 MANAGER, MODERATOR 등 추가 가능
 */
public enum UserRole {
    NORMAL("일반 사용자"),
    ADMIN("관리자");
    
    private final String description;
    
    UserRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    // 어드민 권한 체크 메서드
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    // 일반 사용자 권한 체크 메서드
    public boolean isNormal() {
        return this == NORMAL;
    }
}

