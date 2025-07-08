package com.happymapleday.settlement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class SettlementModifyRequest {
    
    @NotEmpty(message = "수정할 보스 기록은 최소 1개 이상 필요합니다.")
    @Valid
    private List<BossRecordModifyRequest> bossRecords;
    
    // 기본 생성자
    public SettlementModifyRequest() {}
    
    // 생성자
    public SettlementModifyRequest(List<BossRecordModifyRequest> bossRecords) {
        this.bossRecords = bossRecords;
    }
    
    // Getter/Setter
    public List<BossRecordModifyRequest> getBossRecords() {
        return bossRecords;
    }
    
    public void setBossRecords(List<BossRecordModifyRequest> bossRecords) {
        this.bossRecords = bossRecords;
    }
} 