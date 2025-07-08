package com.happymapleday.settlement.dto.response;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class SettlementModifyResponse {
    
    private Long settlementId;
    private BigInteger updatedTotalIncome;
    private BigInteger updatedDesireItemIncome;
    private LocalDateTime modifiedAt;
    
    // 기본 생성자
    public SettlementModifyResponse() {}
    
    // 생성자
    public SettlementModifyResponse(Long settlementId, BigInteger updatedTotalIncome,
                                  BigInteger updatedDesireItemIncome, LocalDateTime modifiedAt) {
        this.settlementId = settlementId;
        this.updatedTotalIncome = updatedTotalIncome;
        this.updatedDesireItemIncome = updatedDesireItemIncome;
        this.modifiedAt = modifiedAt;
    }
    
    // Getter/Setter
    public Long getSettlementId() {
        return settlementId;
    }
    
    public void setSettlementId(Long settlementId) {
        this.settlementId = settlementId;
    }
    
    public BigInteger getUpdatedTotalIncome() {
        return updatedTotalIncome;
    }
    
    public void setUpdatedTotalIncome(BigInteger updatedTotalIncome) {
        this.updatedTotalIncome = updatedTotalIncome;
    }
    
    public BigInteger getUpdatedDesireItemIncome() {
        return updatedDesireItemIncome;
    }
    
    public void setUpdatedDesireItemIncome(BigInteger updatedDesireItemIncome) {
        this.updatedDesireItemIncome = updatedDesireItemIncome;
    }
    
    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }
    
    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
} 