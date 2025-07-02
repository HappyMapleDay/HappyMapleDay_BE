package com.happymapleday.boss.dto.response;

import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.ForceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossResponse {
    private Long id;
    private String bossName;
    private String difficulty;
    private Long crystalPrice;
    private Integer maxPartySize;
    private Boolean isMonthly;
    private Boolean isActive;
    private Integer minEntryLevel;
    private Integer bossLevel;
    private ForceType requiredForceType;
    private Integer requiredForceAmount;
    private String fullName;
    private List<DesireItemResponse> desireItems;

    public static BossResponse from(Boss boss) {
        return BossResponse.builder()
                .id(boss.getId())
                .bossName(boss.getBossName())
                .difficulty(boss.getDifficulty())
                .crystalPrice(boss.getCrystalPrice())
                .maxPartySize(boss.getMaxPartySize())
                .isMonthly(boss.getIsMonthly())
                .isActive(boss.getIsActive())
                .minEntryLevel(boss.getMinEntryLevel())
                .bossLevel(boss.getBossLevel())
                .requiredForceType(boss.getRequiredForceType())
                .requiredForceAmount(boss.getRequiredForceAmount())
                .fullName(boss.getFullName())
                .build();
    }

    public static BossResponse fromWithDesireItems(Boss boss) {
        BossResponse response = from(boss);
        if (boss.getDesireItems() != null) {
            response.setDesireItems(
                    boss.getDesireItems().stream()
                            .map(DesireItemResponse::from)
                            .toList()
            );
        }
        return response;
    }
} 