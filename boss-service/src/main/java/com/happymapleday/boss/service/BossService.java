package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.BossDto;
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
    public List<BossDto.Response> getAllActiveBosses() {
        return bossRepository.findByIsActiveTrueOrderByCrystalPriceDesc()
                .stream()
                .map(BossDto.Response::from)
                .toList();
    }
} 