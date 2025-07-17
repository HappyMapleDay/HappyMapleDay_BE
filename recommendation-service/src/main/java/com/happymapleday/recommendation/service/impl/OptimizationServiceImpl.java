package com.happymapleday.recommendation.service.impl;

import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.OptimizationSummary;
import com.happymapleday.recommendation.service.OptimizationService;
import com.happymapleday.recommendation.service.calculator.OptimizationSummaryCalculator;
import com.happymapleday.recommendation.service.limiter.CrystalLimitManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptimizationServiceImpl implements OptimizationService {
    
    private final CrystalLimitManager crystalLimitManager;
    private final OptimizationSummaryCalculator optimizationSummaryCalculator;
    
    // 캐릭터별 보스 선택을 기반으로 최적화된 추천 결과를 생성
    @Override
    public List<CharacterRecommendation> optimizeRecommendations(List<CharacterBossSelection> characterBossSelections) {
        // 새로운 글로벌 최적화 로직 사용
        return crystalLimitManager.optimizeGlobalRecommendations(characterBossSelections);
    }
    
    // 최적화 요약 생성
    @Override
    public OptimizationSummary createOptimizationSummary(List<CharacterRecommendation> recommendations) {
        return optimizationSummaryCalculator.createOptimizationSummary(recommendations);
    }
} 