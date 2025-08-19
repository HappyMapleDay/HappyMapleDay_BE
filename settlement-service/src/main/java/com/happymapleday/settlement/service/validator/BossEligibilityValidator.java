package com.happymapleday.settlement.service.validator;

import com.happymapleday.common.client.BossServiceClient;
import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.settlement.dto.request.BossRecordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BossEligibilityValidator {

    private final BossServiceClient bossServiceClient;

    public void validate(List<BossRecordRequest> bossRequests) {
        if (bossRequests == null || bossRequests.isEmpty()) {
            return;
        }

        Map<Long, BossResponse> bossIdToInfo = loadBossInfoMap();

        for (BossRecordRequest request : bossRequests) {
            BossResponse boss = bossIdToInfo.get(request.getBossId());
            if (boss == null) {
                throw new IllegalArgumentException("보스 정보를 찾을 수 없습니다: id=" + request.getBossId());
            }

            Integer minEntryLevel = boss.getMinEntryLevel();
            if (minEntryLevel != null && request.getCharacterLevel() < minEntryLevel) {
                throw new IllegalArgumentException("캐릭터 레벨이 보스 최소 레벨보다 낮아 정산이 불가합니다.");
            }

            String forceType = boss.getRequiredForceType();
            Integer requiredAmount = boss.getRequiredForceAmount();
            if (forceType != null && requiredAmount != null) {
                if ("ARCANE".equalsIgnoreCase(forceType)) {
                    Integer arcane = request.getArcaneForce() != null ? request.getArcaneForce() : 0;
                    if (arcane <= requiredAmount / 2) {
                        throw new IllegalArgumentException("아케인포스가 요구치의 절반 이하라 정산이 불가합니다.");
                    }
                } else if ("AUTHENTIC".equalsIgnoreCase(forceType)) {
                    Integer authentic = request.getAuthenticForce() != null ? request.getAuthenticForce() : 0;
                    if (requiredAmount - authentic >= 50) {
                        throw new IllegalArgumentException("어센틱포스가 요구치보다 50 이상 낮아 정산이 불가합니다.");
                    }
                }
            }
        }
    }

    private Map<Long, BossResponse> loadBossInfoMap() {
        ApiResponse<List<BossResponse>> response = bossServiceClient.getBossList();
        if (response == null || response.getData() == null) {
            throw new IllegalStateException("보스 정보를 조회할 수 없습니다.");
        }
        Map<Long, BossResponse> map = new HashMap<>();
        for (BossResponse b : response.getData()) {
            map.put(b.getBossId(), b);
        }
        return map;
    }
}


