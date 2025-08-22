package com.happymapleday.boss.service.impl;

import com.happymapleday.boss.dto.response.BossResponse;
import com.happymapleday.boss.repository.BossRepository;
import com.happymapleday.boss.service.BossService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BossServiceImpl implements BossService {

    private final BossRepository bossRepository;

    // 모든 활성화된 보스 조회
    @Override
    public List<BossResponse> getAllActiveBosses() {
        return bossRepository.findByIsActiveTrueWithBossDropItemsOrderByCrystalPriceDesc()
                .stream()
                .map(BossResponse::fromWithDesireItems)
                .toList();
    }

    // ID 목록으로 특정 보스들 조회 (드랍 아이템 포함)
    @Override
    public List<BossResponse> getBossesByIds(List<Long> bossIds) {
        if (bossIds == null || bossIds.isEmpty()) {
            return List.of();
        }
        List<Long> distinctIds = bossIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (distinctIds.isEmpty()) {
            return List.of();
        }
        return bossRepository.findActiveByIdInWithDropItems(distinctIds)
                .stream()
                .map(BossResponse::fromWithDesireItems)
                .toList();
    }
} 