package com.happymapleday.recommendation.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptimizationSummary {
    
    private boolean isOptimized;
    private String optimizationMessage;
    private Integer totalPartyBossCount;
    private Integer totalSoloBossCount;
    private Integer charactersWithMaxCrystal; // 12개 결정석 모두 사용한 캐릭터 수
    private boolean isWorldCrystalLimitReached; // 90개 제한 달성 여부
} 