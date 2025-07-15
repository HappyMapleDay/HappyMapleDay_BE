package com.happymapleday.settlement.dto.response;

import com.happymapleday.settlement.entity.WeeklySettlement;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class SettlementDetailResponse {
    // 기본 정산 정보
    private final Long settlementId;
    private final Long userId;
    private final String worldName;
    private final LocalDate weekStartDate;
    private final BigInteger totalCrystalIncome;
    private final BigInteger totalDesireItemIncome;
    private final BigInteger totalIncome;
    private final Integer totalBossCount;
    private final Integer characterCount;
    private final Map<Long, Integer> characterCrystalCounts;
    
    // 상세 정보
    private final List<BossRecordDetailResponse> bossRecords;
    
    public static SettlementDetailResponse from(WeeklySettlement settlement, List<BossRecordDetailResponse> bossRecords) {
        return SettlementDetailResponse.builder()
                .settlementId(settlement.getId())
                .userId(settlement.getUserId())
                .worldName(settlement.getWorldName())
                .weekStartDate(settlement.getWeekStartDate())
                .totalCrystalIncome(settlement.getTotalCrystalIncome())
                .totalDesireItemIncome(settlement.getTotalDesireItemIncome())
                .totalIncome(settlement.getTotalIncome())
                .totalBossCount(settlement.getTotalBossCount())
                .characterCount(settlement.getCharacterCount())
                .characterCrystalCounts(settlement.getCharacterCrystalCounts())
                .bossRecords(bossRecords)
                .build();
    }
} 