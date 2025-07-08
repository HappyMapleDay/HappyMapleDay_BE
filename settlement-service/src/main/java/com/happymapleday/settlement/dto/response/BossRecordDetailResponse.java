package com.happymapleday.settlement.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class BossRecordDetailResponse {
    private final String characterName;
    private final String bossName;
    private final String difficulty;
    private final Integer partySize;
    private final BigInteger crystalIncome;
    private final BigInteger desireItemIncome;
    private final BigInteger totalIncome;
} 