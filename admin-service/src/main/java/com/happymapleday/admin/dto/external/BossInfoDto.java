package com.happymapleday.admin.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BossInfoDto {
    private Long id;
    private String bossName;
    private String bossNameEn;
    private String difficulty;
    private Long crystalPrice;
    private Integer maxPartySize;
    private Boolean isMonthly;
    private Boolean isActive;
    private Integer minEntryLevel;
    private Integer bossLevel;
    private String requiredForceType;
    private Integer requiredForceAmount;
}

