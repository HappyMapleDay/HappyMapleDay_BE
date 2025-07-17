package com.happymapleday.recommendation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterBossSelection {
    
    @NotNull
    private Long characterId;
    
    @NotNull
    private String characterName;
    
    @NotNull
    private Integer characterLevel;
    private Integer arcaneForce;
    private Integer authenticForce;
    
    @NotNull
    private List<BossSelection> bossSelections;
} 