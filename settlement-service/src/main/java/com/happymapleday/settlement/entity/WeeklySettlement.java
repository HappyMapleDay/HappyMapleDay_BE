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
@Table(name = "weekly_settlements",
       uniqueConstraints = {
           @UniqueConstraint(name = "unique_weekly_settlement", 
                           columnNames = {"user_id", "world_name", "week_start_date"})
       },
       indexes = {
           @Index(name = "idx_user_week", columnList = "user_id, week_start_date")
       })
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
    private Boolean isFinalized = true;
    
    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt = LocalDateTime.now();
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 연관관계
    @OneToMany(mappedBy = "weeklySettlement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeeklyBossRecord> bossRecords;
    
    // 기본 생성자
    public WeeklySettlement() {}
    
    // 생성자
    public WeeklySettlement(Long userId, String worldName, LocalDate weekStartDate) {
        this.userId = userId;
        this.worldName = worldName;
        this.weekStartDate = weekStartDate;
    }
    
    // Getter/Setter
    public Long getId() {
        return id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }
    
    public BigInteger getTotalCrystalIncome() {
        return totalCrystalIncome;
    }
    
    public void setTotalCrystalIncome(BigInteger totalCrystalIncome) {
        this.totalCrystalIncome = totalCrystalIncome;
    }
    
    public BigInteger getTotalDesireItemIncome() {
        return totalDesireItemIncome;
    }
    
    public void setTotalDesireItemIncome(BigInteger totalDesireItemIncome) {
        this.totalDesireItemIncome = totalDesireItemIncome;
    }
    
    public BigInteger getTotalIncome() {
        return totalIncome;
    }
    
    public void setTotalIncome(BigInteger totalIncome) {
        this.totalIncome = totalIncome;
    }
    
    public Integer getTotalBossCount() {
        return totalBossCount;
    }
    
    public void setTotalBossCount(Integer totalBossCount) {
        this.totalBossCount = totalBossCount;
    }
    
    public Integer getCharacterCount() {
        return characterCount;
    }
    
    public void setCharacterCount(Integer characterCount) {
        this.characterCount = characterCount;
    }
    
    public Boolean getIsFinalized() {
        return isFinalized;
    }
    
    public void setIsFinalized(Boolean isFinalized) {
        this.isFinalized = isFinalized;
    }
    
    public LocalDateTime getFinalizedAt() {
        return finalizedAt;
    }
    
    public void setFinalizedAt(LocalDateTime finalizedAt) {
        this.finalizedAt = finalizedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public List<WeeklyBossRecord> getBossRecords() {
        return bossRecords;
    }
    
    public void setBossRecords(List<WeeklyBossRecord> bossRecords) {
        this.bossRecords = bossRecords;
    }
    
    // 비즈니스 메서드
    public void calculateTotals() {
        if (bossRecords == null || bossRecords.isEmpty()) {
            this.totalCrystalIncome = BigInteger.ZERO;
            this.totalDesireItemIncome = BigInteger.ZERO;
            this.totalIncome = BigInteger.ZERO;
            this.totalBossCount = 0;
            this.characterCount = 0;
            return;
        }
        
        this.totalCrystalIncome = bossRecords.stream()
                .map(WeeklyBossRecord::getCrystalIncome)
                .reduce(BigInteger.ZERO, BigInteger::add);
                
        this.totalDesireItemIncome = bossRecords.stream()
                .map(WeeklyBossRecord::getDesireItemIncome)
                .reduce(BigInteger.ZERO, BigInteger::add);
                
        this.totalIncome = totalCrystalIncome.add(totalDesireItemIncome);
        this.totalBossCount = bossRecords.size();
        this.characterCount = (int) bossRecords.stream()
                .map(WeeklyBossRecord::getCharacterId)
                .distinct()
                .count();
    }
} 