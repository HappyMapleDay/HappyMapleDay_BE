package com.happymapleday.settlement.dto.response;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SettlementCompleteResponse {
    
    private Long settlementId;
    private LocalDate weekStartDate;
    private BigInteger totalCrystalIncome;
    private BigInteger totalDesireItemIncome;
    private BigInteger totalIncome;
    private Integer totalBossCount;
    private Integer characterCount;
    private LocalDateTime finalizedAt;
    
    // 기본 생성자
    public SettlementCompleteResponse() {}
    
    // 생성자
    public SettlementCompleteResponse(Long settlementId, LocalDate weekStartDate,
                                    BigInteger totalCrystalIncome, BigInteger totalDesireItemIncome,
                                    BigInteger totalIncome, Integer totalBossCount,
                                    Integer characterCount, LocalDateTime finalizedAt) {
        this.settlementId = settlementId;
        this.weekStartDate = weekStartDate;
        this.totalCrystalIncome = totalCrystalIncome;
        this.totalDesireItemIncome = totalDesireItemIncome;
        this.totalIncome = totalIncome;
        this.totalBossCount = totalBossCount;
        this.characterCount = characterCount;
        this.finalizedAt = finalizedAt;
    }
    
    // Getter/Setter
    public Long getSettlementId() {
        return settlementId;
    }
    
    public void setSettlementId(Long settlementId) {
        this.settlementId = settlementId;
    }
    
    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }
    
    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
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
    
    public LocalDateTime getFinalizedAt() {
        return finalizedAt;
    }
    
    public void setFinalizedAt(LocalDateTime finalizedAt) {
        this.finalizedAt = finalizedAt;
    }
} 