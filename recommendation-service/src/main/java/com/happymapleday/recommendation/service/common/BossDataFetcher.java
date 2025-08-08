package com.happymapleday.recommendation.service.common;

import com.happymapleday.common.client.BossServiceClient;
import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.exception.BossDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BossDataFetcher {
    
    private final BossServiceClient bossServiceClient;
    
    // 전체 보스 목록 가져오기
    public List<BossResponse> fetchAllBosses() {
        try {
            ApiResponse<List<BossResponse>> response = bossServiceClient.getBossList();
            if (!response.getStatus().equals("success") || response.getData() == null) {
                throw new BossDataException("보스 서비스에서 올바른 응답을 받지 못했습니다.");
            }
            return response.getData();
        } catch (BossDataException e) {
            throw e; // 이미 처리된 예외는 다시 던짐
        } catch (Exception e) {
            log.error("보스 정보 조회 중 예상치 못한 오류 발생", e);
            throw new BossDataException("보스 정보를 가져오는 중 오류가 발생했습니다.", e);
        }
    }
    
    // 보스 ID로 빠른 조회를 위한 Map 생성
    public Map<Long, BossResponse> createBossInfoMap(List<BossResponse> bosses) {
        return bosses.stream()
                .collect(Collectors.toMap(BossResponse::getId, boss -> boss));
    }
} 