package com.happymapleday.boss.dto.response;

import com.happymapleday.boss.entity.Boss;
import lombok.*;

@Getter
@Builder
public class BossSimpleResponse {
    private Long id;
    private String bossName;
    private String bossNameEn;
    private String difficulty;
    private String difficultyEn;
    private Long crystalPrice;
    private String fullName;

    public static BossSimpleResponse from(Boss boss) {
        return BossSimpleResponse.builder()
                .id(boss.getId())
                .bossName(boss.getBossName())
                .bossNameEn(boss.getBossNameEn())
                .difficulty(boss.getDifficulty())
                .difficultyEn(boss.getDifficultyEn())
                .crystalPrice(boss.getCrystalPrice())
                .fullName(boss.getFullName())
                .build();
    }
} 