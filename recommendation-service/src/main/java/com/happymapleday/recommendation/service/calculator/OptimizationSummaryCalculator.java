package com.happymapleday.recommendation.service.calculator;

import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.OptimizationSummary;
import com.happymapleday.recommendation.service.constants.OptimizationConstants;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OptimizationSummaryCalculator {
    
    // 최적화 요약 생성
    public OptimizationSummary createOptimizationSummary(List<CharacterRecommendation> recommendations) {
        int totalPartyBossCount = 0;
        int totalSoloBossCount = 0;
        int charactersWithMaxCrystal = 0;
        int totalCrystalCount = 0;
        
        for (CharacterRecommendation rec : recommendations) {
            totalCrystalCount += rec.getCrystalCount();
            
            if (rec.getCrystalCount() >= OptimizationConstants.CHARACTER_CRYSTAL_LIMIT) {
                charactersWithMaxCrystal++;
            }
            
            for (BossRecommendation bossRec : rec.getBossRecommendations()) {
                if (bossRec.isPartyBoss()) {
                    totalPartyBossCount++;
                } else {
                    totalSoloBossCount++;
                }
            }
        }
        
        boolean isWorldCrystalLimitReached = totalCrystalCount >= OptimizationConstants.WORLD_CRYSTAL_LIMIT;
        String message = isWorldCrystalLimitReached ? 
                "90개 결정석 제한에 도달했습니다." : 
                "최적화가 완료되었습니다.";
        
        return OptimizationSummary.builder()
                .isOptimized(true)
                .optimizationMessage(message)
                .totalPartyBossCount(totalPartyBossCount)
                .totalSoloBossCount(totalSoloBossCount)
                .charactersWithMaxCrystal(charactersWithMaxCrystal)
                .isWorldCrystalLimitReached(isWorldCrystalLimitReached)
                .build();
    }
} 