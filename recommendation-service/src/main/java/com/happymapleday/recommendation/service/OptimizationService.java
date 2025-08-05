package com.happymapleday.recommendation.service;

import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.OptimizationSummary;

import java.util.List;

public interface OptimizationService {
    
    // 캐릭터별 보스 선택을 기반으로 최적화된 추천 결과를 생성
    List<CharacterRecommendation> optimizeRecommendations(List<CharacterBossSelection> characterBossSelections);
    
    // 최적화 요약 생성
    OptimizationSummary createOptimizationSummary(List<CharacterRecommendation> recommendations);
} 