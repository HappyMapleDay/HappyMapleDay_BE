package com.happymapleday.settlement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
public class WeeklyBossRecord {
    
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
    
    // 기본 생성자
    public WeeklyBossRecord() {}
    
    // 생성자
    public WeeklyBossRecord(Long settlementId, Long userId, Long characterId, Long bossId, 
                           LocalDate weekStartDate, BigInteger crystalIncome, Integer partySize) {
        this.settlementId = settlementId;
        this.userId = userId;
        this.characterId = characterId;
        this.bossId = bossId;
        this.weekStartDate = weekStartDate;
        this.crystalIncome = crystalIncome;
        this.partySize = partySize;
        this.totalIncome = crystalIncome;
    }
    
    // Getter/Setter
    public Long getId() {
        return id;
    }
    
    public Long getSettlementId() {
        return settlementId;
    }
    
    public void setSettlementId(Long settlementId) {
        this.settlementId = settlementId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getCharacterId() {
        return characterId;
    }
    
    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }
    
    public Long getBossId() {
        return bossId;
    }
    
    public void setBossId(Long bossId) {
        this.bossId = bossId;
    }
    
    public Integer getPartySize() {
        return partySize;
    }
    
    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }
    
    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }
    
    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }
    
    public BigInteger getCrystalIncome() {
        return crystalIncome;
    }
    
    public void setCrystalIncome(BigInteger crystalIncome) {
        this.crystalIncome = crystalIncome;
        this.calculateTotalIncome();
    }
    
    public BigInteger getDesireItemIncome() {
        return desireItemIncome;
    }
    
    public void setDesireItemIncome(BigInteger desireItemIncome) {
        this.desireItemIncome = desireItemIncome;
        this.calculateTotalIncome();
    }
    
    public BigInteger getTotalIncome() {
        return totalIncome;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public WeeklySettlement getWeeklySettlement() {
        return weeklySettlement;
    }
    
    public void setWeeklySettlement(WeeklySettlement weeklySettlement) {
        this.weeklySettlement = weeklySettlement;
    }
    
    public List<DesireItemRecord> getDesireItemRecords() {
        return desireItemRecords;
    }
    
    public void setDesireItemRecords(List<DesireItemRecord> desireItemRecords) {
        this.desireItemRecords = desireItemRecords;
    }
    
    // 비즈니스 메서드
    public void calculateTotalIncome() {
        this.totalIncome = crystalIncome.add(desireItemIncome != null ? desireItemIncome : BigInteger.ZERO);
    }
    
    public void calculateDesireItemIncome() {
        if (desireItemRecords == null || desireItemRecords.isEmpty()) {
            this.desireItemIncome = BigInteger.ZERO;
        } else {
            this.desireItemIncome = desireItemRecords.stream()
                    .map(DesireItemRecord::getSalePrice)
                    .reduce(BigInteger.ZERO, BigInteger::add);
        }
        this.calculateTotalIncome();
    }
} 