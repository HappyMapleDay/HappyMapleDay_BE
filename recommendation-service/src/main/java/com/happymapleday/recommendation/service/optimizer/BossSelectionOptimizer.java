package com.happymapleday.recommendation.service.optimizer;

import com.happymapleday.recommendation.dto.request.BossSelection;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BossSelectionOptimizer {
    
    // 같은 보스 이름에서 가장 높은 수익을 가진 난이도만 선택
    public List<BossSelection> filterUniqueHighestProfitBosses(List<BossSelection> bossSelections) {
        Map<String, BossSelection> uniqueBossMap = bossSelections.stream()
                .collect(Collectors.toMap(
                        BossSelection::getBossName,
                        boss -> boss,
                        (existing, replacement) -> existing.getCrystalPrice() >= replacement.getCrystalPrice() ? existing : replacement
                ));
        
        return new ArrayList<>(uniqueBossMap.values());
    }
    
    // 파티 보스 필터링
    public List<BossSelection> filterPartyBosses(List<BossSelection> bossSelections) {
        return bossSelections.stream()
                .filter(BossSelection::isPartyBoss)
                .collect(Collectors.toList());
    }
    
    // 솔로 보스 필터링 및 수익 기준 정렬
    public List<BossSelection> filterAndSortSoloBosses(List<BossSelection> bossSelections) {
        return bossSelections.stream()
                .filter(BossSelection::isSoloBoss)
                .sorted(Comparator.comparingLong(BossSelection::getCrystalPrice).reversed())
                .collect(Collectors.toList());
    }
    
    // 최고 난이도 솔로 보스 ID 찾기
    public Long findHighestDifficultySoloBossId(List<BossSelection> soloBosses) {
        return soloBosses.isEmpty() ? null : soloBosses.get(0).getBossId();
    }
    
    // 파티 보스 ID 목록 생성
    public List<Long> extractPartyBossIds(List<BossSelection> partyBosses) {
        return partyBosses.stream()
                .map(BossSelection::getBossId)
                .collect(Collectors.toList());
    }
} 