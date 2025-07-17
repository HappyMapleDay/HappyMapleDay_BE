package com.happymapleday.recommendation.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigInteger;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossRecommendation {
    private Long bossId;
    private String bossName;
    private String difficulty;
    private Long crystalPrice;
    private Integer partySize;
    private BigInteger expectedIncome;
    private boolean isPartyBoss;
    private boolean isHighestDifficultySolo;
    private boolean isIncludedInOptimization;
} 