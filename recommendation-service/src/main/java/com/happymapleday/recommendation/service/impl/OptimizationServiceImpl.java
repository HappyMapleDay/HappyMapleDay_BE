package com.happymapleday.recommendation.service.impl;

import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.OptimizationSummary;
import com.happymapleday.recommendation.service.OptimizationService;
import com.happymapleday.recommendation.service.calculator.OptimizationSummaryCalculator;
import com.happymapleday.recommendation.service.limiter.CrystalLimitManager;
import com.happymapleday.recommendation.service.processor.CharacterRecommendationProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OptimizationServiceImpl implements OptimizationService {
    
    private final CharacterRecommendationProcessor characterRecommendationProcessor;
    private final CrystalLimitManager crystalLimitManager;
    private final OptimizationSummaryCalculator optimizationSummaryCalculator;
    
    // 캐릭터별 보스 선택을 기반으로 최적화된 추천 결과를 생성
    @Override
    public List<CharacterRecommendation> optimizeRecommendations(List<CharacterBossSelection> characterBossSelections) {
        List<CharacterRecommendation> recommendations = new ArrayList<>();
        int totalCrystalCount = 0;
        
        // 1. 각 캐릭터별로 파티 보스 우선 포함
        for (CharacterBossSelection selection : characterBossSelections) {
            CharacterRecommendation recommendation = characterRecommendationProcessor.createCharacterRecommendation(selection, totalCrystalCount);
            recommendations.add(recommendation);
            totalCrystalCount += recommendation.getCrystalCount();
        }
        
        // 2. 전체 90개 제한 내에서 추가 최적화
        crystalLimitManager.optimizeWithinGlobalLimit(recommendations, totalCrystalCount);
        
        return recommendations;
    }
    
    // 최적화 요약 생성
    @Override
    public OptimizationSummary createOptimizationSummary(List<CharacterRecommendation> recommendations) {
        return optimizationSummaryCalculator.createOptimizationSummary(recommendations);
    }
} 