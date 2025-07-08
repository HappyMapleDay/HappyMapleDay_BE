package com.happymapleday.settlement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SettlementModifyRequest {
    @NotEmpty(message = "수정할 보스 기록은 최소 1개 이상 필요합니다.")
    @Valid
    private final List<BossRecordModifyRequest> bossRecords;
} 