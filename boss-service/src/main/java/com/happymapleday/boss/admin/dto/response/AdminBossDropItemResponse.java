package com.happymapleday.boss.admin.dto.response;

import com.happymapleday.boss.entity.BossDropItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminBossDropItemResponse {
    private Long id;
    private Long bossId;
    private String bossName;
    private String difficulty;
    private Long itemId;
    private String itemName;
    private Boolean isRandomBox;
    private List<AdminRandomBoxItemResponse> randomBoxItems;

    public static AdminBossDropItemResponse from(BossDropItem bossDropItem) {
        return AdminBossDropItemResponse.builder()
                .id(bossDropItem.getId())
                .bossId(bossDropItem.getBoss().getId())
                .bossName(bossDropItem.getBoss().getBossName())
                .difficulty(bossDropItem.getBoss().getDifficulty())
                .itemId(bossDropItem.getItem().getId())
                .itemName(bossDropItem.getItem().getItemName())
                .isRandomBox(bossDropItem.getItem().getIsRandomBox())
                .build();
    }

    public static AdminBossDropItemResponse fromWithRandomBoxItems(BossDropItem bossDropItem) {
        AdminBossDropItemResponse response = from(bossDropItem);
        if (bossDropItem.getItem().getIsRandomBox() && bossDropItem.getItem().getRandomBoxItems() != null) {
            response.setRandomBoxItems(
                    bossDropItem.getItem().getRandomBoxItems().stream()
                            .map(AdminRandomBoxItemResponse::from)
                            .toList()
            );
        }
        return response;
    }
} 