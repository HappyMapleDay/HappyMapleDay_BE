package com.happymapleday.recommendation.service.optimization;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.service.common.BossDataFetcher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BossDataProcessor {
    
    private final BossDataFetcher bossDataFetcher;
    
    // 전체 보스 목록 가져오기
    public List<BossResponse> getAllBosses() {
        return bossDataFetcher.fetchAllBosses();
    }
    
    // 가장 어려운 보스 찾기 (결정석 가격이 가장 높은 보스)
    public Long findHighestDifficultySoloBoss(List<BossResponse> allBosses) {
        return allBosses.stream()
                .max(Comparator.comparingLong(BossResponse::getCrystalPrice))
                .map(BossResponse::getId)
                .orElse(null);
    }
}