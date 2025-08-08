package com.happymapleday.recommendation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterInput {
    private Long characterId;
    private String worldName;
    private Integer level;
    private Integer arcaneForce;
    private Integer authenticForce;
    private List<PlannedBoss> plannedBosses;
}


