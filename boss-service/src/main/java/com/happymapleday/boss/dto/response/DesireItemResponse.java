package com.happymapleday.boss.dto.response;

import com.happymapleday.boss.entity.BossDropItem;
import lombok.*;

import java.util.List;

@Getter
@Builder
public class DesireItemResponse {
    private Long desireItemId;
    private String itemName;
    private String itemNameEn;
    private Boolean isRandomBox;
    private String fullItemName;
    private Long bossId;
    private String bossName;
    private String bossNameEn;
    private String bossDifficulty;
    private String bossDifficultyEn;
    private List<RandomBoxItemResponse> randomBoxItems;

    public static DesireItemResponse fromBossDropItem(BossDropItem bossDropItem) {
        List<RandomBoxItemResponse> randomBoxItems = null;
        if (bossDropItem.getItem().getRandomBoxItems() != null) {
            randomBoxItems = bossDropItem.getItem().getRandomBoxItems().stream()
                    .map(randomBoxItem -> RandomBoxItemResponse.fromRandomBoxItem(randomBoxItem))
                    .toList();
        }
        
        return DesireItemResponse.builder()
                .desireItemId(bossDropItem.getItem().getId())
                .itemName(bossDropItem.getItemName())
                .itemNameEn(bossDropItem.getItemNameEn())
                .isRandomBox(bossDropItem.getIsRandomBox())
                .fullItemName(bossDropItem.getFullItemName())
                .bossId(bossDropItem.getBoss().getId())
                .bossName(bossDropItem.getBoss().getBossName())
                .bossNameEn(bossDropItem.getBoss().getBossNameEn())
                .bossDifficulty(bossDropItem.getBoss().getDifficulty())
                .bossDifficultyEn(bossDropItem.getBoss().getDifficultyEn())
                .randomBoxItems(randomBoxItems)
                .build();
    }
}