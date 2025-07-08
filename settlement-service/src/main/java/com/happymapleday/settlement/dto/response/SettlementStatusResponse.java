package com.happymapleday.settlement.dto.response;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SettlementStatusResponse {
    
    private Boolean isCompleted;
    private Long settlementId;
    private LocalDate weekStartDate;
    private LocalDateTime completedAt;
    private BigInteger totalIncome;
    private List<BossRecordDetailResponse> bossRecords;
    private String message;
    
    // 기본 생성자
    public SettlementStatusResponse() {}
    
    // 완료된 정산용 생성자
    public SettlementStatusResponse(Boolean isCompleted, Long settlementId, LocalDate weekStartDate,
                                  LocalDateTime completedAt, BigInteger totalIncome,
                                  List<BossRecordDetailResponse> bossRecords) {
        this.isCompleted = isCompleted;
        this.settlementId = settlementId;
        this.weekStartDate = weekStartDate;
        this.completedAt = completedAt;
        this.totalIncome = totalIncome;
        this.bossRecords = bossRecords;
    }
    
    // 미완료 정산용 생성자
    public SettlementStatusResponse(Boolean isCompleted, LocalDate weekStartDate, String message) {
        this.isCompleted = isCompleted;
        this.weekStartDate = weekStartDate;
        this.message = message;
    }
    
    // Getter/Setter
    public Boolean getIsCompleted() {
        return isCompleted;
    }
    
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    
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
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public BigInteger getTotalIncome() {
        return totalIncome;
    }
    
    public void setTotalIncome(BigInteger totalIncome) {
        this.totalIncome = totalIncome;
    }
    
    public List<BossRecordDetailResponse> getBossRecords() {
        return bossRecords;
    }
    
    public void setBossRecords(List<BossRecordDetailResponse> bossRecords) {
        this.bossRecords = bossRecords;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
} 