package com.happymapleday.boss.admin.dto.response;

import com.happymapleday.boss.entity.Item;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminItemResponse {
    private Long id;
    private String itemName;
    private Boolean isRandomBox;

    public static AdminItemResponse from(Item item) {
        return AdminItemResponse.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .isRandomBox(item.getIsRandomBox())
                .build();
    }
} 