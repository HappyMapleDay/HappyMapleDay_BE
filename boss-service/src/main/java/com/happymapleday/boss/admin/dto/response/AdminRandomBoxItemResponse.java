package com.happymapleday.boss.admin.dto.response;

import com.happymapleday.boss.entity.RandomBoxItem;
import lombok.*;

@Getter
@Builder
public class AdminRandomBoxItemResponse {
    private Long adminRandomBoxItemId;
    private Long itemId;
    private String dropItemName;
    private String dropItemNameEn;
    private Integer dropItemLevel;

    public static AdminRandomBoxItemResponse from(RandomBoxItem randomBoxItem) {
        return AdminRandomBoxItemResponse.builder()
                .adminRandomBoxItemId(randomBoxItem.getBoxContentItem().getId())
                .itemId(randomBoxItem.getItem().getId())
                .dropItemName(randomBoxItem.getDropItemName())
                .dropItemNameEn(randomBoxItem.getDropItemNameEn())
                .dropItemLevel(randomBoxItem.getDropItemLevel())
                .build();
    }
} 