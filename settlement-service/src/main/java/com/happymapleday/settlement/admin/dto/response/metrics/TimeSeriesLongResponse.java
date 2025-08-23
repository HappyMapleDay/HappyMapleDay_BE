package com.happymapleday.settlement.admin.dto.response.metrics;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TimeSeriesLongResponse {

    private LocalDate date;
    private Long value;
}


