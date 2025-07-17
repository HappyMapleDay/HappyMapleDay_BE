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
public class RecommendationRequest {
    
    @NotNull
    private Long userId;
    
    @NotNull
    private List<CharacterBossSelection> characterBossSelections;
} 