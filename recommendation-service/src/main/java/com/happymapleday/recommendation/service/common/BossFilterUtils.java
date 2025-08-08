package com.happymapleday.recommendation.service.common;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;

import java.util.*;
import java.util.stream.Collectors;

public class BossFilterUtils {
    
    private BossFilterUtils() {
        // 유틸리티 클래스
    }
    
    // 주의: BossResponse로는 솔로/파티 구분 불가 (사용자 선택에 따라 결정됨)
    // 솔로/파티 구분은 BossSelection의 partySize를 사용해야 함
    
    // (미사용 메서드 제거)
    
    // 특정 결정석 가격 이하의 보스들만 필터링
    public static List<BossResponse> filterByMaxPrice(List<BossResponse> bosses, Long maxPrice) {
        return bosses.stream()
                .filter(boss -> boss.getCrystalPrice() <= maxPrice)
                .collect(Collectors.toList());
    }
    
    // 솔로 보스 선택만 필터링
    public static List<BossSelection> filterSoloBossSelections(List<BossSelection> bossSelections) {
        return bossSelections.stream()
                .filter(BossSelection::isSoloBoss)
                .collect(Collectors.toList());
    }
    
    // 파티 보스 선택만 필터링
    public static List<BossSelection> filterPartyBossSelections(List<BossSelection> bossSelections) {
        return bossSelections.stream()
                .filter(BossSelection::isPartyBoss)
                .collect(Collectors.toList());
    }
    
    // 같은 보스 이름에서 가장 높은 수익을 가진 난이도만 선택
    public static List<BossSelection> filterUniqueHighestProfitBosses(
            List<BossSelection> bossSelections, Map<Long, BossResponse> bossInfoMap) {
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
    
    // 솔로 보스 선택을 수익 기준으로 정렬
    public static List<BossSelection> sortSoloBossSelectionsByProfit(
            List<BossSelection> bossSelections, Map<Long, BossResponse> bossInfoMap) {
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
    public static Long findHighestDifficultySoloBossId(
            List<BossSelection> soloBosses, Map<Long, BossResponse> bossInfoMap) {
        if (soloBosses.isEmpty()) return null;
        
        return soloBosses.stream()
                .map(boss -> {
                    BossResponse bossInfo = bossInfoMap.get(boss.getBossId());
                    return new AbstractMap.SimpleEntry<>(boss.getBossId(), 
                            bossInfo != null ? bossInfo.getCrystalPrice() : 0L);
                })
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
    
    // 파티 보스 ID 목록 추출
    public static List<Long> extractPartyBossIds(List<BossSelection> partyBosses) {
        return partyBosses.stream()
                .map(BossSelection::getBossId)
                .collect(Collectors.toList());
    }
} 