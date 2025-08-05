package com.happymapleday.boss.admin.dto.response;

import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.ForceType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminBossResponse {
    
    private Long adminBossId;
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

    public static AdminBossResponse from(Boss boss) {
        return AdminBossResponse.builder()
                .adminBossId(boss.getId())
                .bossName(boss.getBossName())
                .bossNameEn(boss.getBossNameEn())
                .difficulty(boss.getDifficulty())
                .difficultyEn(boss.getDifficultyEn())
                .crystalPrice(boss.getCrystalPrice())
                .maxPartySize(boss.getMaxPartySize())
                .isMonthly(boss.getIsMonthly())
                .isActive(boss.getIsActive())
                .minEntryLevel(boss.getMinEntryLevel())
                .requiredForceType(boss.getRequiredForceType())
                .requiredForceAmount(boss.getRequiredForceAmount())
                .build();

    }

} 