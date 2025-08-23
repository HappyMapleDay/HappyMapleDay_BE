package com.happymapleday.settlement.admin.dto.response.metrics;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Builder
public class TimeSeriesBigIntegerResponse {

    private LocalDate date;
    private BigInteger value;
}


