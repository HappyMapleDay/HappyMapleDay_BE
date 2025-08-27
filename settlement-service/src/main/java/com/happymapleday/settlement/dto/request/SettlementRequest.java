package com.happymapleday.settlement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SettlementRequest {
    
    @NotBlank(message = "월드명은 필수입니다.")
    @Size(max = 20, message = "월드명은 20자 이하여야 합니다.")
    private final String worldName;
    
    @NotEmpty(message = "보스 기록은 최소 1개 이상 필요합니다.")
    @Valid
    private final List<BossRecordRequest> bossRecords;

    // 낙관적 락용 버전 (선택적 전송)
    private final Long version;
} 