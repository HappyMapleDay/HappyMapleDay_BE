package com.happymapleday.recommendation.domain;

import com.happymapleday.common.dto.BossResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@Builder
public class CharacterCapability {
    
    private final Long characterId;
    private final String characterName;
    private final Integer characterLevel;
    private final List<BossResponse> clearableBosses;
    private final Set<Long> requestedPartyBossIds;
    private final Long highestDifficultySoloBossId;
    
    // 해당 보스를 클리어할 수 있는지 확인
    public boolean canClearBoss(Long bossId) {
        return clearableBosses.stream()
                .anyMatch(boss -> boss.getId().equals(bossId));
    }
    
    // 파티 보스로 요청한 보스인지 확인
    public boolean isRequestedPartyBoss(Long bossId) {
        return requestedPartyBossIds.contains(bossId);
    }
    
    // 클리어 가능한 보스 개수
    public int getClearableBossCount() {
        return clearableBosses.size();
    }
} 