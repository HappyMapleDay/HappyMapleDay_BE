package com.happymapleday.boss.dto.response;

import com.happymapleday.boss.entity.DesireItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesireItemResponse {
    private Long id;
    private String itemName;
    private Boolean isRandomBox;
    private String fullItemName;
    private Long bossId;
    private String bossName;
    private String bossDifficulty;
    private List<RandomBoxItemResponse> randomBoxItems;

    public static DesireItemResponse from(DesireItem desireItem) {
        return DesireItemResponse.builder()
                .id(desireItem.getId())
                .itemName(desireItem.getItemName())
                .isRandomBox(desireItem.getIsRandomBox())
                .fullItemName(desireItem.getFullItemName())
                .bossId(desireItem.getBoss().getId())
                .bossName(desireItem.getBoss().getBossName())
                .bossDifficulty(desireItem.getBoss().getDifficulty())
                .build();
    }

    public static DesireItemResponse fromWithRandomBoxItems(DesireItem desireItem) {
        DesireItemResponse response = from(desireItem);
        if (desireItem.getRandomBoxItems() != null) {
            response.setRandomBoxItems(
                    desireItem.getRandomBoxItems().stream()
                            .map(RandomBoxItemResponse::from)
                            .toList()
            );
        }
        return response;
    }
} 