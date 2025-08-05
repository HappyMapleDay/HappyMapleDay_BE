package com.happymapleday.recommendation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossSelection {
    
    @NotNull
    private Long bossId;
    
    @NotNull
    private Integer partySize; // 1: 솔로, 2이상: 파티
    
    private boolean isCleared;
    // 2명 이상으로 클리어하는 보스인지 확인
    public boolean isPartyBoss() {
        return partySize >= 2;
    }
    
    // 솔로 보스인지 확인
    public boolean isSoloBoss() {
        return partySize == 1;
    }
} 