package com.happymapleday.recommendation.service.limiter;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.service.constants.OptimizationConstants;
import com.happymapleday.recommendation.service.optimization.BossDataProcessor;
import com.happymapleday.recommendation.service.optimization.CharacterCapabilityAnalyzer;
import com.happymapleday.recommendation.service.optimization.GlobalOptimizationEngine;
import com.happymapleday.recommendation.service.optimization.RecommendationAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrystalLimitManager {
    
    private final BossDataProcessor bossDataProcessor;
    private final CharacterCapabilityAnalyzer characterCapabilityAnalyzer;
    private final GlobalOptimizationEngine globalOptimizationEngine;
    private final RecommendationAssembler recommendationAssembler;
    
    // 90개 제한 내에서 가장 수익이 높은 조합 찾기
    public List<CharacterRecommendation> optimizeGlobalRecommendations(List<CharacterBossSelection> characterBossSelections) {
        try {
            // 1. 전체 보스 목록 가져오기
            List<BossResponse> allBosses = bossDataProcessor.getAllBosses();
            
            // 2. 솔로 보스 중 가장 어려운 보스 찾기 (결정석 가격이 가장 높은 솔로 보스)
            Long highestDifficultySoloBossId = bossDataProcessor.findHighestDifficultySoloBoss(allBosses);
            
            // 3. 각 캐릭터가 클리어할 수 있는 보스 목록 생성
            Map<Long, List<BossResponse>> characterClearableBosses = characterCapabilityAnalyzer
                    .createCharacterClearableBosses(characterBossSelections, allBosses, highestDifficultySoloBossId);
            
            // 4. 캐릭터별 가장 어려운 솔로 보스 ID 계산
            Map<Long, Long> characterHighestDifficultySoloBossIds = characterCapabilityAnalyzer
                    .calculateCharacterHighestDifficultySoloBossIds(characterBossSelections, characterClearableBosses);
            
            // 5. 최적화된 보스 조합 찾기
            Map<Long, List<BossRecommendation>> characterRecommendations = globalOptimizationEngine
                    .findOptimalCombination(characterBossSelections, characterClearableBosses, characterHighestDifficultySoloBossIds);
            
            // 6. CharacterRecommendation 객체들 생성
            return recommendationAssembler.createCharacterRecommendations(
                    characterBossSelections, characterRecommendations, characterHighestDifficultySoloBossIds);
                    
        } catch (Exception e) {
            log.error("최적화 중 오류 발생", e);
            return fallbackToOriginalLogic(characterBossSelections);
        }
    }

    
    // 기본 로직으로 폴백
    private List<CharacterRecommendation> fallbackToOriginalLogic(List<CharacterBossSelection> characterBossSelections) {
        return characterBossSelections.stream()
                .map(selection -> CharacterRecommendation.builder()
                        .characterId(selection.getCharacterId())
                        .characterName(selection.getCharacterName())
                        .characterLevel(selection.getCharacterLevel())
                        .crystalCount(0)
                        .expectedIncome(BigInteger.ZERO)
                        .bossRecommendations(new ArrayList<>())
                        .highestDifficultySoloBossId(null)
                        .partyBossIds(new ArrayList<>())
                        .build())
                .collect(Collectors.toList());
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