package com.happymapleday.boss.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossPresetApplyRequest {
    private Long presetId;
    private Long characterId;
} 