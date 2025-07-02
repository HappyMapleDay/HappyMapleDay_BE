package com.happymapleday.boss.dto.response;

import com.happymapleday.boss.entity.RandomBoxItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RandomBoxItemResponse {
    private Long id;
    private String dropItemName;
    private Integer dropItemLevel;
    private String fullDropItemName;
    private Boolean hasDropLevel;
    private Long itemId;
    private String itemName;

    public static RandomBoxItemResponse from(RandomBoxItem randomBoxItem) {
        return RandomBoxItemResponse.builder()
                .id(randomBoxItem.getId())
                .dropItemName(randomBoxItem.getDropItemName())
                .dropItemLevel(randomBoxItem.getDropItemLevel())
                .fullDropItemName(randomBoxItem.getFullDropItemName())
                .hasDropLevel(randomBoxItem.hasDropLevel())
                .itemId(randomBoxItem.getItem().getId())
                .itemName(randomBoxItem.getItem().getItemName())
                .build();
    }

    // 더 이상 사용하지 않는 메서드 - Template 구조 제거됨

    public static RandomBoxItemResponse fromRandomBoxItem(RandomBoxItem randomBoxItem, Long bossDropItemId, String itemName) {
        return RandomBoxItemResponse.builder()
                .id(randomBoxItem.getId())
                .dropItemName(randomBoxItem.getDropItemName())
                .dropItemLevel(randomBoxItem.getDropItemLevel())
                .fullDropItemName(randomBoxItem.getFullDropItemName())
                .hasDropLevel(randomBoxItem.hasDropLevel())
                .itemId(bossDropItemId)
                .itemName(itemName)
                .build();
    }
} 