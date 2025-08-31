package com.happymapleday.settlement.admin.dto.response.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesBossLongResponse {

    private Long bossId;
    private LocalDate date;
    private Long value;
}


