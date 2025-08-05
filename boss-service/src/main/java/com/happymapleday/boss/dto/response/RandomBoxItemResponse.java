package com.happymapleday.boss.dto.response;

import com.happymapleday.boss.entity.RandomBoxItem;
import lombok.*;

@Getter
@Builder
public class RandomBoxItemResponse {
    private Long randomBoxItemId;
    private String dropItemName;
    private String dropItemNameEn;
    private Integer dropItemLevel;
    private String fullDropItemName;
    private Boolean hasDropLevel;

    public static RandomBoxItemResponse from(RandomBoxItem randomBoxItem) {
        return RandomBoxItemResponse.builder()
                .randomBoxItemId(randomBoxItem.getBoxContentItem().getId())
                .dropItemName(randomBoxItem.getDropItemName())
                .dropItemNameEn(randomBoxItem.getDropItemNameEn())
                .dropItemLevel(randomBoxItem.getDropItemLevel())
                .fullDropItemName(randomBoxItem.getFullDropItemName())
                .hasDropLevel(randomBoxItem.hasDropLevel())
                .build();
    }

    public static RandomBoxItemResponse fromRandomBoxItem(RandomBoxItem randomBoxItem) {
        return RandomBoxItemResponse.builder()
                .randomBoxItemId(randomBoxItem.getBoxContentItem().getId())
                .dropItemName(randomBoxItem.getDropItemName())
                .dropItemNameEn(randomBoxItem.getDropItemNameEn())
                .dropItemLevel(randomBoxItem.getDropItemLevel())
                .fullDropItemName(randomBoxItem.getFullDropItemName())
                .hasDropLevel(randomBoxItem.hasDropLevel())
                .build();
    }
} 