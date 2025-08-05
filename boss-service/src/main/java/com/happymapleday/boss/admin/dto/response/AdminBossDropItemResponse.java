package com.happymapleday.boss.admin.dto.response;

import com.happymapleday.boss.entity.BossDropItem;
import lombok.*;

import java.util.List;


@Getter
@Builder
public class AdminBossDropItemResponse {
    private Long adminBossDropDataId;
    private Long bossId;
    private String bossName;
    private String bossNameEn;
    private String difficulty;
    private String difficultyEn;
    private Long itemId;
    private String itemName;
    private String itemNameEn;
    private Boolean isRandomBox;
    private List<AdminRandomBoxItemResponse> randomBoxItems;

    public static AdminBossDropItemResponse from(BossDropItem bossDropItem) {
        return AdminBossDropItemResponse.builder()
                .adminBossDropDataId(bossDropItem.getId())
                .bossId(bossDropItem.getBoss().getId())
                .bossName(bossDropItem.getBoss().getBossName())
                .bossNameEn(bossDropItem.getBoss().getBossNameEn())
                .difficulty(bossDropItem.getBoss().getDifficulty())
                .difficultyEn(bossDropItem.getBoss().getDifficultyEn())
                .itemId(bossDropItem.getItem().getId())
                .itemName(bossDropItem.getItem().getItemName())
                .itemNameEn(bossDropItem.getItem().getItemNameEn())
                .isRandomBox(bossDropItem.getItem().getIsRandomBox())
                .build();
    }

    public static AdminBossDropItemResponse fromWithRandomBoxItems(BossDropItem bossDropItem) {
        List<AdminRandomBoxItemResponse> randomBoxItems = null;
        if (bossDropItem.getItem().getIsRandomBox() && bossDropItem.getItem().getRandomBoxItems() != null) {
            randomBoxItems = bossDropItem.getItem().getRandomBoxItems().stream()
                    .map(AdminRandomBoxItemResponse::from)
                    .toList();
        }
        
        return AdminBossDropItemResponse.builder()
                .adminBossDropDataId(bossDropItem.getId())
                .bossId(bossDropItem.getBoss().getId())
                .bossName(bossDropItem.getBoss().getBossName())
                .bossNameEn(bossDropItem.getBoss().getBossNameEn())
                .difficulty(bossDropItem.getBoss().getDifficulty())
                .difficultyEn(bossDropItem.getBoss().getDifficultyEn())
                .itemId(bossDropItem.getItem().getId())
                .itemName(bossDropItem.getItem().getItemName())
                .itemNameEn(bossDropItem.getItem().getItemNameEn())
                .isRandomBox(bossDropItem.getItem().getIsRandomBox())
                .randomBoxItems(randomBoxItems)
                .build();
    }
} 