package com.happymapleday.recommendation.service.processor;

import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.service.factory.BossRecommendationFactory;
import com.happymapleday.recommendation.service.limiter.CrystalLimitManager;
import com.happymapleday.recommendation.service.optimizer.BossSelectionOptimizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CharacterRecommendationProcessor {
    
    private final BossSelectionOptimizer bossSelectionOptimizer;
    private final BossRecommendationFactory bossRecommendationFactory;
    private final CrystalLimitManager crystalLimitManager;
    
    // 캐릭터별 추천 생성
    public CharacterRecommendation createCharacterRecommendation(CharacterBossSelection selection, int currentGlobalCrystalCount) {
        List<BossSelection> bossSelections = selection.getBossSelections();
        
        // 1. 보스 필터링 및 정렬
        List<BossSelection> filteredBossSelections = bossSelectionOptimizer.filterUniqueHighestProfitBosses(bossSelections);
        List<BossSelection> partyBosses = bossSelectionOptimizer.filterPartyBosses(filteredBossSelections);
        List<BossSelection> soloBosses = bossSelectionOptimizer.filterAndSortSoloBosses(filteredBossSelections);
        
        // 2. 최고 난이도 솔로 보스 찾기
        Long highestDifficultySoloBossId = bossSelectionOptimizer.findHighestDifficultySoloBossId(soloBosses);
        
        // 3. 추천 생성
        List<BossRecommendation> recommendations = new ArrayList<>();
        BigInteger totalIncome = BigInteger.ZERO;
        int crystalCount = 0;
        
        // 파티 보스 우선 포함
        for (BossSelection boss : partyBosses) {
            if (crystalLimitManager.isCharacterLimitReached(crystalCount) || 
                crystalLimitManager.isWorldLimitReached(currentGlobalCrystalCount)) {
                break;
            }
            
            BossRecommendation recommendation = bossRecommendationFactory.createBossRecommendation(boss, true, false, true);
            recommendations.add(recommendation);
            totalIncome = totalIncome.add(BigInteger.valueOf(boss.getCrystalPrice()));
            crystalCount++;
            currentGlobalCrystalCount++;
        }
        
        // 솔로 보스 수익 기준 최적화
        for (BossSelection boss : soloBosses) {
            if (crystalLimitManager.isCharacterLimitReached(crystalCount) || 
                crystalLimitManager.isWorldLimitReached(currentGlobalCrystalCount)) {
                break;
            }
            
            boolean isHighestDifficulty = boss.getBossId().equals(highestDifficultySoloBossId);
            BossRecommendation recommendation = bossRecommendationFactory.createBossRecommendation(boss, false, isHighestDifficulty, true);
            recommendations.add(recommendation);
            totalIncome = totalIncome.add(BigInteger.valueOf(boss.getCrystalPrice()));
            crystalCount++;
            currentGlobalCrystalCount++;
        }
        
        // 4. 파티 보스 ID 목록 생성
        List<Long> partyBossIds = bossSelectionOptimizer.extractPartyBossIds(partyBosses);
        
        return CharacterRecommendation.builder()
                .characterId(selection.getCharacterId())
                .characterName(selection.getCharacterName())
                .characterLevel(selection.getCharacterLevel())
                .crystalCount(crystalCount)
                .expectedIncome(totalIncome)
                .bossRecommendations(recommendations)
                .highestDifficultySoloBossId(highestDifficultySoloBossId)
                .partyBossIds(partyBossIds)
                .build();
    }
} 