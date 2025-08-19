package com.happymapleday.boss.service.impl;

import com.happymapleday.boss.dto.response.BossSimpleResponse;
import com.happymapleday.boss.dto.response.BossPresetResponse;
import com.happymapleday.boss.entity.BossPreset;
import com.happymapleday.boss.repository.BossPresetRepository;
import com.happymapleday.boss.repository.BossRepository;
import com.happymapleday.boss.service.BossPresetService;
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
public class BossPresetServiceImpl implements BossPresetService {

    private final BossPresetRepository bossPresetRepository;
    private final BossRepository bossRepository;

    // 보스 정보를 포함한 모든 프리셋 조회
    @Override
    @Cacheable(cacheNames = "bossPresetList")
    public List<BossPresetResponse> getAllPresetsWithBosses() {
        return bossPresetRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(preset -> {
                    List<BossSimpleResponse> bosses = getBossesFromPreset(preset);
                    return BossPresetResponse.fromWithBosses(preset, bosses);
                })
                .toList();
    }

    // 프리셋에서 보스 목록 추출
    private List<BossSimpleResponse> getBossesFromPreset(BossPreset preset) {
        List<Long> bossIds = preset.extractBossIds();
        return bossRepository.findAllById(bossIds)
                .stream()
                .map(BossSimpleResponse::from)
                .toList();
    }
} 