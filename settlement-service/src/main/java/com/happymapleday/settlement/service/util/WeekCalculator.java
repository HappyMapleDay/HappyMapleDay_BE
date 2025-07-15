package com.happymapleday.settlement.service.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class WeekCalculator {
    
    // 목요일을 기준으로 주차 시작일 계산
    public LocalDate getWeekStartDate(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue(); // 월=1, 화=2, 수=3, 목=4, 금=5, 토=6, 일=7
        int daysFromThursday = (dayOfWeek + 3) % 7; // 목요일을 0으로 만들기 위한 계산
        return date.minusDays(daysFromThursday);
    }
    
    // 다음 리셋 날짜 계산
    public LocalDate getNextResetDate(LocalDate today) {
        LocalDate currentWeekStart = getWeekStartDate(today);
        LocalDate nextWeekStart = currentWeekStart.plusWeeks(1);
        
        if (today.isBefore(currentWeekStart)) {
            // 현재 날짜가 이번 주 목요일 이전이면 → 이번 주 목요일
            return currentWeekStart;
        } else {
            // 현재 날짜가 이번 주 목요일 이후이거나 같으면 → 다음 주 목요일
            return nextWeekStart;
        }
    }
    
    // 리셋까지 남은 일수 계산
    public int getRemainingDays(LocalDate today) {
        LocalDate nextResetDate = getNextResetDate(today);
        return (int) ChronoUnit.DAYS.between(today, nextResetDate);
    }
} 