package com.happymapleday.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;  // 토큰의 해시값 저장 (보안상 원본 저장 안함)
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "device_info", length = 500)
    private String deviceInfo;  // 디바이스 정보 (User-Agent 등)
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;   // IPv6 고려한 길이
    
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;  // 한 번 사용되면 true
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    // 기본 생성자
    public RefreshToken() {}
    
    // 생성자
    public RefreshToken(Long userId, String tokenHash, LocalDateTime expiresAt, 
                       String deviceInfo, String ipAddress) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();
        this.isUsed = false;
    }
    
    // 토큰 사용 처리
    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }
    
    // 만료 여부 확인
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
    
    // 유효한 토큰인지 확인 (만료되지 않고, 사용되지 않은 토큰)
    public boolean isValid() {
        return !isExpired() && !isUsed;
    }
    
    // Getter 메서드들
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public String getDeviceInfo() { return deviceInfo; }
    public String getIpAddress() { return ipAddress; }
    public Boolean getIsUsed() { return isUsed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUsedAt() { return usedAt; }
    
    // Setter 메서드들
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setIsUsed(Boolean isUsed) { this.isUsed = isUsed; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }
} 