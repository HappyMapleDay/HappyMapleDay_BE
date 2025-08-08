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
    private Long bossId;
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
    

} 