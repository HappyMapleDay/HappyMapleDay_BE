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
            log.info("전체 최적화 시작");
            
            // 1. 전체 보스 목록 가져오기
            ApiResponse<List<BossResponse>> response = bossServiceClient.getBossList();
            if (!response.getStatus().equals("success") || response.getData() == null) {
                log.error("보스 목록 조회 실패: status={}, data={}", response.getStatus(), response.getData());
                return fallbackToOriginalLogic(characterBossSelections);
            }
            
            log.info("보스 목록 조회 성공: {} 개", response.getData().size());
            
            List<BossResponse> allBosses = response.getData();
            
            // 2. 솔로 보스 중 가장 어려운 보스 찾기 (결정석 가격이 가장 높은 솔로 보스)
            Long highestDifficultySoloBossId = findHighestDifficultySoloBoss(allBosses);
            
            // 3. 각 캐릭터가 클리어할 수 있는 보스 목록 생성
            Map<Long, List<BossResponse>> characterClearableBosses = createCharacterClearableBosses(
                    characterBossSelections, allBosses, highestDifficultySoloBossId);
            
            log.info("캐릭터별 클리어 가능 보스 개수: {}", 
                    characterClearableBosses.entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue().size()
                            ))
            );
            
            // 4. 최적화된 보스 조합 찾기
            List<CharacterRecommendation> result = findOptimalCombination(characterBossSelections, characterClearableBosses, highestDifficultySoloBossId);
            
            log.info("최적화 결과: 캐릭터별 크리스탈 개수 {}",
                    result.stream()
                            .collect(Collectors.toMap(
                                    CharacterRecommendation::getCharacterName,
                                    CharacterRecommendation::getCrystalCount
                            ))
            );
            
            return result;
                    
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
            Long globalHighestDifficultySoloBossId) {
        
        Map<Long, List<BossResponse>> characterClearableBosses = new HashMap<>();
        
        for (CharacterBossSelection selection : characterBossSelections) {
            log.info("캐릭터 {} 처리 시작: 선택한 보스 개수 {}", 
                    selection.getCharacterName(), selection.getBossSelections().size());
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
            
            // 2. 각 캐릭터가 선택한 솔로 보스들 중에서 가장 어려운 보스 찾기 (partySize == 1)
            log.info("캐릭터 {} 전체 선택 보스 partySize 확인:", selection.getCharacterName());
            selection.getBossSelections().forEach(boss -> 
                log.info("  - 보스 ID: {}, partySize: {}, isPartyBoss: {}", 
                    boss.getBossId(), boss.getPartySize(), boss.isPartyBoss()));
            
            List<BossSelection> soloBossSelections = selection.getBossSelections().stream()
                    .filter(boss -> boss.getPartySize() == 1) // 솔로로 가기로 선택한 보스들
                    .collect(Collectors.toList());
            
            log.info("캐릭터 {} 솔로로 가기로 선택한 보스 개수: {}", selection.getCharacterName(), soloBossSelections.size());
            
            // 단계별로 확인
            List<Long> selectedBossIds = soloBossSelections.stream()
                    .map(BossSelection::getBossId)
                    .collect(Collectors.toList());
            log.info("캐릭터 {} 선택한 보스 ID들: {}", selection.getCharacterName(), selectedBossIds);
            
            List<BossResponse> foundBosses = selectedBossIds.stream()
                    .map(bossId -> allBosses.stream()
                            .filter(boss -> boss.getId().equals(bossId))
                            .findFirst()
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            log.info("캐릭터 {} 찾은 보스 개수: {}", selection.getCharacterName(), foundBosses.size());
            
            // 솔로로 가기로 선택한 보스들의 실제 정보 가져오기
            List<BossResponse> soloBossResponses = foundBosses.stream()
                    .filter(bossResponse -> soloBossSelections.stream()
                            .anyMatch(soloBossSelection -> soloBossSelection.getBossId().equals(bossResponse.getId())))
                    .collect(Collectors.toList());
            
            log.info("캐릭터 {} 솔로로 가기로 선택한 보스 정보:", selection.getCharacterName());
            soloBossResponses.forEach(boss -> 
                log.info("  - 보스 ID: {}, 이름: {}, 가격: {}", 
                    boss.getId(), boss.getBossName(), boss.getCrystalPrice()));
            
            Long characterHighestDifficultySoloBossId = soloBossResponses.stream()
                    .max(Comparator.comparingLong(BossResponse::getCrystalPrice))
                    .map(BossResponse::getId)
                    .orElse(null);
            
            log.info("캐릭터 {} 가장 어려운 솔로 보스 ID: {}", selection.getCharacterName(), characterHighestDifficultySoloBossId);
            
            // 3. 모든 보스는 클리어 가능하되, 솔로 보스는 해당 캐릭터의 가장 어려운 솔로 보스보다 결정석 가격이 낮거나 같은 것만 클리어 가능
            if (characterHighestDifficultySoloBossId != null) {
                BossResponse characterHighestDifficultySoloBoss = allBosses.stream()
                        .filter(boss -> boss.getId().equals(characterHighestDifficultySoloBossId))
                        .findFirst()
                        .orElse(null);
                
                if (characterHighestDifficultySoloBoss != null) {
                    List<BossResponse> availableBosses = allBosses.stream()
                            .filter(boss -> boss.getCrystalPrice() <= characterHighestDifficultySoloBoss.getCrystalPrice())
                            .collect(Collectors.toList());
                    
                    clearableBosses.addAll(availableBosses);
                    
                    log.info("캐릭터 {} 클리어 가능 보스 개수: {} (기준 가격: {})", 
                            selection.getCharacterName(), 
                            availableBosses.size(), 
                            characterHighestDifficultySoloBoss.getCrystalPrice());
                }
            } else {
                // 솔로로 가기로 선택한 보스가 없는 경우, 파티로 가기로 선택한 보스들만 클리어 가능
                log.info("캐릭터 {} 솔로로 가기로 선택한 보스 없음", selection.getCharacterName());
            }
            
            log.info("캐릭터 {} 총 클리어 가능 보스 개수: {}", selection.getCharacterName(), clearableBosses.size());
            characterClearableBosses.put(selection.getCharacterId(), clearableBosses);
        }
        
        return characterClearableBosses;
    }
    
    // 최적화된 보스 조합 찾기
    private List<CharacterRecommendation> findOptimalCombination(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossResponse>> characterClearableBosses,
            Long globalHighestDifficultySoloBossId) {
        
        Map<Long, List<BossRecommendation>> characterRecommendations = new HashMap<>();
        Set<Long> usedBossIds = new HashSet<>();
        // 각 캐릭터별로 사용된 보스 이름을 추적 (같은 보스 이름의 다른 난이도 방지)
        Map<Long, Set<String>> characterUsedBossNames = new HashMap<>();
        
        // 각 캐릭터별 가장 어려운 솔로 보스 계산
        Map<Long, Long> characterHighestDifficultySoloBossIds = new HashMap<>();
        for (CharacterBossSelection selection : characterBossSelections) {
            Long characterHighestDifficultySoloBossId = selection.getBossSelections().stream()
                    .filter(boss -> !boss.isPartyBoss()) // 솔로 보스만
                    .map(BossSelection::getBossId)
                    .map(bossId -> characterClearableBosses.get(selection.getCharacterId()).stream()
                            .filter(boss -> boss.getId().equals(bossId))
                            .findFirst()
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .filter(BossResponse::isSoloBoss)
                    .max(Comparator.comparingLong(BossResponse::getCrystalPrice))
                    .map(BossResponse::getId)
                    .orElse(null);
            
            characterHighestDifficultySoloBossIds.put(selection.getCharacterId(), characterHighestDifficultySoloBossId);
        }
        
        // 1. 파티 보스 우선 배정 (사용자가 파티로 가기로 선택한 보스들)
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
        
        // 2. 남은 슬롯에 수익이 높은 보스 배정 (솔로로 가는 것으로 간주)
        List<BossCandidate> candidates = new ArrayList<>();
        for (CharacterBossSelection selection : characterBossSelections) {
            List<BossResponse> clearableBosses = characterClearableBosses.get(selection.getCharacterId());
            
            for (BossResponse boss : clearableBosses) {
                if (!usedBossIds.contains(boss.getId())) {
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
            
            // 해당 캐릭터가 이미 같은 보스 이름의 다른 난이도를 가지고 있는지 확인
            Set<String> usedBossNames = characterUsedBossNames.getOrDefault(characterId, new HashSet<>());
            if (usedBossNames.contains(candidate.getBoss().getBossName())) {
                log.debug("캐릭터 {} - 보스 {} 건너뜀 (이미 같은 이름의 다른 난이도 보스 보유)", 
                        characterId, candidate.getBoss().getBossName());
                continue; // 같은 보스 이름의 다른 난이도가 이미 배정되어 있으므로 건너뜀
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
        
        // 3. CharacterRecommendation 객체 생성
        return createCharacterRecommendations(characterBossSelections, characterRecommendations, characterHighestDifficultySoloBossIds);
    }
    
    // CharacterRecommendation 객체들 생성
    private List<CharacterRecommendation> createCharacterRecommendations(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossRecommendation>> characterRecommendations,
            Map<Long, Long> characterHighestDifficultySoloBossIds) {
        
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
            
            // 해당 캐릭터의 가장 어려운 솔로 보스 ID 사용
            Long characterHighestDifficultySoloBossId = characterHighestDifficultySoloBossIds.get(selection.getCharacterId());
            
            CharacterRecommendation characterRec = CharacterRecommendation.builder()
                    .characterId(selection.getCharacterId())
                    .characterName(selection.getCharacterName())
                    .characterLevel(selection.getCharacterLevel())
                    .crystalCount(recommendations.size())
                    .expectedIncome(totalIncome)
                    .bossRecommendations(recommendations)
                    .highestDifficultySoloBossId(characterHighestDifficultySoloBossId)
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