package com.happymapleday.boss.service.impl;

import com.happymapleday.boss.dto.response.BossResponse;
import com.happymapleday.boss.repository.BossRepository;
import com.happymapleday.boss.service.BossService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BossServiceImpl implements BossService {

    private final BossRepository bossRepository;

    // 모든 활성화된 보스 조회
    @Override
    @Cacheable(cacheNames = "bossList")
    public List<BossResponse> getAllActiveBosses() {
        return bossRepository.findByIsActiveTrueWithBossDropItemsOrderByCrystalPriceDesc()
                .stream()
                .map(BossResponse::fromWithDesireItems)
                .toList();
    }
} 