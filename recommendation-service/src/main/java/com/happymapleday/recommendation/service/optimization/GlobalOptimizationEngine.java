package com.happymapleday.recommendation.service.optimization;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.service.constants.OptimizationConstants;
import com.happymapleday.recommendation.service.factory.BossRecommendationFactory;
import com.happymapleday.recommendation.service.limiter.BossCandidate;
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
        Set<Long> usedBossIds = new HashSet<>();
        // 각 캐릭터별로 사용된 보스 이름을 추적 (같은 보스 이름의 다른 난이도 방지)
        Map<Long, Set<String>> characterUsedBossNames = new HashMap<>();
        
        // 1. 파티 보스 우선 배정
        assignPartyBosses(characterBossSelections, characterClearableBosses, 
                characterRecommendations, usedBossIds, characterUsedBossNames);
        
        // 2. 남은 슬롯에 수익이 높은 보스 배정
        assignOptimalSoloBosses(characterBossSelections, characterClearableBosses, 
                characterHighestDifficultySoloBossIds, characterRecommendations, 
                usedBossIds, characterUsedBossNames);
        
        return characterRecommendations;
    }
    
    // 파티 보스 우선 배정
    private void assignPartyBosses(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossResponse>> characterClearableBosses,
            Map<Long, List<BossRecommendation>> characterRecommendations,
            Set<Long> usedBossIds,
            Map<Long, Set<String>> characterUsedBossNames) {
        
        for (CharacterBossSelection selection : characterBossSelections) {
            for (BossSelection requestedBoss : selection.getBossSelections()) {
                if (requestedBoss.getPartySize() > 1) {
                    List<BossResponse> clearableBosses = characterClearableBosses.get(selection.getCharacterId());
                    BossResponse targetBoss = clearableBosses.stream()
                            .filter(boss -> boss.getId().equals(requestedBoss.getBossId()))
                            .findFirst()
                            .orElse(null);
                    
                    if (targetBoss != null) {
                        // BossSelection으로 변환
                        BossSelection bossSelection = BossSelection.builder()
                                .bossId(targetBoss.getId())
                                .partySize(requestedBoss.getPartySize())
                                .build();
                        
                        BossRecommendation recommendation = bossRecommendationFactory.createBossRecommendation(
                                bossSelection, targetBoss, requestedBoss.getPartySize() > 1, false, true);
                        
                        characterRecommendations.computeIfAbsent(selection.getCharacterId(), k -> new ArrayList<>())
                                .add(recommendation);
                        
                        usedBossIds.add(targetBoss.getId());
                        
                        // 해당 캐릭터가 사용한 보스 이름 추가
                        characterUsedBossNames.computeIfAbsent(selection.getCharacterId(), k -> new HashSet<>())
                                .add(targetBoss.getBossName());
                    }
                }
            }
        }
    }
    
    // 수익이 높은 솔로 보스 배정
    private void assignOptimalSoloBosses(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossResponse>> characterClearableBosses,
            Map<Long, Long> characterHighestDifficultySoloBossIds,
            Map<Long, List<BossRecommendation>> characterRecommendations,
            Set<Long> usedBossIds,
            Map<Long, Set<String>> characterUsedBossNames) {
        
        // 후보 보스들 생성
        List<BossCandidate> candidates = createBossCandidates(
                characterBossSelections, characterClearableBosses, usedBossIds);
        
        // 수익 기준으로 정렬 (높은 순)
        candidates.sort(Comparator.comparingLong(c -> c.getBoss().getCrystalPrice()));
        Collections.reverse(candidates);
        
        // 이미 배정된 파티 보스 개수 계산
        int totalAssigned = usedBossIds.size();
        
        // 90개 제한 내에서 수익이 높은 순으로 배정
        for (BossCandidate candidate : candidates) {
            if (totalAssigned >= OptimizationConstants.WORLD_CRYSTAL_LIMIT) {
                break;
            }
            
            Long characterId = candidate.getCharacterId();
            List<BossRecommendation> characterRecs = characterRecommendations.get(characterId);
            
            // 캐릭터별 12개 제한 확인
            if (characterRecs != null && characterRecs.size() >= OptimizationConstants.CHARACTER_CRYSTAL_LIMIT) {
                continue;
            }
            
            // 해당 캐릭터가 이미 같은 보스 이름의 다른 난이도를 가지고 있는지 확인
            Set<String> usedBossNames = characterUsedBossNames.getOrDefault(characterId, new HashSet<>());
            if (usedBossNames.contains(candidate.getBoss().getBossName())) {
                log.debug("캐릭터 {} - 보스 {} 건너뜀 (이미 같은 이름의 다른 난이도 보스 보유)", 
                        characterId, candidate.getBoss().getBossName());
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
                    false, // 솔로로 가는 것으로 간주
                    candidate.getBoss().getId().equals(characterHighestDifficultySoloBossId), 
                    true);
            
            characterRecommendations.computeIfAbsent(characterId, k -> new ArrayList<>())
                    .add(recommendation);
            
            usedBossIds.add(candidate.getBoss().getId());
            
            // 해당 캐릭터가 사용한 보스 이름 추가
            characterUsedBossNames.computeIfAbsent(characterId, k -> new HashSet<>())
                    .add(candidate.getBoss().getBossName());
            
            totalAssigned++;
        }
    }
    
    // 보스 후보들 생성
    private List<BossCandidate> createBossCandidates(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossResponse>> characterClearableBosses,
            Set<Long> usedBossIds) {
        
        List<BossCandidate> candidates = new ArrayList<>();
        
        for (CharacterBossSelection selection : characterBossSelections) {
            List<BossResponse> clearableBosses = characterClearableBosses.get(selection.getCharacterId());
            
            for (BossResponse boss : clearableBosses) {
                if (!usedBossIds.contains(boss.getId())) {
                    candidates.add(new BossCandidate(boss, selection.getCharacterId()));
                }
            }
        }
        
        return candidates;
    }
} 