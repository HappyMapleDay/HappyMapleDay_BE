package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.response.BossSimpleResponse;
import com.happymapleday.boss.dto.response.BossPresetResponse;

import com.happymapleday.boss.dto.request.ValidateLimitsRequest;
import com.happymapleday.boss.dto.response.ValidateLimitsResponse;
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
    public List<BossPresetResponse> getAllPresetsWithBosses() {
        return bossPresetRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(preset -> {
                    List<BossSimpleResponse> bosses = getBossesFromPreset(preset);
                    return BossPresetResponse.fromWithBosses(preset, bosses);
                })
                .toList();
    }


    // 보스 제한 검증
    public ValidateLimitsResponse validateLimits(ValidateLimitsRequest request) {
        // 캐릭터별 보스 개수 계산
        Map<String, ValidateLimitsResponse.CharacterLimitStatus> characterLimitStatus = 
                calculateCharacterLimits(request.getSelectedBosses());
        
        // 서버 전체 보스 개수 계산
        ValidateLimitsResponse.ServerLimitStatus serverLimitStatus = 
                calculateServerLimits(request.getSelectedBosses());
        
        // 제한 위반 체크
        List<String> violations = checkViolations(characterLimitStatus, serverLimitStatus);
        
        return ValidateLimitsResponse.builder()
                .isValid(violations.isEmpty())
                .characterLimitStatus(characterLimitStatus)
                .serverLimitStatus(serverLimitStatus)
                .violations(violations)
                .build();
    }

    // 프리셋에서 보스 목록 추출
    private List<BossSimpleResponse> getBossesFromPreset(BossPreset preset) {
        List<Long> bossIds = preset.extractBossIds();
        return bossRepository.findAllById(bossIds)
                .stream()
                .map(BossSimpleResponse::from)
                .toList();
    }

    // 캐릭터별 제한 계산
    private Map<String, ValidateLimitsResponse.CharacterLimitStatus> calculateCharacterLimits(
            List<ValidateLimitsRequest.SelectedBoss> selectedBosses) {
        
        Map<String, ValidateLimitsResponse.CharacterLimitStatus> characterLimits = new java.util.HashMap<>();
        Map<Long, Integer> characterBossCounts = new java.util.HashMap<>();
        
        // 캐릭터별 보스 개수 계산
        for (ValidateLimitsRequest.SelectedBoss selectedBoss : selectedBosses) {
            characterBossCounts.merge(selectedBoss.getCharacterId(), 1, Integer::sum);
        }
        
        // 각 캐릭터별 제한 상태 계산
        for (Map.Entry<Long, Integer> entry : characterBossCounts.entrySet()) {
            Long characterId = entry.getKey();
            Integer current = entry.getValue();
            Integer limit = 12; // 캐릭터당 최대 12개 보스
            Integer remaining = Math.max(0, limit - current);
            
            characterLimits.put(characterId.toString(), 
                    ValidateLimitsResponse.CharacterLimitStatus.builder()
                            .current(current)
                            .limit(limit)
                            .remaining(remaining)
                            .build());
        }
        
        return characterLimits;
    }

    // 서버 전체 제한 계산
    private ValidateLimitsResponse.ServerLimitStatus calculateServerLimits(
            List<ValidateLimitsRequest.SelectedBoss> selectedBosses) {
        
        Integer current = selectedBosses.size();
        Integer limit = 90; // 서버 전체 최대 90개 보스
        Integer remaining = Math.max(0, limit - current);
        
        return ValidateLimitsResponse.ServerLimitStatus.builder()
                .current(current)
                .limit(limit)
                .remaining(remaining)
                .build();
    }

    // 제한 위반 체크
    private List<String> checkViolations(
            Map<String, ValidateLimitsResponse.CharacterLimitStatus> characterLimitStatus,
            ValidateLimitsResponse.ServerLimitStatus serverLimitStatus) {
        
        List<String> violations = new java.util.ArrayList<>();
        
        // 캐릭터별 제한 위반 체크
        for (Map.Entry<String, ValidateLimitsResponse.CharacterLimitStatus> entry : characterLimitStatus.entrySet()) {
            String characterId = entry.getKey();
            ValidateLimitsResponse.CharacterLimitStatus status = entry.getValue();
            
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