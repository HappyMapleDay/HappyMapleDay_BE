package com.happymapleday.recommendation.domain;

import com.happymapleday.recommendation.dto.response.BossRecommendation;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class OptimizationResult {
    
    private final Map<Long, List<BossRecommendation>> characterRecommendations;
    private final Set<Long> usedBossIds;
    private final Map<Long, Set<String>> characterUsedBossNames;
    private final int totalAssignedCount;
    private final boolean isWorldLimitReached;
    
    // 전체 예상 수익 계산
    public BigInteger getTotalExpectedIncome() {
        return characterRecommendations.values().stream()
                .flatMap(List::stream)
                .map(BossRecommendation::getExpectedIncome)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }
    
    // 전체 결정석 개수
    public int getTotalCrystalCount() {
        return characterRecommendations.values().stream()
                .mapToInt(List::size)
                .sum();
    }
    
    // 특정 캐릭터의 추천 보스 개수
    public int getCharacterCrystalCount(Long characterId) {
        return characterRecommendations.getOrDefault(characterId, List.of()).size();
    }
    
    // 특정 캐릭터가 사용한 보스 이름들
    public Set<String> getCharacterUsedBossNames(Long characterId) {
        return characterUsedBossNames.getOrDefault(characterId, Set.of());
    }
} 