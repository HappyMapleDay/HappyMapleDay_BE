package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.response.DesireItemResponse;
import com.happymapleday.boss.repository.BossDropItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BossDropItemService {

    private final BossDropItemRepository bossDropItemRepository;

    // 특정 보스의 모든 드랍 아이템 조회
    public List<DesireItemResponse> getDropItemsByBossId(Long bossId) {
        return bossDropItemRepository.findByBossIdWithRandomBoxItems(bossId)
                .stream()
                .map(DesireItemResponse::fromBossDropItem)
                .toList();
    }
} 