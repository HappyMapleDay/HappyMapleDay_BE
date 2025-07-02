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
    private Long crystalPrice;
    private String fullName;

    public static BossSimpleResponse from(Boss boss) {
        return BossSimpleResponse.builder()
                .id(boss.getId())
                .bossName(boss.getBossName())
                .difficulty(boss.getDifficulty())
                .crystalPrice(boss.getCrystalPrice())
                .fullName(boss.getFullName())
                .build();
    }
} 