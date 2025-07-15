package com.happymapleday.settlement.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Builder
public class SettlementCompleteResponse {
    private final Long settlementId;
    private final LocalDate weekStartDate;
    private final BigInteger totalCrystalIncome;
    private final BigInteger totalDesireItemIncome;
    private final BigInteger totalIncome;
    private final Integer totalBossCount;
    private final Integer characterCount;
} 