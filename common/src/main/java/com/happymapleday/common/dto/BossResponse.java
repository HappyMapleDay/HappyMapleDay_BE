package com.happymapleday.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossResponse {
    private Long id;
    private String bossName;
    private String difficulty;
    private Long crystalPrice;
    private Integer maxPartySize;
    private Boolean isMonthly;
    private Boolean isActive;
    private Integer minEntryLevel;
    private Integer bossLevel;
    private String requiredForceType;
    private Integer requiredForceAmount;
    private String fullName;
    
    // 파티 보스 여부 판단 (maxPartySize > 1)
    public boolean isPartyBoss() {
        return maxPartySize != null && maxPartySize > 1;
    }
    
    // 솔로 보스 여부 판단
    public boolean isSoloBoss() {
        return !isPartyBoss();
    }
} 