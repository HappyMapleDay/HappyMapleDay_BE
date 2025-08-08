package com.happymapleday.recommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizedRecommendationResponse {
    private List<WorldRecommendation> worlds;
    private Long totalCrystalIncome;
    private Integer totalBossCount;
}


