package com.happymapleday.settlement.admin.dto.response.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvgCombatPowerByBossJobResponse {
    private Long bossId;
    private String characterClass;
    private Double avgCombatPower;
}
