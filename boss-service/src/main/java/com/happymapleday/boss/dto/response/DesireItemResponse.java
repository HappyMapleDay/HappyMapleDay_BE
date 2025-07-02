package com.happymapleday.boss.dto.response;

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
public class DesireItemResponse {
    private Long id;
    private String itemName;
    private Boolean isRandomBox;
    private String fullItemName;
    private Long bossId;
    private String bossName;
    private String bossDifficulty;
    private List<RandomBoxItemResponse> randomBoxItems;

    public static DesireItemResponse fromBossDropItem(BossDropItem bossDropItem) {
        DesireItemResponse response = DesireItemResponse.builder()
                .id(bossDropItem.getId())
                .itemName(bossDropItem.getItemName())
                .isRandomBox(bossDropItem.getIsRandomBox())
                .fullItemName(bossDropItem.getFullItemName())
                .bossId(bossDropItem.getBoss().getId())
                .bossName(bossDropItem.getBoss().getBossName())
                .bossDifficulty(bossDropItem.getBoss().getDifficulty())
                .build();
        
        if (bossDropItem.getItem().getRandomBoxItems() != null) {
            response.setRandomBoxItems(
                    bossDropItem.getItem().getRandomBoxItems().stream()
                            .map(randomBoxItem -> RandomBoxItemResponse.fromRandomBoxItem(randomBoxItem, bossDropItem.getId(), bossDropItem.getItemName()))
                            .toList()
            );
        }
        return response;
    }
}