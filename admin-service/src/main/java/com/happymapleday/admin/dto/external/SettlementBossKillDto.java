package com.happymapleday.admin.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementBossKillDto {
    private Long bossId;
    private LocalDate date;
    private Long totalKills;
    private Long value;

    public Long getTotalKills() {
        return totalKills != null ? totalKills : value;
    }
}

