package com.happymapleday.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossCombatPowerMetricsResponse {
    private LocalDate metricDate;
    private Long bossId;
    private String bossName;
    private String bossNameEn;
    private String difficulty;
    private String characterClass;
    private BigDecimal avgCombatPower;
}

