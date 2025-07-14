package com.happymapleday.settlement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class SettlementModifyRequest {
    @NotNull(message = "정산 기준일은 필수입니다.")
    private final LocalDate settlementDate;
    
    @NotEmpty(message = "수정할 보스 기록은 최소 1개 이상 필요합니다.")
    @Valid
    private final List<BossRecordRequest> bossRecords;
} 