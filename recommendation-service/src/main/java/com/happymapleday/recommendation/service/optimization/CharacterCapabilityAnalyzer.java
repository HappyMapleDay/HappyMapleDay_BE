package com.happymapleday.recommendation.service.optimization;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.service.common.BossDataFetcher;
import com.happymapleday.recommendation.service.common.BossFilterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CharacterCapabilityAnalyzer {
    
    private final BossDataFetcher bossDataFetcher;
    
    // 각 캐릭터가 클리어할 수 있는 보스 목록 생성
    public Map<Long, List<BossResponse>> createCharacterClearableBosses(
            List<CharacterBossSelection> characterBossSelections, 
            List<BossResponse> allBosses, 
            Long globalHighestDifficultySoloBossId) {
        
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
            
            // 2. 각 캐릭터가 선택한 솔로 보스들 중에서 가장 어려운 보스 찾기
            Long characterHighestDifficultySoloBossId = findCharacterHighestDifficultySoloBossId(
                    selection, allBosses);
            
            // 3. 모든 보스는 클리어 가능하되, 솔로 보스는 해당 캐릭터의 가장 어려운 솔로 보스보다 
            //    결정석 가격이 낮거나 같은 것만 클리어 가능
            if (characterHighestDifficultySoloBossId != null) {
                BossResponse characterHighestDifficultySoloBoss = allBosses.stream()
                        .filter(boss -> boss.getId().equals(characterHighestDifficultySoloBossId))
                        .findFirst()
                        .orElse(null);
                
                if (characterHighestDifficultySoloBoss != null) {
                    List<BossResponse> availableBosses = BossFilterUtils.filterByMaxPrice(
                            allBosses, characterHighestDifficultySoloBoss.getCrystalPrice());
                    
                    clearableBosses.addAll(availableBosses);
                }
            }
            
            characterClearableBosses.put(selection.getCharacterId(), clearableBosses);
        }
        
        return characterClearableBosses;
    }
    
    // 캐릭터별 가장 어려운 솔로 보스 ID 계산
    public Map<Long, Long> calculateCharacterHighestDifficultySoloBossIds(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossResponse>> characterClearableBosses) {
        
        Map<Long, Long> characterHighestDifficultySoloBossIds = new HashMap<>();
        
        for (CharacterBossSelection selection : characterBossSelections) {
            Long characterHighestDifficultySoloBossId = selection.getBossSelections().stream()
                    .filter(boss -> boss.isSoloBoss()) // 솔로로 가기로 선택한 보스만
                    .map(BossSelection::getBossId)
                    .map(bossId -> characterClearableBosses.get(selection.getCharacterId()).stream()
                            .filter(boss -> boss.getId().equals(bossId))
                            .findFirst()
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .max(Comparator.comparingLong(BossResponse::getCrystalPrice))
                    .map(BossResponse::getId)
                    .orElse(null);
            
            characterHighestDifficultySoloBossIds.put(selection.getCharacterId(), characterHighestDifficultySoloBossId);
        }
        
        return characterHighestDifficultySoloBossIds;
    }
    
    // 개별 캐릭터의 가장 어려운 솔로 보스 찾기
    private Long findCharacterHighestDifficultySoloBossId(
            CharacterBossSelection selection, List<BossResponse> allBosses) {
        
                    // 솔로로 가기로 선택한 보스들
        List<BossSelection> soloBossSelections = BossFilterUtils.filterSoloBossSelections(
                selection.getBossSelections());
        
        // 선택한 보스 ID들
        List<Long> selectedBossIds = soloBossSelections.stream()
                .map(BossSelection::getBossId)
                .collect(Collectors.toList());
        
        // 실제 보스 정보 가져오기
        List<BossResponse> foundBosses = bossDataFetcher.findBossesByIds(allBosses, selectedBossIds);
        
        // 솔로로 가기로 선택한 보스들의 실제 정보 필터링
        List<BossResponse> soloBossResponses = foundBosses.stream()
                .filter(bossResponse -> soloBossSelections.stream()
                        .anyMatch(soloBossSelection -> soloBossSelection.getBossId().equals(bossResponse.getId())))
                .collect(Collectors.toList());
        
        return soloBossResponses.stream()
                .max(Comparator.comparingLong(BossResponse::getCrystalPrice))
                .map(BossResponse::getId)
                .orElse(null);
    }
} 