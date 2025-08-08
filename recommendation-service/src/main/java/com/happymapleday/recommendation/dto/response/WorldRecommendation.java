package com.happymapleday.recommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorldRecommendation {
    private String worldName;
    private Integer worldBossCount;
    private Long worldCrystalIncome;
    private List<CharacterRecommendation> characters;
}


