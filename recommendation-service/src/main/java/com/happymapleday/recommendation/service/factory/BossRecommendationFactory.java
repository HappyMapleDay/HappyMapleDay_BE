package com.happymapleday.recommendation.service.factory;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class BossRecommendationFactory {
    
    // 보스 추천 생성
    public BossRecommendation createBossRecommendation(BossSelection bossSelection, BossResponse bossInfo,
                                                      boolean isPartyBoss, boolean isHighestDifficultySolo, 
                                                      boolean isIncluded) {
        return BossRecommendation.builder()
                .bossId(bossSelection.getBossId())
                .bossName(bossInfo.getBossName())
                .difficulty(bossInfo.getDifficulty())
                .crystalPrice(bossInfo.getCrystalPrice())
                .partySize(bossSelection.getPartySize())
                .expectedIncome(BigInteger.valueOf(bossInfo.getCrystalPrice()))
                .isPartyBoss(isPartyBoss)
                .isHighestDifficultySolo(isHighestDifficultySolo)
                .isIncludedInOptimization(isIncluded)
                .build();
    }
} 