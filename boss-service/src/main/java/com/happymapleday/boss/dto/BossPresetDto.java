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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplyRequest {
        private Long presetId;
        private Long characterId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplyResponse {
        private List<BossDto.SimpleResponse> appliedBosses;
        private Long characterId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ValidateLimitsRequest {
        private Long userId;
        private List<SelectedBoss> selectedBosses;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class SelectedBoss {
            private Long characterId;
            private Long bossId;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ValidateLimitsResponse {
        private Boolean isValid;
        private Map<String, CharacterLimitStatus> characterLimitStatus;
        private ServerLimitStatus serverLimitStatus;
        private List<String> violations;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class CharacterLimitStatus {
            private Integer current;
            private Integer limit;
            private Integer remaining;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ServerLimitStatus {
            private Integer current;
            private Integer limit;
            private Integer remaining;
        }
    }
} 