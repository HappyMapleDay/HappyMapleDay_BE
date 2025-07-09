package com.happymapleday.settlement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "desire_item_records",
       indexes = {
           @Index(name = "idx_weekly_boss_record", columnList = "weekly_boss_record_id"),
           @Index(name = "idx_character", columnList = "character_id")
       })
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DesireItemRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "weekly_boss_record_id", nullable = false)
    private Long weeklyBossRecordId;

    @NotNull
    @Column(name = "character_id", nullable = false)
    private Long characterId;
    
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
} 