package com.happymapleday.recommendation.service.optimizer;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BossSelectionOptimizer {
    // 같은 보스 이름에서 가장 높은 수익을 가진 난이도만 선택
    public List<BossSelection> filterUniqueHighestProfitBosses(List<BossSelection> bossSelections, Map<Long, BossResponse> bossInfoMap) {
        Map<String, BossSelection> uniqueBossMap = new HashMap<>();
        
        for (BossSelection boss : bossSelections) {
            BossResponse bossInfo = bossInfoMap.get(boss.getBossId());
            if (bossInfo == null) continue;
            
            String bossName = bossInfo.getBossName();
            BossSelection existingBoss = uniqueBossMap.get(bossName);
            
            if (existingBoss == null) {
                uniqueBossMap.put(bossName, boss);
            } else {
                BossResponse existingBossInfo = bossInfoMap.get(existingBoss.getBossId());
                if (existingBossInfo != null && bossInfo.getCrystalPrice() > existingBossInfo.getCrystalPrice()) {
                    uniqueBossMap.put(bossName, boss);
                }
            }
        }
        
        return new ArrayList<>(uniqueBossMap.values());
    }
    
    // 파티 보스 필터링
    public List<BossSelection> filterPartyBosses(List<BossSelection> bossSelections) {
        return bossSelections.stream()
                .filter(BossSelection::isPartyBoss)
                .collect(Collectors.toList());
    }
    
    // 솔로 보스 필터링 및 수익 기준 정렬
    public List<BossSelection> filterAndSortSoloBosses(List<BossSelection> bossSelections, Map<Long, BossResponse> bossInfoMap) {
        return bossSelections.stream()
                .filter(BossSelection::isSoloBoss)
                .sorted((a, b) -> {
                    BossResponse aInfo = bossInfoMap.get(a.getBossId());
                    BossResponse bInfo = bossInfoMap.get(b.getBossId());
                    if (aInfo == null || bInfo == null) return 0;
                    return Long.compare(bInfo.getCrystalPrice(), aInfo.getCrystalPrice()); // 내림차순
                })
                .collect(Collectors.toList());
    }
    
    // 최고 난이도 솔로 보스 ID 찾기
    public Long findHighestDifficultySoloBossId(List<BossSelection> soloBosses, Map<Long, BossResponse> bossInfoMap) {
        if (soloBosses.isEmpty()) return null;
        
        return soloBosses.stream()
                .map(boss -> {
                    BossResponse bossInfo = bossInfoMap.get(boss.getBossId());
                    return new AbstractMap.SimpleEntry<>(boss.getBossId(), bossInfo != null ? bossInfo.getCrystalPrice() : 0L);
                })
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
    
    // 파티 보스 ID 목록 생성
    public List<Long> extractPartyBossIds(List<BossSelection> partyBosses) {
        return partyBosses.stream()
                .map(BossSelection::getBossId)
                .collect(Collectors.toList());
    }
} 