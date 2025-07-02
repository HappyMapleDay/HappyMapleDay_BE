package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.BossDto;
import com.happymapleday.boss.dto.BossPresetDto;
import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.BossPreset;
import com.happymapleday.boss.repository.BossPresetRepository;
import com.happymapleday.boss.repository.BossRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BossPresetService {

    private final BossPresetRepository bossPresetRepository;
    private final BossRepository bossRepository;

    // 모든 프리셋 조회
    public List<BossPresetDto.SimpleResponse> getAllPresets() {
        return bossPresetRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(BossPresetDto.SimpleResponse::from)
                .toList();
    }

    // ID로 프리셋 상세 조회 (보스 목록 포함)
    public BossPresetDto.Response getPresetById(Long id) {
        BossPreset preset = bossPresetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 프리셋을 찾을 수 없습니다: " + id));

        List<BossDto.SimpleResponse> bosses = getBossesFromPreset(preset);
        return BossPresetDto.Response.fromWithBosses(preset, bosses);
    }

    // 프리셋명으로 조회
    public BossPresetDto.Response getPresetByName(String presetName) {
        BossPreset preset = bossPresetRepository.findByPresetName(presetName)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 프리셋을 찾을 수 없습니다: " + presetName));

        List<BossDto.SimpleResponse> bosses = getBossesFromPreset(preset);
        return BossPresetDto.Response.fromWithBosses(preset, bosses);
    }

    // 프리셋명으로 검색
    public List<BossPresetDto.SimpleResponse> searchPresetsByName(String presetName) {
        return bossPresetRepository.findByPresetNameContainingIgnoreCaseOrderByCreatedAtDesc(presetName)
                .stream()
                .map(BossPresetDto.SimpleResponse::from)
                .toList();
    }

    // 특정 보스를 포함하는 프리셋 조회
    public List<BossPresetDto.SimpleResponse> getPresetsContainingBoss(Long bossId) {
        String bossIdJson = String.format("{\"boss_id\": %d}", bossId);
        return bossPresetRepository.findPresetsContainingBoss(bossIdJson)
                .stream()
                .map(BossPresetDto.SimpleResponse::from)
                .toList();
    }

    // 프리셋에서 보스 목록 추출
    private List<BossDto.SimpleResponse> getBossesFromPreset(BossPreset preset) {
        List<Long> bossIds = preset.extractBossIds();
        return bossRepository.findAllById(bossIds)
                .stream()
                .map(BossDto.SimpleResponse::from)
                .toList();
    }

    // 프리셋 생성
    @Transactional
    public BossPresetDto.Response createPreset(BossPresetDto.CreateRequest createRequest) {
        // 중복 체크
        if (bossPresetRepository.existsByPresetName(createRequest.getPresetName())) {
            throw new IllegalArgumentException("이미 존재하는 프리셋 이름입니다: " + createRequest.getPresetName());
        }

        // 보스 ID 유효성 검증
        validateBossIds(createRequest.getBossIds());

        BossPreset preset = createRequest.toEntity();
        BossPreset savedPreset = bossPresetRepository.save(preset);
        
        log.info("새로운 프리셋이 생성되었습니다: {} (보스 {}개)", 
                savedPreset.getPresetName(), savedPreset.getBossCount());
        
        List<BossDto.SimpleResponse> bosses = getBossesFromPreset(savedPreset);
        return BossPresetDto.Response.fromWithBosses(savedPreset, bosses);
    }

    // 프리셋 수정
    @Transactional
    public BossPresetDto.Response updatePreset(Long id, BossPresetDto.UpdateRequest updateRequest) {
        BossPreset preset = bossPresetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 프리셋을 찾을 수 없습니다: " + id));

        // 프리셋명이 변경되는 경우 중복 체크
        if (updateRequest.getPresetName() != null && 
            !updateRequest.getPresetName().equals(preset.getPresetName())) {
            if (bossPresetRepository.existsByPresetName(updateRequest.getPresetName())) {
                throw new IllegalArgumentException("이미 존재하는 프리셋 이름입니다: " + updateRequest.getPresetName());
            }
        }

        // 보스 ID 목록이 변경되는 경우 유효성 검증
        if (updateRequest.getBossIds() != null) {
            validateBossIds(updateRequest.getBossIds());
            preset.updateBossIds(updateRequest.getBossIds());
        }

        BossPreset updatedPreset = bossPresetRepository.save(preset);
        log.info("프리셋이 수정되었습니다: {} (보스 {}개)", 
                updatedPreset.getPresetName(), updatedPreset.getBossCount());
        
        List<BossDto.SimpleResponse> bosses = getBossesFromPreset(updatedPreset);
        return BossPresetDto.Response.fromWithBosses(updatedPreset, bosses);
    }

    // 프리셋 삭제
    @Transactional
    public void deletePreset(Long id) {
        BossPreset preset = bossPresetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 프리셋을 찾을 수 없습니다: " + id));

        bossPresetRepository.delete(preset);
        log.info("프리셋이 삭제되었습니다: {}", preset.getPresetName());
    }

    // 보스 ID 유효성 검증
    private void validateBossIds(List<Map<String, Object>> bossIds) {
        if (bossIds == null || bossIds.isEmpty()) {
            throw new IllegalArgumentException("프리셋에는 최소 1개 이상의 보스가 포함되어야 합니다.");
        }

        // 최대 12개 제한 (요구사항에 따라)
        if (bossIds.size() > 12) {
            throw new IllegalArgumentException("프리셋에는 최대 12개의 보스만 포함할 수 있습니다.");
        }

        // 각 보스 ID가 존재하는지 확인
        for (Map<String, Object> bossData : bossIds) {
            Object bossIdObj = bossData.get("boss_id");
            if (bossIdObj == null) {
                throw new IllegalArgumentException("보스 ID가 누락되었습니다.");
            }

            Long bossId;
            if (bossIdObj instanceof Number) {
                bossId = ((Number) bossIdObj).longValue();
            } else {
                throw new IllegalArgumentException("잘못된 보스 ID 형식입니다: " + bossIdObj);
            }

            if (!bossRepository.existsById(bossId)) {
                throw new IllegalArgumentException("존재하지 않는 보스 ID입니다: " + bossId);
            }
        }
    }

    // 프리셋에 보스 추가
    @Transactional
    public BossPresetDto.Response addBossToPreset(Long presetId, Long bossId) {
        BossPreset preset = bossPresetRepository.findById(presetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 프리셋을 찾을 수 없습니다: " + presetId));

        if (!bossRepository.existsById(bossId)) {
            throw new IllegalArgumentException("존재하지 않는 보스 ID입니다: " + bossId);
        }

        List<Map<String, Object>> currentBossIds = preset.getBossIds();
        if (currentBossIds.size() >= 12) {
            throw new IllegalArgumentException("프리셋에는 최대 12개의 보스만 포함할 수 있습니다.");
        }

        // 이미 포함된 보스인지 확인
        boolean alreadyExists = currentBossIds.stream()
                .anyMatch(data -> {
                    Object existingBossId = data.get("boss_id");
                    return existingBossId instanceof Number && 
                           ((Number) existingBossId).longValue() == bossId;
                });

        if (alreadyExists) {
            throw new IllegalArgumentException("이미 프리셋에 포함된 보스입니다.");
        }

        currentBossIds.add(Map.of("boss_id", bossId));
        preset.updateBossIds(currentBossIds);

        BossPreset updatedPreset = bossPresetRepository.save(preset);
        log.info("프리셋 '{}'에 보스(ID: {})가 추가되었습니다.", preset.getPresetName(), bossId);

        List<BossDto.SimpleResponse> bosses = getBossesFromPreset(updatedPreset);
        return BossPresetDto.Response.fromWithBosses(updatedPreset, bosses);
    }

    // 프리셋에서 보스 제거
    @Transactional
    public BossPresetDto.Response removeBossFromPreset(Long presetId, Long bossId) {
        BossPreset preset = bossPresetRepository.findById(presetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 프리셋을 찾을 수 없습니다: " + presetId));

        List<Map<String, Object>> currentBossIds = preset.getBossIds();
        List<Map<String, Object>> updatedBossIds = currentBossIds.stream()
                .filter(data -> {
                    Object existingBossId = data.get("boss_id");
                    return !(existingBossId instanceof Number && 
                           ((Number) existingBossId).longValue() == bossId);
                })
                .toList();

        if (updatedBossIds.size() == currentBossIds.size()) {
            throw new IllegalArgumentException("프리셋에 포함되지 않은 보스입니다.");
        }

        if (updatedBossIds.isEmpty()) {
            throw new IllegalArgumentException("프리셋에는 최소 1개 이상의 보스가 포함되어야 합니다.");
        }

        preset.updateBossIds(updatedBossIds);

        BossPreset updatedPreset = bossPresetRepository.save(preset);
        log.info("프리셋 '{}'에서 보스(ID: {})가 제거되었습니다.", preset.getPresetName(), bossId);

        List<BossDto.SimpleResponse> bosses = getBossesFromPreset(updatedPreset);
        return BossPresetDto.Response.fromWithBosses(updatedPreset, bosses);
    }
} 