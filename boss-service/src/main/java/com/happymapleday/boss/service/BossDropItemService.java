package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.response.DesireItemResponse;

import java.util.List;

public interface BossDropItemService {
    
    // 특정 보스의 모든 드랍 아이템 조회
    List<DesireItemResponse> getDropItemsByBossId(Long bossId);
} 