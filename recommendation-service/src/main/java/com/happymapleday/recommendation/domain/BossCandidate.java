package com.happymapleday.recommendation.domain;

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
    
    // 수익성 비교를 위한 메서드
    public Long getCrystalPrice() {
        return boss.getCrystalPrice();
    }
    
    // 보스 이름 반환
    public String getBossName() {
        return boss.getBossName();
    }
    
    // 솔로 보스인지 확인
    public boolean isSoloBoss() {
        return boss.isSoloBoss();
    }
} 