package com.happymapleday.boss.dto;

import com.happymapleday.boss.entity.DesireItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class DesireItemDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String itemName;
        private Boolean isRandomBox;
        private String fullItemName;
        private Long bossId;
        private String bossName;
        private String bossDifficulty;
        private List<RandomBoxItemDto.Response> randomBoxItems;

        public static Response from(DesireItem desireItem) {
            return Response.builder()
                    .id(desireItem.getId())
                    .itemName(desireItem.getItemName())
                    .isRandomBox(desireItem.getIsRandomBox())
                    .fullItemName(desireItem.getFullItemName())
                    .bossId(desireItem.getBoss().getId())
                    .bossName(desireItem.getBoss().getBossName())
                    .bossDifficulty(desireItem.getBoss().getDifficulty())
                    .build();
        }

        public static Response fromWithRandomBoxItems(DesireItem desireItem) {
            Response response = from(desireItem);
            if (desireItem.getRandomBoxItems() != null) {
                response.setRandomBoxItems(
                        desireItem.getRandomBoxItems().stream()
                                .map(RandomBoxItemDto.Response::from)
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
        private Long bossId;
        private String itemName;
        private Boolean isRandomBox;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String itemName;
        private Boolean isRandomBox;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleResponse {
        private Long id;
        private String itemName;
        private Boolean isRandomBox;

        public static SimpleResponse from(DesireItem desireItem) {
            return SimpleResponse.builder()
                    .id(desireItem.getId())
                    .itemName(desireItem.getItemName())
                    .isRandomBox(desireItem.getIsRandomBox())
                    .build();
        }
    }
} 