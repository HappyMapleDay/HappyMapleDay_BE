package com.happymapleday.settlement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class SettlementCompleteRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다.")
    @Positive(message = "사용자 ID는 양수여야 합니다.")
    private Long userId;
    
    @NotBlank(message = "월드명은 필수입니다.")
    @Size(max = 20, message = "월드명은 20자 이하여야 합니다.")
    private String worldName;
    
    @NotNull(message = "주차 시작일은 필수입니다.")
    private LocalDate weekStartDate;
    
    @NotEmpty(message = "보스 기록은 최소 1개 이상 필요합니다.")
    @Valid
    private List<BossRecordRequest> bossRecords;
    
    // 기본 생성자
    public SettlementCompleteRequest() {}
    
    // 생성자
    public SettlementCompleteRequest(Long userId, String worldName, LocalDate weekStartDate, 
                                   List<BossRecordRequest> bossRecords) {
        this.userId = userId;
        this.worldName = worldName;
        this.weekStartDate = weekStartDate;
        this.bossRecords = bossRecords;
    }
    
    // Getter/Setter
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
    
    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }
    
    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }
    
    public List<BossRecordRequest> getBossRecords() {
        return bossRecords;
    }
    
    public void setBossRecords(List<BossRecordRequest> bossRecords) {
        this.bossRecords = bossRecords;
    }
} 