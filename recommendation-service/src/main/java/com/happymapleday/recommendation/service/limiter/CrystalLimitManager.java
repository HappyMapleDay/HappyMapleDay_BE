package com.happymapleday.recommendation.service.limiter;

import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.service.constants.OptimizationConstants;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CrystalLimitManager {
    
    // 전체 제한 내에서 추가 최적화
    public void optimizeWithinGlobalLimit(List<CharacterRecommendation> recommendations, int totalCrystalCount) {
        if (totalCrystalCount >= OptimizationConstants.WORLD_CRYSTAL_LIMIT) {
            // 90개 제한 초과 시 수익 기준으로 재조정
            adjustForGlobalLimit(recommendations);
        }
    }
    
    // 전체 제한 초과 시 재조정
    private void adjustForGlobalLimit(List<CharacterRecommendation> recommendations) {
        List<BossRecommendation> allBosses = new ArrayList<>();
        Map<BossRecommendation, Long> bossToCharacterMap = new HashMap<>();
        
        for (CharacterRecommendation charRec : recommendations) {
            for (BossRecommendation bossRec : charRec.getBossRecommendations()) {
                allBosses.add(bossRec);
                bossToCharacterMap.put(bossRec, charRec.getCharacterId());
            }
        }
        
        // 파티 보스 우선, 그 다음 수익 순으로 정렬
        allBosses.sort((a, b) -> {
            if (a.isPartyBoss() && !b.isPartyBoss()) return -1;
            if (!a.isPartyBoss() && b.isPartyBoss()) return 1;
            return Long.compare(b.getCrystalPrice(), a.getCrystalPrice());
        });
        
        // 상위 90개만 유지
        Set<BossRecommendation> selectedBosses = new HashSet<>(allBosses.subList(0, Math.min(OptimizationConstants.WORLD_CRYSTAL_LIMIT, allBosses.size())));
        
        // 각 캐릭터별로 선택된 보스만 유지
        for (CharacterRecommendation charRec : recommendations) {
            List<BossRecommendation> filteredBosses = charRec.getBossRecommendations().stream()
                    .filter(selectedBosses::contains)
                    .collect(Collectors.toList());
            
            CharacterRecommendation updatedRec = CharacterRecommendation.builder()
                    .characterId(charRec.getCharacterId())
                    .characterName(charRec.getCharacterName())
                    .characterLevel(charRec.getCharacterLevel())
                    .crystalCount(filteredBosses.size())
                    .expectedIncome(filteredBosses.stream()
                            .map(BossRecommendation::getExpectedIncome)
                            .reduce(BigInteger.ZERO, BigInteger::add))
                    .bossRecommendations(filteredBosses)
                    .highestDifficultySoloBossId(charRec.getHighestDifficultySoloBossId())
                    .partyBossIds(charRec.getPartyBossIds())
                    .build();
            
            // 원본 리스트 업데이트
            int index = recommendations.indexOf(charRec);
            recommendations.set(index, updatedRec);
        }
    }
    
    // 캐릭터별 결정석 제한 체크
    public boolean isCharacterLimitReached(int currentCrystalCount) {
        return currentCrystalCount >= OptimizationConstants.CHARACTER_CRYSTAL_LIMIT;
    }
    
    // 전체 결정석 제한 체크
    public boolean isWorldLimitReached(int currentGlobalCrystalCount) {
        return currentGlobalCrystalCount >= OptimizationConstants.WORLD_CRYSTAL_LIMIT;
    }
} 