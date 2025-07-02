package com.happymapleday.boss.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossPresetApplyResponse {
    private List<BossSimpleResponse> appliedBosses;
    private Long characterId;
} 