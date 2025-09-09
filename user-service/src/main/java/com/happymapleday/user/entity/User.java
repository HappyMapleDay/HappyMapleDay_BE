package com.happymapleday.user.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "main_character_name", unique = true, nullable = false, length = 12)
    private String mainCharacterName; // 로그인 ID
    
    @Column(name = "password", nullable = false)
    private String password; // 암호화된 비밀번호
    
    @Column(name = "nexon_api_key", nullable = false)
    private String nexonApiKey; // 암호화된 API Key
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.NORMAL; // 사용자 권한 역할
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 기본 생성자
    protected User() {}
    
    // 생성자
    public User(String mainCharacterName, String password, String nexonApiKey) {
        this.mainCharacterName = mainCharacterName;
        this.password = password;
        this.nexonApiKey = nexonApiKey;
        this.role = UserRole.NORMAL; // 기본값은 일반 유저
    }
    
    // Getter 메서드들
    public Long getId() {
        return id;
    }
    
    public String getMainCharacterName() {
        return mainCharacterName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getNexonApiKey() {
        return nexonApiKey;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    // 편의 메서드: 어드민 권한 체크
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }
    
    // 편의 메서드: 일반 사용자 체크
    public boolean isNormal() {
        return role != null && role.isNormal();
    }
    
    // Setter 메서드들 (필요한 경우)
    public void updatePassword(String password) {
        this.password = password;
    }
    
    public void updateNexonApiKey(String nexonApiKey) {
        this.nexonApiKey = nexonApiKey;
    }
    
    public void updateMainCharacterName(String mainCharacterName) {
        this.mainCharacterName = mainCharacterName;
    }
    
    public void updateRole(UserRole role) {
        this.role = role;
    }
} 