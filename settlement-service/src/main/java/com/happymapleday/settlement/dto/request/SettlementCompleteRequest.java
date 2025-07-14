package com.happymapleday.settlement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class SettlementCompleteRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다.")
    @Positive(message = "사용자 ID는 양수여야 합니다.")
    private final Long userId;
    
    @NotBlank(message = "월드명은 필수입니다.")
    @Size(max = 20, message = "월드명은 20자 이하여야 합니다.")
    private final String worldName;
    
    @NotNull(message = "정산 기준일은 필수입니다.")
    private final LocalDate settlementDate;
    
    @NotEmpty(message = "보스 기록은 최소 1개 이상 필요합니다.")
    @Valid
    private final List<BossRecordRequest> bossRecords;
} 