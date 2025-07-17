package com.happymapleday.recommendation.service.limiter;

import com.happymapleday.common.dto.BossResponse;
import lombok.Getter;

@Getter
public class BossCandidate {
    private final BossResponse boss;
    private final Long characterId;
    
    public BossCandidate(BossResponse boss, Long characterId) {
        this.boss = boss;
        this.characterId = characterId;
    }
} 