package com.happymapleday.boss.dto.response;

import com.happymapleday.boss.entity.Boss;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossSimpleResponse {
    private Long id;
    private String bossName;
    private String difficulty;
    private String fullName;
    private Long crystalPrice;

    public static BossSimpleResponse from(Boss boss) {
        return BossSimpleResponse.builder()
                .id(boss.getId())
                .bossName(boss.getBossName())
                .difficulty(boss.getDifficulty())
                .fullName(boss.getFullName())
                .crystalPrice(boss.getCrystalPrice())
                .build();
    }
} 