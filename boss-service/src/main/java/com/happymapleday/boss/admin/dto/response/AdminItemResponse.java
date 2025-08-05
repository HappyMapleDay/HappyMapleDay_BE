package com.happymapleday.boss.admin.dto.response;

import com.happymapleday.boss.entity.Item;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminItemResponse {
    private Long adminItemId;
    private String itemName;
    private String itemNameEn;
    private Boolean isRandomBox;

    public static AdminItemResponse from(Item item) {
        return AdminItemResponse.builder()
                .adminItemId(item.getId())
                .itemName(item.getItemName())
                .itemNameEn(item.getItemNameEn())
                .isRandomBox(item.getIsRandomBox())
                .build();
    }
} 