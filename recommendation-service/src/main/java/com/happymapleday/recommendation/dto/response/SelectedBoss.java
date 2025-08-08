package com.happymapleday.recommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectedBoss {
    private Long bossId;
    private String bossName;
    private String difficulty;
    private Long crystalPrice;
    private Integer partySize;
    private Boolean forcedIncluded; // 파티 2인 이상 또는 이미 클리어로 인한 강제 포함
}


