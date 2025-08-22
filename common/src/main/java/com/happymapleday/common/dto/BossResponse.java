package com.happymapleday.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossResponse {
    private Long bossId;
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
    private String requiredForceType;
    private Integer requiredForceAmount;
    private String fullName;
    private List<DesireItemResponse> desireItems;
    

} 