package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.response.BossResponse;

import java.util.List;

public interface BossService {
    
    // 모든 활성화된 보스 조회
    List<BossResponse> getAllActiveBosses();

    // ID 목록으로 특정 보스들 조회 (드랍 아이템 포함)
    List<BossResponse> getBossesByIds(List<Long> bossIds);
} 