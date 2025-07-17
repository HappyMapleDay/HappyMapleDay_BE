package com.happymapleday.recommendation.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponse {
    
    private Long userId;
    private BigInteger totalExpectedIncome;
    private Integer totalCrystalCount;
    private Integer totalBossCount;
    private List<CharacterRecommendation> characterRecommendations;
    private OptimizationSummary optimizationSummary;
} 