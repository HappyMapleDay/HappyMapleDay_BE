package com.happymapleday.recommendation.service.impl;

import com.happymapleday.recommendation.dto.request.RecommendationRequest;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.OptimizationSummary;
import com.happymapleday.recommendation.dto.response.RecommendationResponse;
import com.happymapleday.recommendation.service.OptimizationService;
import com.happymapleday.recommendation.service.RecommendationService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    private final OptimizationService optimizationService;
    
    public RecommendationServiceImpl(OptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }
    
    @Override
    public RecommendationResponse generateRecommendation(RecommendationRequest request) {
        // 1. 최적화 서비스를 통해 캐릭터별 추천 생성
        List<CharacterRecommendation> characterRecommendations = optimizationService.optimizeRecommendations(
                request.getCharacterBossSelections());
        
        // 2. 전체 통계 계산
        BigInteger totalExpectedIncome = calculateTotalIncome(characterRecommendations);
        Integer totalCrystalCount = calculateTotalCrystalCount(characterRecommendations);
        Integer totalBossCount = calculateTotalBossCount(characterRecommendations);
        
        // 3. 최적화 요약 생성
        OptimizationSummary optimizationSummary = optimizationService.createOptimizationSummary(characterRecommendations);
        
        // 4. 응답 생성
        return RecommendationResponse.builder()
                .userId(request.getUserId())
                .totalExpectedIncome(totalExpectedIncome)
                .totalCrystalCount(totalCrystalCount)
                .totalBossCount(totalBossCount)
                .characterRecommendations(characterRecommendations)
                .optimizationSummary(optimizationSummary)
                .build();
    }
    
    // 전체 예상 수익 계산
    private BigInteger calculateTotalIncome(List<CharacterRecommendation> recommendations) {
        return recommendations.stream()
                .map(CharacterRecommendation::getExpectedIncome)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }
    
    // 전체 결정석 개수 계산
    private Integer calculateTotalCrystalCount(List<CharacterRecommendation> recommendations) {
        return recommendations.stream()
                .mapToInt(CharacterRecommendation::getCrystalCount)
                .sum();
    }
    
    // 전체 보스 개수 계산
    private Integer calculateTotalBossCount(List<CharacterRecommendation> recommendations) {
        return recommendations.stream()
                .mapToInt(rec -> rec.getBossRecommendations().size())
                .sum();
    }
} 