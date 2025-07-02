package com.happymapleday.user.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_settings")
@EntityListeners(AuditingEntityListener.class)
public class UserSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(name = "weekly_reset_enabled", nullable = false)
    private Boolean weeklyResetEnabled; // 일주일 초기화 토글
    
    @Column(name = "data_collection_agreed", nullable = false)
    private Boolean dataCollectionAgreed; // 데이터 수집 동의
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 기본 생성자
    protected UserSettings() {}
    
    // 생성자
    public UserSettings(Long userId, Boolean dataCollectionAgreed) {
        this.userId = userId;
        this.dataCollectionAgreed = dataCollectionAgreed;
        this.weeklyResetEnabled = true; // 기본값
    }
    
    // 테스트용 생성자
    public UserSettings(Long userId, Boolean dataCollectionAgreed, Boolean weeklyResetEnabled) {
        this.userId = userId;
        this.dataCollectionAgreed = dataCollectionAgreed;
        this.weeklyResetEnabled = weeklyResetEnabled;
    }
    
    // Getter 메서드들
    public Long getId() {
        return id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Boolean getWeeklyResetEnabled() {
        return weeklyResetEnabled;
    }
    
    public Boolean getDataCollectionAgreed() {
        return dataCollectionAgreed;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Setter 메서드들
    public void updateWeeklyResetEnabled(Boolean weeklyResetEnabled) {
        this.weeklyResetEnabled = weeklyResetEnabled;
    }
    
    public void updateDataCollectionAgreed(Boolean dataCollectionAgreed) {
        this.dataCollectionAgreed = dataCollectionAgreed;
    }
} 