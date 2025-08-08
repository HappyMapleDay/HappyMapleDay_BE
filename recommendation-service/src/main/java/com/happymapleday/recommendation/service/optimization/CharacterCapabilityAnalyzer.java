package com.happymapleday.recommendation.service.optimization;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.service.common.BossDataFetcher;
import com.happymapleday.recommendation.service.common.BossFilterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CharacterCapabilityAnalyzer {
    
    private final BossDataFetcher bossDataFetcher;
    
    // 각 캐릭터가 클리어할 수 있는 보스 목록 생성
    public Map<Long, List<BossResponse>> createCharacterClearableBosses(
            List<CharacterBossSelection> characterBossSelections,
            List<BossResponse> allBosses) {
        
        Map<Long, List<BossResponse>> characterClearableBosses = new HashMap<>();
        Map<Long, BossResponse> bossInfoMap = bossDataFetcher.createBossInfoMap(allBosses);
        
        for (CharacterBossSelection selection : characterBossSelections) {
            List<BossResponse> clearableBosses = new ArrayList<>();
            
            // 1. 사용자가 요청한 파티 보스들 (2명 이상으로 클리어한다고 작성한 것들)
            Set<Long> requestedPartyBossIds = BossFilterUtils.extractPartyBossIds(
                    BossFilterUtils.filterPartyBossSelections(selection.getBossSelections()))
                    .stream().collect(Collectors.toSet());
            
            // 요청한 파티 보스들을 클리어 가능 목록에 추가
            clearableBosses.addAll(allBosses.stream()
                    .filter(boss -> requestedPartyBossIds.contains(boss.getId()))
                    .collect(Collectors.toList()));
            
            // 2. 각 캐릭터가 선택한 솔로 보스들 중에서 가장 어려운 보스 찾기
            Long characterHighestDifficultySoloBossId = BossFilterUtils.findHighestDifficultySoloBossId(
                    BossFilterUtils.filterSoloBossSelections(selection.getBossSelections()),
                    bossInfoMap);
            
            // 3. 가장 어려운 보스의 결정석 가격 보다 낮은 보스들은 모두 클리어 가능하다고 판단
            if (characterHighestDifficultySoloBossId != null) {
                BossResponse characterHighestDifficultySoloBoss = bossInfoMap.get(characterHighestDifficultySoloBossId);
                
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
            Set<Long> soloSelectedBossIds = selection.getBossSelections().stream()
                    .filter(BossSelection::isSoloBoss)
                    .map(BossSelection::getBossId)
                    .collect(Collectors.toSet());

            Map<Long, BossResponse> clearableBossMap = characterClearableBosses.getOrDefault(selection.getCharacterId(), List.of()).stream()
                    .collect(Collectors.toMap(BossResponse::getId, boss -> boss));

            Long characterHighestDifficultySoloBossId = soloSelectedBossIds.stream()
                    .map(clearableBossMap::get)
                    .filter(Objects::nonNull)
                    .max(Comparator.comparingLong(BossResponse::getCrystalPrice))
                    .map(BossResponse::getId)
                    .orElse(null);
            
            characterHighestDifficultySoloBossIds.put(selection.getCharacterId(), characterHighestDifficultySoloBossId);
        }
        
        return characterHighestDifficultySoloBossIds;
    }
    
} 