package com.happymapleday.boss.admin.dto.request;

import com.happymapleday.boss.entity.ForceType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminBossUpdateRequest {
    
    private String bossName;
    private String difficulty;
    private Long crystalPrice;
    private Integer maxPartySize;
    private Boolean isMonthly;
    private Boolean isActive;
    private Integer minEntryLevel;
    private Integer bossLevel;
    private ForceType requiredForceType;
    private Integer requiredForceAmount;
    
    public AdminBossUpdateRequest(String bossName, String difficulty, Long crystalPrice,
                                 Integer maxPartySize, Boolean isMonthly, Boolean isActive,
                                 Integer minEntryLevel, Integer bossLevel, 
                                 ForceType requiredForceType, Integer requiredForceAmount) {
        this.bossName = bossName;
        this.difficulty = difficulty;
        this.crystalPrice = crystalPrice;
        this.maxPartySize = maxPartySize;
        this.isMonthly = isMonthly;
        this.isActive = isActive;
        this.minEntryLevel = minEntryLevel;
        this.bossLevel = bossLevel;
        this.requiredForceType = requiredForceType;
        this.requiredForceAmount = requiredForceAmount;
    }
} 