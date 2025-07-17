package com.happymapleday.recommendation.service.limiter;

import com.happymapleday.common.client.BossServiceClient;
import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.service.constants.OptimizationConstants;
import com.happymapleday.recommendation.service.factory.BossRecommendationFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrystalLimitManager {
    
    private final BossServiceClient bossServiceClient;
    private final BossRecommendationFactory bossRecommendationFactory;
    
    // 90개 제한 내에서 가장 수익이 높은 조합 찾기
    public List<CharacterRecommendation> optimizeGlobalRecommendations(List<CharacterBossSelection> characterBossSelections) {
        try {
            // 1. 전체 보스 목록 가져오기
            ApiResponse<List<BossResponse>> response = bossServiceClient.getBossList();
            if (!response.getStatus().equals("success") || response.getData() == null) {
                log.error("보스 목록 조회 실패");
                return fallbackToOriginalLogic(characterBossSelections);
            }
            
            List<BossResponse> allBosses = response.getData();
            
            // 2. 솔로 보스 중 가장 어려운 보스 찾기 (결정석 가격이 가장 높은 솔로 보스)
            Long highestDifficultySoloBossId = findHighestDifficultySoloBoss(allBosses);
            
            // 3. 각 캐릭터가 클리어할 수 있는 보스 목록 생성
            Map<Long, List<BossResponse>> characterClearableBosses = createCharacterClearableBosses(
                    characterBossSelections, allBosses, highestDifficultySoloBossId);
            
            // 4. 최적화된 보스 조합 찾기
            return findOptimalCombination(characterBossSelections, characterClearableBosses, highestDifficultySoloBossId);
                    
        } catch (Exception e) {
            log.error("최적화 중 오류 발생, 기본 로직으로 폴백", e);
            return fallbackToOriginalLogic(characterBossSelections);
        }
    }
    
    // 솔로 보스 중 가장 어려운 보스 찾기
    private Long findHighestDifficultySoloBoss(List<BossResponse> allBosses) {
        return allBosses.stream()
                .filter(BossResponse::isSoloBoss)
                .max(Comparator.comparingLong(BossResponse::getCrystalPrice))
                .map(BossResponse::getId)
                .orElse(null);
    }
    
    // 각 캐릭터가 클리어할 수 있는 보스 목록 생성
    private Map<Long, List<BossResponse>> createCharacterClearableBosses(
            List<CharacterBossSelection> characterBossSelections, 
            List<BossResponse> allBosses, 
            Long highestDifficultySoloBossId) {
        
        Map<Long, List<BossResponse>> characterClearableBosses = new HashMap<>();
        
        for (CharacterBossSelection selection : characterBossSelections) {
            List<BossResponse> clearableBosses = new ArrayList<>();
            
            // 1. 사용자가 요청한 파티 보스들 (2명 이상으로 클리어한다고 작성한 것들)
            Set<Long> requestedPartyBossIds = selection.getBossSelections().stream()
                    .filter(BossSelection::isPartyBoss)
                    .map(BossSelection::getBossId)
                    .collect(Collectors.toSet());
            
            // 요청한 파티 보스들을 클리어 가능 목록에 추가
            clearableBosses.addAll(allBosses.stream()
                    .filter(boss -> requestedPartyBossIds.contains(boss.getId()))
                    .collect(Collectors.toList()));
            
            // 2. 솔로 보스는 가장 어려운 보스보다 결정석 가격이 낮거나 같은 것만 클리어 가능
            if (highestDifficultySoloBossId != null) {
                BossResponse highestDifficultySoloBoss = allBosses.stream()
                        .filter(boss -> boss.getId().equals(highestDifficultySoloBossId))
                        .findFirst()
                        .orElse(null);
                
                if (highestDifficultySoloBoss != null) {
                    clearableBosses.addAll(allBosses.stream()
                            .filter(BossResponse::isSoloBoss)
                            .filter(boss -> boss.getCrystalPrice() <= highestDifficultySoloBoss.getCrystalPrice())
                            .collect(Collectors.toList()));
                }
            }
            
            characterClearableBosses.put(selection.getCharacterId(), clearableBosses);
        }
        
        return characterClearableBosses;
    }
    
    // 최적화된 보스 조합 찾기
    private List<CharacterRecommendation> findOptimalCombination(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossResponse>> characterClearableBosses,
            Long highestDifficultySoloBossId) {
        
        Map<Long, List<BossRecommendation>> characterRecommendations = new HashMap<>();
        Set<Long> usedBossIds = new HashSet<>();
        
        // 1. 파티 보스 우선 배정 (사용자가 요청한 것들)
        for (CharacterBossSelection selection : characterBossSelections) {
            for (BossSelection requestedBoss : selection.getBossSelections()) {
                if (requestedBoss.isPartyBoss()) {
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
                                bossSelection, targetBoss, true, false, true);
                        
                        characterRecommendations.computeIfAbsent(selection.getCharacterId(), k -> new ArrayList<>())
                                .add(recommendation);
                        
                        usedBossIds.add(targetBoss.getId());
                    }
                }
            }
        }
        
        // 2. 남은 슬롯에 수익이 높은 솔로 보스 배정
        List<BossCandidate> candidates = new ArrayList<>();
        for (CharacterBossSelection selection : characterBossSelections) {
            List<BossResponse> clearableBosses = characterClearableBosses.get(selection.getCharacterId());
            
            for (BossResponse boss : clearableBosses) {
                if (boss.isSoloBoss() && !usedBossIds.contains(boss.getId())) {
                    candidates.add(new BossCandidate(boss, selection.getCharacterId()));
                }
            }
        }
        
        // 수익 기준으로 정렬 (높은 순)
        candidates.sort(Comparator.comparingLong(c -> c.getBoss().getCrystalPrice()));
        Collections.reverse(candidates);
        
        // 이미 배정된 파티 보스 개수 계산
        int totalAssigned = (int) usedBossIds.size();
        
        // 90개 제한 내에서 수익이 높은 순으로 배정
        for (BossCandidate candidate : candidates) {
            if (totalAssigned >= OptimizationConstants.WORLD_CRYSTAL_LIMIT) {
                break;
            }
            
            Long characterId = candidate.getCharacterId();
            List<BossRecommendation> characterRecs = characterRecommendations.get(characterId);
            
            // 캐릭터별 12개 제한 확인 (필수는 아니지만 참고)
            if (characterRecs != null && characterRecs.size() >= OptimizationConstants.CHARACTER_CRYSTAL_LIMIT) {
                continue;
            }
            
            // BossSelection으로 변환
            BossSelection bossSelection = BossSelection.builder()
                    .bossId(candidate.getBoss().getId())
                    .partySize(1) // 솔로 보스
                    .build();
            
            BossRecommendation recommendation = bossRecommendationFactory.createBossRecommendation(
                    bossSelection, candidate.getBoss(),
                    false, 
                    candidate.getBoss().getId().equals(highestDifficultySoloBossId), 
                    true);
            
            characterRecommendations.computeIfAbsent(characterId, k -> new ArrayList<>())
                    .add(recommendation);
            
            usedBossIds.add(candidate.getBoss().getId());
            totalAssigned++;
        }
        
        // 3. CharacterRecommendation 객체 생성
        return createCharacterRecommendations(characterBossSelections, characterRecommendations, highestDifficultySoloBossId);
    }
    
    // CharacterRecommendation 객체들 생성
    private List<CharacterRecommendation> createCharacterRecommendations(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossRecommendation>> characterRecommendations,
            Long highestDifficultySoloBossId) {
        
        List<CharacterRecommendation> results = new ArrayList<>();
        
        for (CharacterBossSelection selection : characterBossSelections) {
            List<BossRecommendation> recommendations = characterRecommendations.getOrDefault(
                    selection.getCharacterId(), new ArrayList<>());
            
            BigInteger totalIncome = recommendations.stream()
                    .map(BossRecommendation::getExpectedIncome)
                    .reduce(BigInteger.ZERO, BigInteger::add);
            
            List<Long> partyBossIds = recommendations.stream()
                    .filter(BossRecommendation::isPartyBoss)
                    .map(BossRecommendation::getBossId)
                    .collect(Collectors.toList());
            
            CharacterRecommendation characterRec = CharacterRecommendation.builder()
                    .characterId(selection.getCharacterId())
                    .characterName(selection.getCharacterName())
                    .characterLevel(selection.getCharacterLevel())
                    .crystalCount(recommendations.size())
                    .expectedIncome(totalIncome)
                    .bossRecommendations(recommendations)
                    .highestDifficultySoloBossId(highestDifficultySoloBossId)
                    .partyBossIds(partyBossIds)
                    .build();
            
            results.add(characterRec);
        }
        
        return results;
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