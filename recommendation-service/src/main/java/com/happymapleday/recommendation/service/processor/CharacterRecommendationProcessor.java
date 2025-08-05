package com.happymapleday.recommendation.service.processor;

import com.happymapleday.common.client.BossServiceClient;
import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.service.factory.BossRecommendationFactory;
import com.happymapleday.recommendation.service.limiter.CrystalLimitManager;
import com.happymapleday.recommendation.service.optimizer.BossSelectionOptimizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CharacterRecommendationProcessor {
    
    private final BossSelectionOptimizer bossSelectionOptimizer;
    private final BossRecommendationFactory bossRecommendationFactory;
    private final CrystalLimitManager crystalLimitManager;
    private final BossServiceClient bossServiceClient;
    
    // 캐릭터별 추천 생성
    public CharacterRecommendation createCharacterRecommendation(CharacterBossSelection selection, int currentGlobalCrystalCount) {
        List<BossSelection> bossSelections = selection.getBossSelections();
        
        // 보스 정보 조회
        ApiResponse<List<BossResponse>> response = bossServiceClient.getBossList();
        if (!"success".equals(response.getStatus()) || response.getData() == null) {
            log.error("보스 목록 조회 실패");
            throw new RuntimeException("보스 정보를 가져올 수 없습니다.");
        }
        
        // 보스 ID로 빠른 조회를 위한 Map 생성
        Map<Long, BossResponse> bossInfoMap = response.getData().stream()
                .collect(Collectors.toMap(BossResponse::getId, boss -> boss));
        
        // 1. 보스 필터링 및 정렬
        List<BossSelection> filteredBossSelections = bossSelectionOptimizer.filterUniqueHighestProfitBosses(bossSelections, bossInfoMap);
        List<BossSelection> partyBosses = bossSelectionOptimizer.filterPartyBosses(filteredBossSelections);
        List<BossSelection> soloBosses = bossSelectionOptimizer.filterAndSortSoloBosses(filteredBossSelections, bossInfoMap);
        
        // 2. 최고 난이도 솔로 보스 찾기
        Long highestDifficultySoloBossId = bossSelectionOptimizer.findHighestDifficultySoloBossId(soloBosses, bossInfoMap);
        
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
                log.warn("보스 정보를 찾을 수 없음: {}", boss.getBossId());
                continue;
            }
            
            BossRecommendation recommendation = bossRecommendationFactory.createBossRecommendation(boss, bossInfo, true, false, true);
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
                log.warn("보스 정보를 찾을 수 없음: {}", boss.getBossId());
                continue;
            }
            
            boolean isHighestDifficulty = boss.getBossId().equals(highestDifficultySoloBossId);
            BossRecommendation recommendation = bossRecommendationFactory.createBossRecommendation(boss, bossInfo, false, isHighestDifficulty, true);
            recommendations.add(recommendation);
            totalIncome = totalIncome.add(BigInteger.valueOf(bossInfo.getCrystalPrice()));
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