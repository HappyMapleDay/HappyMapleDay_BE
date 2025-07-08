package com.happymapleday.settlement.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Builder
public class SettlementModifyResponse {
    private final Long settlementId;
    private final BigInteger updatedTotalIncome;
    private final BigInteger updatedDesireItemIncome;
    private final LocalDateTime modifiedAt;
} 