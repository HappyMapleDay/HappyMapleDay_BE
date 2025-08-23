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
public class DesireItemResponse {
    private Long desireItemId;
    private String itemName;
    private String itemNameEn;
    private Boolean isRandomBox;
    private String fullItemName;
    private Long bossId;
    private String bossName;
    private String bossNameEn;
    private String bossDifficulty;
    private String bossDifficultyEn;
    private List<RandomBoxItemResponse> randomBoxItems;
}


