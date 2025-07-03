package com.happymapleday.boss.dto.response;

import com.happymapleday.boss.entity.BossPreset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossPresetResponse {
    private Long id;
    private String presetName;
    private List<Map<String, Object>> bossIds;
    private Integer bossCount;
    private List<BossSimpleResponse> bosses;

    public static BossPresetResponse from(BossPreset preset) {
        return BossPresetResponse.builder()
                .id(preset.getId())
                .presetName(preset.getPresetName())
                .bossIds(preset.getBossIds())
                .bossCount(preset.getBossCount())
                .build();
    }

    public static BossPresetResponse fromWithBosses(BossPreset preset, List<BossSimpleResponse> bosses) {
        BossPresetResponse response = from(preset);
        response.setBosses(bosses);
        return response;
    }
} 