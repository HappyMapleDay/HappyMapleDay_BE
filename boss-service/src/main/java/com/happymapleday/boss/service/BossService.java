package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.response.BossResponse;

import java.util.List;

public interface BossService {
    
    // 모든 활성화된 보스 조회
    List<BossResponse> getAllActiveBosses();
} 