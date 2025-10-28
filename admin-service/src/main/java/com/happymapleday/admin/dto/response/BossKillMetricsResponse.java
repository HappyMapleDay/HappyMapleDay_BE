package com.happymapleday.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossKillMetricsResponse {
    private LocalDate metricDate;
    private Long bossId;
    private String bossName;
    private String bossNameEn;
    private String difficulty;
    private Long totalKills;
}

