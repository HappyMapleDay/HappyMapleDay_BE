package com.happymapleday.settlement.admin.dto.response.metrics;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class TimeSeriesDecimalResponse {

    private LocalDate date;
    private BigDecimal value;
}


