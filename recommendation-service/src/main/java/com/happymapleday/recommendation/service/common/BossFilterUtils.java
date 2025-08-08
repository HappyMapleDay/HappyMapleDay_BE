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