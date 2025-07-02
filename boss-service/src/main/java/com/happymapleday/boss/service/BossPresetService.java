package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.BossDto;
import com.happymapleday.boss.dto.BossPresetDto;
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

    // 보스 정보를 포함한 모든 프리셋 조회
    public List<BossPresetDto.Response> getAllPresetsWithBosses() {
        return bossPresetRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(preset -> {
                    List<BossDto.SimpleResponse> bosses = getBossesFromPreset(preset);
                    return BossPresetDto.Response.fromWithBosses(preset, bosses);
                })
                .toList();
    }

    // 프리셋 적용
    @Transactional
    public BossPresetDto.ApplyResponse applyPreset(Long presetId, Long characterId) {
        BossPreset preset = bossPresetRepository.findById(presetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 프리셋을 찾을 수 없습니다: " + presetId));

        List<BossDto.SimpleResponse> appliedBosses = getBossesFromPreset(preset);
        
        log.info("캐릭터 {}에게 프리셋 '{}'이 적용되었습니다. (보스 {}개)", 
                characterId, preset.getPresetName(), appliedBosses.size());

        return BossPresetDto.ApplyResponse.builder()
                .appliedBosses(appliedBosses)
                .characterId(characterId)
                .build();
    }

    // 보스 제한 검증
    public BossPresetDto.ValidateLimitsResponse validateLimits(BossPresetDto.ValidateLimitsRequest request) {
        // 캐릭터별 보스 개수 계산
        Map<String, BossPresetDto.ValidateLimitsResponse.CharacterLimitStatus> characterLimitStatus = 
                calculateCharacterLimits(request.getSelectedBosses());
        
        // 서버 전체 보스 개수 계산
        BossPresetDto.ValidateLimitsResponse.ServerLimitStatus serverLimitStatus = 
                calculateServerLimits(request.getSelectedBosses());
        
        // 제한 위반 체크
        List<String> violations = checkViolations(characterLimitStatus, serverLimitStatus);
        
        return BossPresetDto.ValidateLimitsResponse.builder()
                .isValid(violations.isEmpty())
                .characterLimitStatus(characterLimitStatus)
                .serverLimitStatus(serverLimitStatus)
                .violations(violations)
                .build();
    }

    // 프리셋에서 보스 목록 추출
    private List<BossDto.SimpleResponse> getBossesFromPreset(BossPreset preset) {
        List<Long> bossIds = preset.extractBossIds();
        return bossRepository.findAllById(bossIds)
                .stream()
                .map(BossDto.SimpleResponse::from)
                .toList();
    }

    // 캐릭터별 제한 계산
    private Map<String, BossPresetDto.ValidateLimitsResponse.CharacterLimitStatus> calculateCharacterLimits(
            List<BossPresetDto.ValidateLimitsRequest.SelectedBoss> selectedBosses) {
        
        Map<String, BossPresetDto.ValidateLimitsResponse.CharacterLimitStatus> characterLimits = new java.util.HashMap<>();
        Map<Long, Integer> characterBossCounts = new java.util.HashMap<>();
        
        // 캐릭터별 보스 개수 계산
        for (BossPresetDto.ValidateLimitsRequest.SelectedBoss selectedBoss : selectedBosses) {
            characterBossCounts.merge(selectedBoss.getCharacterId(), 1, Integer::sum);
        }
        
        // 각 캐릭터별 제한 상태 계산
        for (Map.Entry<Long, Integer> entry : characterBossCounts.entrySet()) {
            Long characterId = entry.getKey();
            Integer current = entry.getValue();
            Integer limit = 12; // 캐릭터당 최대 12개 보스
            Integer remaining = Math.max(0, limit - current);
            
            characterLimits.put(characterId.toString(), 
                    BossPresetDto.ValidateLimitsResponse.CharacterLimitStatus.builder()
                            .current(current)
                            .limit(limit)
                            .remaining(remaining)
                            .build());
        }
        
        return characterLimits;
    }

    // 서버 전체 제한 계산
    private BossPresetDto.ValidateLimitsResponse.ServerLimitStatus calculateServerLimits(
            List<BossPresetDto.ValidateLimitsRequest.SelectedBoss> selectedBosses) {
        
        Integer current = selectedBosses.size();
        Integer limit = 90; // 서버 전체 최대 90개 보스
        Integer remaining = Math.max(0, limit - current);
        
        return BossPresetDto.ValidateLimitsResponse.ServerLimitStatus.builder()
                .current(current)
                .limit(limit)
                .remaining(remaining)
                .build();
    }

    // 제한 위반 체크
    private List<String> checkViolations(
            Map<String, BossPresetDto.ValidateLimitsResponse.CharacterLimitStatus> characterLimitStatus,
            BossPresetDto.ValidateLimitsResponse.ServerLimitStatus serverLimitStatus) {
        
        List<String> violations = new java.util.ArrayList<>();
        
        // 캐릭터별 제한 위반 체크
        for (Map.Entry<String, BossPresetDto.ValidateLimitsResponse.CharacterLimitStatus> entry : characterLimitStatus.entrySet()) {
            String characterId = entry.getKey();
            BossPresetDto.ValidateLimitsResponse.CharacterLimitStatus status = entry.getValue();
            
            if (status.getCurrent() > status.getLimit()) {
                violations.add(String.format("캐릭터 %s번이 %d개 제한을 초과했습니다.", 
                        characterId, status.getLimit()));
            }
        }
        
        // 서버 전체 제한 위반 체크
        if (serverLimitStatus.getCurrent() > serverLimitStatus.getLimit()) {
            violations.add(String.format("서버 전체 %d개 제한을 초과했습니다.", 
                    serverLimitStatus.getLimit()));
        }
        
        return violations;
    }
} 