package com.happymapleday.recommendation.service.impl;

import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.OptimizationSummary;
import com.happymapleday.recommendation.service.OptimizationService;
import com.happymapleday.recommendation.service.calculator.OptimizationSummaryCalculator;
import com.happymapleday.recommendation.service.limiter.CrystalLimitManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimizationServiceImpl implements OptimizationService {
    
    private final CrystalLimitManager crystalLimitManager;
    private final OptimizationSummaryCalculator optimizationSummaryCalculator;
    
    // 각 캐릭터별 제약 조건을 고려한 최적화된 추천 결과 생성
    @Override
    public List<CharacterRecommendation> optimizeRecommendations(List<CharacterBossSelection> characterBossSelections) {
        if (characterBossSelections.isEmpty()) {
            return List.of();
        }

        // 전체 최적화 로직 사용
        return crystalLimitManager.optimizeGlobalRecommendations(characterBossSelections);
    }
    
    // 최적화 요약 생성
    @Override
    public OptimizationSummary createOptimizationSummary(List<CharacterRecommendation> recommendations) {
        return optimizationSummaryCalculator.createOptimizationSummary(recommendations);
    }
} 