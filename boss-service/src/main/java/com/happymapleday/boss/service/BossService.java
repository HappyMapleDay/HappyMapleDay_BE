package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.response.BossResponse;
import com.happymapleday.boss.repository.BossRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BossService {

    private final BossRepository bossRepository;

    // 모든 활성화된 보스 조회
    public List<BossResponse> getAllActiveBosses() {
        return bossRepository.findByIsActiveTrueWithBossDropItemsOrderByCrystalPriceDesc()
                .stream()
                .map(BossResponse::fromWithDesireItems)
                .toList();
    }
} 