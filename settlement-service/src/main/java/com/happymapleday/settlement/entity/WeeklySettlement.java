package com.happymapleday.settlement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDate;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private BigInteger totalCrystalIncome;
    
    @Column(name = "total_desire_item_income")
    private BigInteger totalDesireItemIncome;
    
    @Column(name = "total_income")
    private BigInteger totalIncome;
    
    @Column(name = "total_boss_count")
    private Integer totalBossCount;
    
    @Column(name = "character_count")
    private Integer characterCount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_status", nullable = false)
    private SettlementStatus status;

    @Version
    @Column(name = "version")
    private Long version;
    
    // 연관관계
    @OneToMany(mappedBy = "weeklySettlement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeeklyBossRecord> bossRecords;

    // 결정석 제한 관련 메서드
    public Map<Long, Integer> getCharacterCrystalCounts() {
        return WeeklyBossRecord.getCharacterCrystalCounts(bossRecords);
    }
} 