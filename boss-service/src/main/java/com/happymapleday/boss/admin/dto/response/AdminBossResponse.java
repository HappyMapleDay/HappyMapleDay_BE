package com.happymapleday.boss.admin.dto.response;

import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.ForceType;
import lombok.Getter;

@Getter
public class AdminBossResponse {
    
    private final Long id;
    private final String bossName;
    private final String difficulty;
    private final Long crystalPrice;
    private final Integer maxPartySize;
    private final Boolean isMonthly;
    private final Boolean isActive;
    private final Integer minEntryLevel;
    private final Integer bossLevel;
    private final ForceType requiredForceType;
    private final Integer requiredForceAmount;
    
    public AdminBossResponse(Boss boss) {
        this.id = boss.getId();
        this.bossName = boss.getBossName();
        this.difficulty = boss.getDifficulty();
        this.crystalPrice = boss.getCrystalPrice();
        this.maxPartySize = boss.getMaxPartySize();
        this.isMonthly = boss.getIsMonthly();
        this.isActive = boss.getIsActive();
        this.minEntryLevel = boss.getMinEntryLevel();
        this.bossLevel = boss.getBossLevel();
        this.requiredForceType = boss.getRequiredForceType();
        this.requiredForceAmount = boss.getRequiredForceAmount();
    }
} 