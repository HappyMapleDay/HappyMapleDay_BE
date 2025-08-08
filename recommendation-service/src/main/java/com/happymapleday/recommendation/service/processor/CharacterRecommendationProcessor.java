package com.happymapleday.recommendation.service.processor;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.exception.BossDataException;
import com.happymapleday.recommendation.service.common.BossDataFetcher;
import com.happymapleday.recommendation.service.common.BossFilterUtils;
import com.happymapleday.recommendation.service.factory.BossRecommendationFactory;
import com.happymapleday.recommendation.service.limiter.CrystalLimitManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CharacterRecommendationProcessor {
    
    private final BossDataFetcher bossDataFetcher;
    private final BossRecommendationFactory bossRecommendationFactory;
    private final CrystalLimitManager crystalLimitManager;
    
    // 캐릭터별 추천 생성
    public CharacterRecommendation createCharacterRecommendation(CharacterBossSelection selection, int currentGlobalCrystalCount) {
        List<BossSelection> bossSelections = selection.getBossSelections();
        
        // 보스 정보 조회
        List<BossResponse> allBosses = bossDataFetcher.fetchAllBosses();
        Map<Long, BossResponse> bossInfoMap = bossDataFetcher.createBossInfoMap(allBosses);
        
        // 1. 보스 필터링 및 정렬
        List<BossSelection> filteredBossSelections = BossFilterUtils.filterUniqueHighestProfitBosses(bossSelections, bossInfoMap);
        List<BossSelection> partyBosses = BossFilterUtils.filterPartyBossSelections(filteredBossSelections);
        List<BossSelection> soloBosses = BossFilterUtils.sortSoloBossSelectionsByProfit(filteredBossSelections, bossInfoMap);
        
        // 2. 최고 난이도 솔로 보스 찾기
        Long highestDifficultySoloBossId = BossFilterUtils.findHighestDifficultySoloBossId(soloBosses, bossInfoMap);
        
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
            
            BossResponse bossInfo = bossInfoMap.get(boss.getBossId());
            if (bossInfo == null) {
                throw new BossDataException("보스 ID " + boss.getBossId() + "에 해당하는 정보를 찾을 수 없습니다.");
            }
            
            BossRecommendation recommendation = bossRecommendationFactory.createBossRecommendation(boss, bossInfo, boss.isPartyBoss(), false, true);
            recommendations.add(recommendation);
            totalIncome = totalIncome.add(BigInteger.valueOf(bossInfo.getCrystalPrice()));
            crystalCount++;
            currentGlobalCrystalCount++;
        }
        
        // 솔로 보스 수익 기준 최적화
        for (BossSelection boss : soloBosses) {
            if (crystalLimitManager.isCharacterLimitReached(crystalCount) || 
                crystalLimitManager.isWorldLimitReached(currentGlobalCrystalCount)) {
                break;
            }
            
            BossResponse bossInfo = bossInfoMap.get(boss.getBossId());
            if (bossInfo == null) {
                throw new BossDataException("보스 ID " + boss.getBossId() + "에 해당하는 정보를 찾을 수 없습니다.");
            }
            
            boolean isHighestDifficulty = boss.getBossId().equals(highestDifficultySoloBossId);
            BossRecommendation recommendation = bossRecommendationFactory.createBossRecommendation(boss, bossInfo, boss.isPartyBoss(), isHighestDifficulty, true);
            recommendations.add(recommendation);
            totalIncome = totalIncome.add(BigInteger.valueOf(bossInfo.getCrystalPrice()));
            crystalCount++;
            currentGlobalCrystalCount++;
        }
        
        // 4. 파티 보스 ID 목록 생성
        List<Long> partyBossIds = BossFilterUtils.extractPartyBossIds(partyBosses);
        
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