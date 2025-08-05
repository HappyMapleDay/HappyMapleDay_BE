package com.happymapleday.recommendation.service.common;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BossFilterUtils {
    
    private BossFilterUtils() {
        // 유틸리티 클래스
    }
    
    // 솔로 보스만 필터링
    public static List<BossResponse> filterSoloBosses(List<BossResponse> bosses) {
        return bosses.stream()
                .filter(BossResponse::isSoloBoss)
                .collect(Collectors.toList());
    }
    
    // 파티 보스만 필터링
    public static List<BossResponse> filterPartyBosses(List<BossResponse> bosses) {
        return bosses.stream()
                .filter(boss -> !boss.isSoloBoss())
                .collect(Collectors.toList());
    }
    
    // 결정석 가격 기준으로 정렬 (높은 순)
    public static List<BossResponse> sortByProfitDesc(List<BossResponse> bosses) {
        return bosses.stream()
                .sorted(Comparator.comparingLong(BossResponse::getCrystalPrice).reversed())
                .collect(Collectors.toList());
    }
    
    // 결정석 가격 기준으로 정렬 (낮은 순)
    public static List<BossResponse> sortByProfitAsc(List<BossResponse> bosses) {
        return bosses.stream()
                .sorted(Comparator.comparingLong(BossResponse::getCrystalPrice))
                .collect(Collectors.toList());
    }
    
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
} 