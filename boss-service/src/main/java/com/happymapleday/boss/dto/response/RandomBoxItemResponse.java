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
    private Long desireItemId;
    private String desireItemName;

    public static RandomBoxItemResponse from(RandomBoxItem randomBoxItem) {
        return RandomBoxItemResponse.builder()
                .id(randomBoxItem.getId())
                .dropItemName(randomBoxItem.getDropItemName())
                .dropItemLevel(randomBoxItem.getDropItemLevel())
                .fullDropItemName(randomBoxItem.getFullDropItemName())
                .hasDropLevel(randomBoxItem.hasDropLevel())
                .desireItemId(randomBoxItem.getDesireItem().getId())
                .desireItemName(randomBoxItem.getDesireItem().getItemName())
                .build();
    }
} 