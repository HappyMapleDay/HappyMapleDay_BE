package com.happymapleday.settlement.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SettlementStatusResponse {
    private final Boolean isCompleted;
    private final Long settlementId;
    private final LocalDate weekStartDate;
    private final LocalDateTime completedAt;
    private final BigInteger totalIncome;
    private final List<BossRecordDetailResponse> bossRecords;
    private final String message;
} 