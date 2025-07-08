package com.happymapleday.settlement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "desire_item_records",
       indexes = {
           @Index(name = "idx_weekly_boss_record", columnList = "weekly_boss_record_id")
       })
public class DesireItemRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "weekly_boss_record_id", nullable = false)
    private Long weeklyBossRecordId;
    
    @NotNull
    @Column(name = "desire_item_id", nullable = false)
    private Long desireItemId;
    
    @NotNull
    @Column(name = "sale_price", nullable = false)
    private BigInteger salePrice;
    
    @CreationTimestamp
    @Column(name = "acquired_at")
    private LocalDateTime acquiredAt;
    
    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_boss_record_id", insertable = false, updatable = false)
    private WeeklyBossRecord weeklyBossRecord;
    
    // 기본 생성자
    public DesireItemRecord() {}
    
    // 생성자
    public DesireItemRecord(Long weeklyBossRecordId, Long desireItemId, BigInteger salePrice) {
        this.weeklyBossRecordId = weeklyBossRecordId;
        this.desireItemId = desireItemId;
        this.salePrice = salePrice;
    }
    
    // Getter
    public Long getId() {
        return id;
    }
    
    public Long getWeeklyBossRecordId() {
        return weeklyBossRecordId;
    }
    
    public Long getDesireItemId() {
        return desireItemId;
    }
    
    public BigInteger getSalePrice() {
        return salePrice;
    }
    
    public LocalDateTime getAcquiredAt() {
        return acquiredAt;
    }
    
    public WeeklyBossRecord getWeeklyBossRecord() {
        return weeklyBossRecord;
    }
} 