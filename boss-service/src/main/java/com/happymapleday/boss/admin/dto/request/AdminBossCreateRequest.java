package com.happymapleday.boss.admin.dto.request;

import com.happymapleday.boss.entity.ForceType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminBossCreateRequest {

    private String bossName;
    private String bossNameEn;
    private String difficulty;
    private String difficultyEn;
    private Long crystalPrice;
    private Integer maxPartySize;
    private Boolean isMonthly;
    private Boolean isActive;
    private Integer minEntryLevel;
    private Integer bossLevel;
    private ForceType requiredForceType;
    private Integer requiredForceAmount;

} 