package com.happymapleday.settlement.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class CurrentWeekStatusResponse {
    private final LocalDate currentWeekStart;
    private final LocalDate nextWeekStart;
    private final Boolean isCompleted;
    private final Integer remainingDays;
    private final LocalDateTime nextResetDate;
} 