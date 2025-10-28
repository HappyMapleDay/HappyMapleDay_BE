package com.happymapleday.admin.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementCombatPowerDto {
    private Long bossId;
    private LocalDate date;
    private String characterClass;
    private BigDecimal avgCombatPower;
}

