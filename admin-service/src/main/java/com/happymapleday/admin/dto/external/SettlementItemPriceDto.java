package com.happymapleday.admin.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementItemPriceDto {
    private Long bossId;
    private LocalDate date;
    private Long itemId;
    private BigDecimal avgPrice;
}

