package com.happymapleday.settlement.dto.response;

import com.happymapleday.settlement.entity.WeeklySettlement;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder
public class SettlementStatusResponse {
    private final Long settlementId;
    private final Long userId;
    private final String worldName;
    private final LocalDate weekStartDate;
    private final BigInteger totalCrystalIncome;
    private final BigInteger totalDesireItemIncome;
    private final BigInteger totalIncome;
    private final Integer totalBossCount;
    private final Integer characterCount;
    private final Boolean isFinalized;
    private final Map<Long, Integer> characterCrystalCounts;

    public static SettlementStatusResponse from(WeeklySettlement settlement) {
        return SettlementStatusResponse.builder()
            .settlementId(settlement.getId())
            .userId(settlement.getUserId())
            .worldName(settlement.getWorldName())
            .weekStartDate(settlement.getWeekStartDate())
            .totalCrystalIncome(settlement.getTotalCrystalIncome())
            .totalDesireItemIncome(settlement.getTotalDesireItemIncome())
            .totalIncome(settlement.getTotalIncome())
            .totalBossCount(settlement.getTotalBossCount())
            .characterCount(settlement.getCharacterCount())
            .isFinalized(settlement.getIsFinalized())
            .characterCrystalCounts(settlement.getCharacterCrystalCounts())
            .build();
    }
} 