package com.happymapleday.recommendation.service.orchestrator;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.exception.OptimizationException;
import com.happymapleday.recommendation.service.optimization.BossDataProcessor;
import com.happymapleday.recommendation.service.optimization.CharacterCapabilityAnalyzer;
import com.happymapleday.recommendation.service.optimization.GlobalOptimizationEngine;
import com.happymapleday.recommendation.service.optimization.RecommendationAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OptimizationOrchestrator {

    private final BossDataProcessor bossDataProcessor;
    private final CharacterCapabilityAnalyzer characterCapabilityAnalyzer;
    private final GlobalOptimizationEngine globalOptimizationEngine;
    private final RecommendationAssembler recommendationAssembler;

    public List<CharacterRecommendation> optimizeGlobalRecommendations(List<CharacterBossSelection> characterBossSelections) {
        try {
            List<BossResponse> allBosses = bossDataProcessor.getAllBosses();

            Map<Long, List<BossResponse>> characterClearableBosses = characterCapabilityAnalyzer
                    .createCharacterClearableBosses(characterBossSelections, allBosses);

            Map<Long, Long> characterHighestDifficultySoloBossIds = characterCapabilityAnalyzer
                    .calculateCharacterHighestDifficultySoloBossIds(characterBossSelections, characterClearableBosses);

            Map<Long, List<BossRecommendation>> characterRecommendations = globalOptimizationEngine
                    .findOptimalCombination(characterBossSelections, characterClearableBosses, characterHighestDifficultySoloBossIds);

            return recommendationAssembler.createCharacterRecommendations(
                    characterBossSelections, characterRecommendations, characterHighestDifficultySoloBossIds);

        } catch (Exception e) {
            log.error("최적화 중 예상치 못한 오류 발생", e);
            throw new OptimizationException("보스 추천 최적화 중 오류가 발생했습니다.", e);
        }
    }
}


