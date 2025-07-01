package com.happymapleday.boss.dto;

import com.happymapleday.boss.entity.RandomBoxItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RandomBoxItemDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String dropItemName;
        private Integer dropItemLevel;
        private String fullDropItemName;
        private Boolean hasDropLevel;
        private Long desireItemId;
        private String desireItemName;

        public static Response from(RandomBoxItem randomBoxItem) {
            return Response.builder()
                    .id(randomBoxItem.getId())
                    .dropItemName(randomBoxItem.getDropItemName())
                    .dropItemLevel(randomBoxItem.getDropItemLevel())
                    .fullDropItemName(randomBoxItem.getFullDropItemName())
                    .hasDropLevel(randomBoxItem.hasDropLevel())
                    .desireItemId(randomBoxItem.getDesireItem().getId())
                    .desireItemName(randomBoxItem.getDesireItem().getItemName())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private Long desireItemId;
        private String dropItemName;
        private Integer dropItemLevel;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String dropItemName;
        private Integer dropItemLevel;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleResponse {
        private Long id;
        private String dropItemName;
        private Integer dropItemLevel;
        private String fullDropItemName;

        public static SimpleResponse from(RandomBoxItem randomBoxItem) {
            return SimpleResponse.builder()
                    .id(randomBoxItem.getId())
                    .dropItemName(randomBoxItem.getDropItemName())
                    .dropItemLevel(randomBoxItem.getDropItemLevel())
                    .fullDropItemName(randomBoxItem.getFullDropItemName())
                    .build();
        }
    }
} 