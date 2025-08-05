package com.happymapleday.recommendation.service.optimization;

import com.happymapleday.common.client.BossServiceClient;
import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BossDataProcessor {
    
    private final BossServiceClient bossServiceClient;
    
    // 전체 보스 목록 가져오기
    public List<BossResponse> getAllBosses() {
        try {
            ApiResponse<List<BossResponse>> response = bossServiceClient.getBossList();
            if (!response.getStatus().equals("success") || response.getData() == null) {
                throw new RuntimeException("보스 정보를 가져올 수 없습니다.");
            }
            return response.getData();
        } catch (Exception e) {
            log.error("보스 정보 조회 중 오류 발생", e);
            throw new RuntimeException("보스 정보를 가져올 수 없습니다.", e);
        }
    }
    
    // 솔로 보스 중 가장 어려운 보스 찾기 (결정석 가격이 가장 높은 솔로 보스)
    public Long findHighestDifficultySoloBoss(List<BossResponse> allBosses) {
        return allBosses.stream()
                .filter(BossResponse::isSoloBoss)
                .max(Comparator.comparingLong(BossResponse::getCrystalPrice))
                .map(BossResponse::getId)
                .orElse(null);
    }
} 