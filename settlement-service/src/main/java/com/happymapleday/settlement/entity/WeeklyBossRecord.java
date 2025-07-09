package com.happymapleday.settlement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "weekly_boss_records",
       uniqueConstraints = {
           @UniqueConstraint(name = "unique_weekly_boss", 
                           columnNames = {"character_id", "boss_id", "week_start_date"})
       },
       indexes = {
           @Index(name = "idx_settlement", columnList = "settlement_id"),
           @Index(name = "idx_user_week", columnList = "user_id, week_start_date"),
           @Index(name = "idx_character_week", columnList = "character_id, week_start_date")
       })
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WeeklyBossRecord {
    
    // 결정석 판매 제한 상수
    public static final int CHARACTER_CRYSTAL_LIMIT = 12;
    public static final int WORLD_CRYSTAL_LIMIT = 90;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "settlement_id", nullable = false)
    private Long settlementId;
    
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @NotNull
    @Column(name = "character_id", nullable = false)
    private Long characterId;
    
    @NotNull
    @Column(name = "boss_id", nullable = false)
    private Long bossId;
    
    @Column(name = "party_size")
    private Integer partySize = 1;
    
    @NotNull
    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;
    
    @NotNull
    @Column(name = "crystal_income", nullable = false)
    private BigInteger crystalIncome;
    
    @Column(name = "desire_item_income")
    private BigInteger desireItemIncome = BigInteger.ZERO;
    
    @NotNull
    @Column(name = "total_income", nullable = false)
    private BigInteger totalIncome;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", insertable = false, updatable = false)
    private WeeklySettlement weeklySettlement;
    
    @OneToMany(mappedBy = "weeklyBossRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesireItemRecord> desireItemRecords;

    @Builder
    public WeeklyBossRecord(Long settlementId, Long userId, Long characterId, Long bossId, 
                           LocalDate weekStartDate, BigInteger crystalIncome, Integer partySize) {
        this.settlementId = settlementId;
        this.userId = userId;
        this.characterId = characterId;
        this.bossId = bossId;
        this.weekStartDate = weekStartDate;
        this.crystalIncome = crystalIncome;
        this.partySize = partySize != null ? partySize : 1;
        this.desireItemIncome = BigInteger.ZERO;
        this.totalIncome = crystalIncome;
    }
    
    // 도메인 로직 메서드 (불변 계산)
    public BigInteger calculateTotalIncome() {
        return crystalIncome.add(desireItemIncome != null ? desireItemIncome : BigInteger.ZERO);
    }
    
    public BigInteger calculateDesireItemIncome() {
        if (desireItemRecords == null || desireItemRecords.isEmpty()) {
            return BigInteger.ZERO;
        }
        return desireItemRecords.stream()
                .map(DesireItemRecord::getSalePrice)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    // 결정석 판매 제한 검증 메서드
    public static int getCharacterCrystalCount(List<WeeklyBossRecord> records, Long characterId) {
        if (records == null || records.isEmpty()) {
            return 0;
        }
        return (int) records.stream()
                .filter(record -> record.getCharacterId().equals(characterId))
                .count();
    }

    public static boolean isCharacterOverCrystalLimit(List<WeeklyBossRecord> records, Long characterId) {
        return getCharacterCrystalCount(records, characterId) >= CHARACTER_CRYSTAL_LIMIT;
    }

    public static boolean isWorldOverCrystalLimit(List<WeeklyBossRecord> records) {
        return records != null && records.size() >= WORLD_CRYSTAL_LIMIT;
    }

    public static Map<Long, Integer> getCharacterCrystalCounts(List<WeeklyBossRecord> records) {
        if (records == null || records.isEmpty()) {
            return Map.of();
        }
        return records.stream()
                .collect(Collectors.groupingBy(
                    WeeklyBossRecord::getCharacterId,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.size()
                    )
                ));
    }
}