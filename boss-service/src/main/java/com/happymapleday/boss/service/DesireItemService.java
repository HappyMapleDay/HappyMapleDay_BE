package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.response.DesireItemResponse;
import com.happymapleday.boss.repository.DesireItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DesireItemService {

    private final DesireItemRepository desireItemRepository;

    // 특정 보스의 모든 물욕템 조회
    public List<DesireItemResponse> getDesireItemsByBossId(Long bossId) {
        return desireItemRepository.findByBossIdOrderByItemName(bossId)
                .stream()
                .map(DesireItemResponse::fromWithRandomBoxItems)
                .toList();
    }
} 