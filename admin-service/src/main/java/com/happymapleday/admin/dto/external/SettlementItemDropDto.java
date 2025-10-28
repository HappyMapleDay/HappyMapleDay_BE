package com.happymapleday.admin.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementItemDropDto {
    private Long bossId;
    private LocalDate date;
    private Long itemId;
    private Long dropCount;
    private Long count;

    public Long getDropCount() {
        return dropCount != null ? dropCount : count;
    }
}

