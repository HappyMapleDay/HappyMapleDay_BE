package com.happymapleday.boss.dto.response;

import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.ForceType;
import lombok.*;

import java.util.List;

@Getter
@Builder
public class BossResponse {
    private Long bossId;
    private String bossName;
    private String bossNameEn;
    private String difficulty;
    private String difficultyEn;
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
                .bossId(boss.getId())
                .bossName(boss.getBossName())
                .bossNameEn(boss.getBossNameEn())
                .difficulty(boss.getDifficulty())
                .difficultyEn(boss.getDifficultyEn())
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
        List<DesireItemResponse> desireItems = null;
        if (boss.getBossDropItems() != null) {
            desireItems = boss.getBossDropItems().stream()
                    .map(DesireItemResponse::fromBossDropItem)
                    .toList();
        }
        
        return BossResponse.builder()
                .bossId(boss.getId())
                .bossName(boss.getBossName())
                .bossNameEn(boss.getBossNameEn())
                .difficulty(boss.getDifficulty())
                .difficultyEn(boss.getDifficultyEn())
                .crystalPrice(boss.getCrystalPrice())
                .maxPartySize(boss.getMaxPartySize())
                .isMonthly(boss.getIsMonthly())
                .isActive(boss.getIsActive())
                .minEntryLevel(boss.getMinEntryLevel())
                .bossLevel(boss.getBossLevel())
                .requiredForceType(boss.getRequiredForceType())
                .requiredForceAmount(boss.getRequiredForceAmount())
                .fullName(boss.getFullName())
                .desireItems(desireItems)
                .build();
    }
}