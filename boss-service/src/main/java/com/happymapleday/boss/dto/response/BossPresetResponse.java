package com.happymapleday.boss.dto.response;

import com.happymapleday.boss.entity.BossPreset;
import lombok.*;


import java.util.List;
import java.util.Map;

@Getter
@Builder
public class BossPresetResponse {
    private Long presetId;
    private String presetName;
    private List<Map<String, Object>> bossIds;
    private Integer bossCount;
    private List<BossSimpleResponse> bosses;

    public static BossPresetResponse from(BossPreset preset) {
        return BossPresetResponse.builder()
                .presetId(preset.getId())
                .presetName(preset.getPresetName())
                .bossIds(preset.getBossIds())
                .bossCount(preset.getBossCount())
                .build();
    }

    public static BossPresetResponse fromWithBosses(BossPreset preset, List<BossSimpleResponse> bosses) {
        return BossPresetResponse.builder()
                .presetId(preset.getId())
                .presetName(preset.getPresetName())
                .bossIds(preset.getBossIds())
                .bossCount(preset.getBossCount())
                .bosses(bosses)
                .build();
    }
} 