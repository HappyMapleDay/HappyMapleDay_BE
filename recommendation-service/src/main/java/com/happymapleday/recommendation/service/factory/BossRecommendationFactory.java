package com.happymapleday.recommendation.service.factory;

import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class BossRecommendationFactory {
    // 보스 추천 생성
    public BossRecommendation createBossRecommendation(BossSelection boss, boolean isPartyBoss, 
                                                      boolean isHighestDifficultySolo, boolean isIncluded) {
        return BossRecommendation.builder()
                .bossId(boss.getBossId())
                .bossName(boss.getBossName())
                .difficulty(boss.getDifficulty())
                .crystalPrice(boss.getCrystalPrice())
                .partySize(boss.getPartySize())
                .expectedIncome(BigInteger.valueOf(boss.getCrystalPrice()))
                .isPartyBoss(isPartyBoss)
                .isHighestDifficultySolo(isHighestDifficultySolo)
                .isIncludedInOptimization(isIncluded)
                .build();
    }
} 