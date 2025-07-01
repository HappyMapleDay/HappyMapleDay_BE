package com.happymapleday.boss.dto;

import com.happymapleday.boss.entity.BossPreset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class BossPresetDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String presetName;
        private List<Map<String, Object>> bossIds;
        private LocalDateTime createdAt;
        private Integer bossCount;
        private List<BossDto.SimpleResponse> bosses;

        public static Response from(BossPreset preset) {
            return Response.builder()
                    .id(preset.getId())
                    .presetName(preset.getPresetName())
                    .bossIds(preset.getBossIds())
                    .createdAt(preset.getCreatedAt())
                    .bossCount(preset.getBossCount())
                    .build();
        }

        public static Response fromWithBosses(BossPreset preset, List<BossDto.SimpleResponse> bosses) {
            Response response = from(preset);
            response.setBosses(bosses);
            return response;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private String presetName;
        private List<Map<String, Object>> bossIds;

        public BossPreset toEntity() {
            return BossPreset.builder()
                    .presetName(presetName)
                    .bossIds(bossIds)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String presetName;
        private List<Map<String, Object>> bossIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleResponse {
        private Long id;
        private String presetName;
        private Integer bossCount;
        private LocalDateTime createdAt;

        public static SimpleResponse from(BossPreset preset) {
            return SimpleResponse.builder()
                    .id(preset.getId())
                    .presetName(preset.getPresetName())
                    .bossCount(preset.getBossCount())
                    .createdAt(preset.getCreatedAt())
                    .build();
        }
    }
} 