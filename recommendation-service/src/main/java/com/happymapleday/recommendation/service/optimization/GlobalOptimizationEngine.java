package com.happymapleday.recommendation.service.optimization;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.service.constants.OptimizationConstants;
import com.happymapleday.recommendation.service.factory.BossRecommendationFactory;
import com.happymapleday.recommendation.domain.BossCandidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j  
public class GlobalOptimizationEngine {
    
    private final BossRecommendationFactory bossRecommendationFactory;
    
    // 최적화된 보스 조합 찾기
    public Map<Long, List<BossRecommendation>> findOptimalCombination(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossResponse>> characterClearableBosses,
            Map<Long, Long> characterHighestDifficultySoloBossIds) {

        Map<Long, List<BossRecommendation>> characterRecommendations = new HashMap<>();
        // 각 캐릭터별로 사용된 보스 이름을 추적 (같은 보스 이름의 다른 난이도 방지)
        Map<Long, Set<String>> characterUsedBossNames = new HashMap<>();

        int totalAssigned = 0; // 세계(월드) 단위로 배정된 총 추천 개수

        // 0. 클리어한 보스 우선 고정 배정 (제한 적용)
        totalAssigned += assignClearedBosses(characterBossSelections, characterClearableBosses,
                characterRecommendations, characterUsedBossNames, totalAssigned);

        // 1. 파티 보스 우선 배정 (클리어하지 않은 것만, 제한 적용)
        totalAssigned += assignPartyBosses(characterBossSelections, characterClearableBosses,
                characterRecommendations, characterUsedBossNames, totalAssigned);

        // 2. 남은 슬롯에 수익이 높은 솔로 보스 배정 (제한 적용)
        totalAssigned = assignOptimalSoloBosses(characterBossSelections, characterClearableBosses,
                characterHighestDifficultySoloBossIds, characterRecommendations,
                characterUsedBossNames, totalAssigned);

        return characterRecommendations;
    }
    
    // 클리어한 보스 고정 배정
    private int assignClearedBosses(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossResponse>> characterClearableBosses,
            Map<Long, List<BossRecommendation>> characterRecommendations,
            Map<Long, Set<String>> characterUsedBossNames,
            int totalAssigned) {

        int assigned = 0;

        for (CharacterBossSelection selection : characterBossSelections) {
            for (BossSelection requestedBoss : selection.getBossSelections()) {
                if (totalAssigned + assigned >= OptimizationConstants.WORLD_CRYSTAL_LIMIT) {
                    return assigned;
                }
                // 클리어한 보스만 처리
                if (requestedBoss.isCleared()) {
                    List<BossResponse> clearableBosses = characterClearableBosses.get(selection.getCharacterId());
                    BossResponse targetBoss = clearableBosses.stream()
                            .filter(boss -> boss.getId().equals(requestedBoss.getBossId()))
                            .findFirst()
                            .orElse(null);

                    if (targetBoss != null) {
                        // 캐릭터별 결정석 제한 확인
                        List<BossRecommendation> characterRecs = characterRecommendations
                                .computeIfAbsent(selection.getCharacterId(), k -> new ArrayList<>());
                        if (characterRecs.size() >= OptimizationConstants.CHARACTER_CRYSTAL_LIMIT) {
                            continue;
                        }

                        // 같은 이름의 다른 난이도 금지
                        Set<String> usedNames = characterUsedBossNames
                                .computeIfAbsent(selection.getCharacterId(), k -> new HashSet<>());
                        if (usedNames.contains(targetBoss.getBossName())) {
                            continue;
                        }

                        // 클리어된 보스로 BossSelection 생성
                        BossSelection clearedBossSelection = BossSelection.builder()
                                .bossId(targetBoss.getId())
                                .partySize(requestedBoss.getPartySize())
                                .isCleared(true)
                                .build();

                        BossRecommendation recommendation = bossRecommendationFactory.createBossRecommendation(
                                clearedBossSelection, targetBoss, clearedBossSelection.isPartyBoss(), false, true);

                        characterRecs.add(recommendation);

                        // 해당 캐릭터가 사용한 보스 이름 추가
                        usedNames.add(targetBoss.getBossName());

                        assigned++;

                        log.debug("클리어한 보스 고정 배정: 캐릭터 ID {}, 보스 {}",
                                selection.getCharacterId(), targetBoss.getBossName());
                    }
                }
            }
        }

        return assigned;
    }
    
    // 파티 보스 우선 배정
    /**
     * 도메인 규칙 적용: 파티 보스 우선 배정(아직 클리어하지 않은 것)
     * - 캐릭터별 12개, 세계 90개 제한 적용
     * - 같은 캐릭터의 같은 이름 다른 난이도 금지
     */
    private int assignPartyBosses(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossResponse>> characterClearableBosses,
            Map<Long, List<BossRecommendation>> characterRecommendations,
            Map<Long, Set<String>> characterUsedBossNames,
            int totalAssigned) {

        int assigned = 0;

        for (CharacterBossSelection selection : characterBossSelections) {
            for (BossSelection requestedBoss : selection.getBossSelections()) {
                if (totalAssigned + assigned >= OptimizationConstants.WORLD_CRYSTAL_LIMIT) {
                    return assigned;
                }
                // 파티 보스이면서 아직 클리어하지 않은 보스만 처리
                if (requestedBoss.getPartySize() > 1 && !requestedBoss.isCleared()) {
                    List<BossResponse> clearableBosses = characterClearableBosses.get(selection.getCharacterId());
                    BossResponse targetBoss = clearableBosses.stream()
                            .filter(boss -> boss.getId().equals(requestedBoss.getBossId()))
                            .findFirst()
                            .orElse(null);

                    if (targetBoss != null) {
                        // 캐릭터별 결정석 제한 확인
                        List<BossRecommendation> characterRecs = characterRecommendations
                                .computeIfAbsent(selection.getCharacterId(), k -> new ArrayList<>());
                        if (characterRecs.size() >= OptimizationConstants.CHARACTER_CRYSTAL_LIMIT) {
                            continue;
                        }

                        // 같은 이름의 다른 난이도 금지
                        Set<String> usedNames = characterUsedBossNames
                                .computeIfAbsent(selection.getCharacterId(), k -> new HashSet<>());
                        if (usedNames.contains(targetBoss.getBossName())) {
                            continue;
                        }

                        // BossSelection으로 변환
                        BossSelection bossSelection = BossSelection.builder()
                                .bossId(targetBoss.getId())
                                .partySize(requestedBoss.getPartySize())
                                .isCleared(false)
                                .build();

                        BossRecommendation recommendation = bossRecommendationFactory.createBossRecommendation(
                                bossSelection, targetBoss, bossSelection.isPartyBoss(), false, true);

                        characterRecs.add(recommendation);

                        // 해당 캐릭터가 사용한 보스 이름 추가
                        usedNames.add(targetBoss.getBossName());

                        assigned++;
                    }
                }
            }
        }

        return assigned;
    }
    
    // 수익이 높은 솔로 보스 배정
    /**
     * 도메인 규칙 적용: 남은 슬롯에 수익이 높은 솔로 보스 배정
     * - 캐릭터별 12개, 세계 90개 제한 적용
     * - 같은 캐릭터의 같은 이름 다른 난이도 금지
     * - 서로 다른 캐릭터는 같은 보스 배정 가능
     */
    private int assignOptimalSoloBosses(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossResponse>> characterClearableBosses,
            Map<Long, Long> characterHighestDifficultySoloBossIds,
            Map<Long, List<BossRecommendation>> characterRecommendations,
            Map<Long, Set<String>> characterUsedBossNames,
            int totalAssigned) {

        // 후보 보스들 생성 (전역 중복 금지 제거: usedBossIds 미사용)
        List<BossCandidate> candidates = createBossCandidates(
                characterBossSelections, characterClearableBosses);

        // 수익 기준으로 정렬 (높은 순)
        candidates.sort(Comparator.comparingLong(BossCandidate::getCrystalPrice));
        Collections.reverse(candidates);

        // 90개 제한 내에서 수익이 높은 순으로 배정
        for (BossCandidate candidate : candidates) {
            if (totalAssigned >= OptimizationConstants.WORLD_CRYSTAL_LIMIT) {
                break;
            }

            Long characterId = candidate.getCharacterId();
            List<BossRecommendation> characterRecs = characterRecommendations
                    .computeIfAbsent(characterId, k -> new ArrayList<>());

            // 캐릭터별 12개 제한 확인
            if (characterRecs.size() >= OptimizationConstants.CHARACTER_CRYSTAL_LIMIT) {
                continue;
            }

            // 해당 캐릭터가 이미 같은 보스 이름의 다른 난이도를 가지고 있는지 확인
            Set<String> usedBossNames = characterUsedBossNames
                    .computeIfAbsent(characterId, k -> new HashSet<>());
            if (usedBossNames.contains(candidate.getBossName())) {
                log.debug("캐릭터 {} - 보스 {} 건너뜀 (이미 같은 이름의 다른 난이도 보스 보유)",
                        characterId, candidate.getBossName());
                continue;
            }

            // BossSelection으로 변환
            BossSelection bossSelection = BossSelection.builder()
                    .bossId(candidate.getBoss().getId())
                    .partySize(1) // 솔로 보스
                    .build();

            // 해당 캐릭터의 가장 어려운 솔로 보스 ID 사용
            Long characterHighestDifficultySoloBossId = characterHighestDifficultySoloBossIds.get(characterId);
            BossRecommendation recommendation = bossRecommendationFactory.createBossRecommendation(
                    bossSelection, candidate.getBoss(),
                    bossSelection.isPartyBoss(),
                    candidate.getBoss().getId().equals(characterHighestDifficultySoloBossId),
                    true);

            characterRecs.add(recommendation);

            // 해당 캐릭터가 사용한 보스 이름 추가
            usedBossNames.add(candidate.getBossName());

            totalAssigned++;
        }

        return totalAssigned;
    }
    
    // 보스 후보들 생성 (클리어하지 않은 솔로 보스만)
    private List<BossCandidate> createBossCandidates(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossResponse>> characterClearableBosses) {
        
        List<BossCandidate> candidates = new ArrayList<>();
        
        for (CharacterBossSelection selection : characterBossSelections) {
            List<BossResponse> clearableBosses = characterClearableBosses.get(selection.getCharacterId());
            
            // 해당 캐릭터가 선택한 솔로 보스들 중에서 클리어하지 않은 것만 추출
            Set<Long> unclearedSoloBossIds = selection.getBossSelections().stream()
                    .filter(boss -> boss.getPartySize() == 1 && !boss.isCleared()) // 솔로 보스이면서 클리어하지 않은 것
                    .map(BossSelection::getBossId)
                    .collect(Collectors.toSet());
            
            for (BossResponse boss : clearableBosses) {
                // 솔로 보스이면서, 클리어하지 않은 보스만 후보로 추가 (전역 중복 미차단)
                if (unclearedSoloBossIds.contains(boss.getId())) {
                    candidates.add(new BossCandidate(boss, selection.getCharacterId()));
                }
            }
        }
        
        return candidates;
    }
} 