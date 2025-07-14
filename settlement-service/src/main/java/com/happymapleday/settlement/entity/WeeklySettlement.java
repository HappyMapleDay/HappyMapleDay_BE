package com.happymapleday.settlement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "weekly_settlements",
       uniqueConstraints = {
           @UniqueConstraint(name = "unique_weekly_settlement", 
                           columnNames = {"user_id", "world_name", "week_start_date"})
       },
       indexes = {
           @Index(name = "idx_user_week", columnList = "user_id, week_start_date")
       })
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WeeklySettlement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @NotNull
    @Column(name = "world_name", nullable = false, length = 20)
    private String worldName;
    
    @NotNull
    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;
    
    @Column(name = "total_crystal_income")
    private BigInteger totalCrystalIncome = BigInteger.ZERO;
    
    @Column(name = "total_desire_item_income")
    private BigInteger totalDesireItemIncome = BigInteger.ZERO;
    
    @Column(name = "total_income")
    private BigInteger totalIncome = BigInteger.ZERO;
    
    @Column(name = "total_boss_count")
    private Integer totalBossCount = 0;
    
    @Column(name = "character_count")
    private Integer characterCount = 0;
    
    @Column(name = "is_finalized")
    private Boolean isFinalized = false;
    
    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 연관관계
    @OneToMany(mappedBy = "weeklySettlement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeeklyBossRecord> bossRecords;

    @Builder
    public WeeklySettlement(Long userId, String worldName, LocalDate weekStartDate,
                           BigInteger totalCrystalIncome, BigInteger totalDesireItemIncome,
                           BigInteger totalIncome, Integer totalBossCount, Integer characterCount,
                           Boolean isFinalized, LocalDateTime finalizedAt) {
        this.userId = userId;
        this.worldName = worldName;
        this.weekStartDate = weekStartDate;
        this.totalCrystalIncome = totalCrystalIncome != null ? totalCrystalIncome : BigInteger.ZERO;
        this.totalDesireItemIncome = totalDesireItemIncome != null ? totalDesireItemIncome : BigInteger.ZERO;
        this.totalIncome = totalIncome != null ? totalIncome : BigInteger.ZERO;
        this.totalBossCount = totalBossCount != null ? totalBossCount : 0;
        this.characterCount = characterCount != null ? characterCount : 0;
        this.isFinalized = isFinalized != null ? isFinalized : false;
        this.finalizedAt = finalizedAt;
    }
    
    // 도메인 로직 메서드 (불변 계산)
    public BigInteger calculateTotalCrystalIncome() {
        if (bossRecords == null || bossRecords.isEmpty()) {
            return BigInteger.ZERO;
        }
        return bossRecords.stream()
                .map(WeeklyBossRecord::getCrystalIncome)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }
    
    public BigInteger calculateTotalDesireItemIncome() {
        if (bossRecords == null || bossRecords.isEmpty()) {
            return BigInteger.ZERO;
        }
        return bossRecords.stream()
                .map(WeeklyBossRecord::getDesireItemIncome)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    // 결정석 제한 관련 메서드
    public Map<Long, Integer> getCharacterCrystalCounts() {
        return WeeklyBossRecord.getCharacterCrystalCounts(bossRecords);
    }
} 