package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.response.BossPresetResponse;

import java.util.List;

public interface BossPresetService {
    
    // 보스 정보를 포함한 모든 프리셋 조회
    List<BossPresetResponse> getAllPresetsWithBosses();
} 