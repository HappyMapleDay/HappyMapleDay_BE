package com.happymapleday.boss.admin.dto.response;

import com.happymapleday.boss.entity.RandomBoxItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRandomBoxItemResponse {
    private Long id;
    private Long itemId;
    private String dropItemName;
    private Integer dropItemLevel;

    public static AdminRandomBoxItemResponse from(RandomBoxItem randomBoxItem) {
        return AdminRandomBoxItemResponse.builder()
                .id(randomBoxItem.getBoxContentItem().getId())
                .itemId(randomBoxItem.getItem().getId())
                .dropItemName(randomBoxItem.getDropItemName())
                .dropItemLevel(randomBoxItem.getDropItemLevel())
                .build();
    }
} 