package com.happymapleday.boss.dto;

import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.ForceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class BossDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String bossName;
        private String difficulty;
        private Long crystalPrice;
        private Integer maxPartySize;
        private Boolean isMonthly;
        private Boolean isActive;
        private Integer minEntryLevel;
        private Integer bossLevel;
        private ForceType requiredForceType;
        private Integer requiredForceAmount;
        private String fullName;
        private List<DesireItemDto.Response> desireItems;

        public static Response from(Boss boss) {
            return Response.builder()
                    .id(boss.getId())
                    .bossName(boss.getBossName())
                    .difficulty(boss.getDifficulty())
                    .crystalPrice(boss.getCrystalPrice())
                    .maxPartySize(boss.getMaxPartySize())
                    .isMonthly(boss.getIsMonthly())
                    .isActive(boss.getIsActive())
                    .minEntryLevel(boss.getMinEntryLevel())
                    .bossLevel(boss.getBossLevel())
                    .requiredForceType(boss.getRequiredForceType())
                    .requiredForceAmount(boss.getRequiredForceAmount())
                    .fullName(boss.getFullName())
                    .build();
        }

        public static Response fromWithDesireItems(Boss boss) {
            Response response = from(boss);
            if (boss.getDesireItems() != null) {
                response.setDesireItems(
                        boss.getDesireItems().stream()
                                .map(DesireItemDto.Response::from)
                                .toList()
                );
            }
            return response;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private String bossName;
        private String difficulty;
        private Long crystalPrice;
        private Integer maxPartySize;
        private Boolean isMonthly;
        private Integer minEntryLevel;
        private Integer bossLevel;
        private ForceType requiredForceType;
        private Integer requiredForceAmount;

        public Boss toEntity() {
            return Boss.builder()
                    .bossName(bossName)
                    .difficulty(difficulty)
                    .crystalPrice(crystalPrice)
                    .maxPartySize(maxPartySize)
                    .isMonthly(isMonthly)
                    .minEntryLevel(minEntryLevel)
                    .bossLevel(bossLevel)
                    .requiredForceType(requiredForceType)
                    .requiredForceAmount(requiredForceAmount)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String bossName;
        private String difficulty;
        private Long crystalPrice;
        private Integer maxPartySize;
        private Boolean isMonthly;
        private Boolean isActive;
        private Integer minEntryLevel;
        private Integer bossLevel;
        private ForceType requiredForceType;
        private Integer requiredForceAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchRequest {
        private String bossName;
        private String difficulty;
        private Boolean isMonthly;
        private ForceType requiredForceType;
        private Long minPrice;
        private Long maxPrice;
        private Integer characterLevel;
        private Integer arcaneForce;
        private Integer authenticForce;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleResponse {
        private Long id;
        private String bossName;
        private String difficulty;
        private Long crystalPrice;
        private String fullName;

        public static SimpleResponse from(Boss boss) {
            return SimpleResponse.builder()
                    .id(boss.getId())
                    .bossName(boss.getBossName())
                    .difficulty(boss.getDifficulty())
                    .crystalPrice(boss.getCrystalPrice())
                    .fullName(boss.getFullName())
                    .build();
        }
    }
} 