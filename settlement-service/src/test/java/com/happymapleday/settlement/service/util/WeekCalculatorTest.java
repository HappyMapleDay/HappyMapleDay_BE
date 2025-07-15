package com.happymapleday.settlement.service.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class WeekCalculatorTest {
    
    private WeekCalculator weekCalculator;
    
    @BeforeEach
    void setUp() {
        weekCalculator = new WeekCalculator();
    }
    
    @Test
    @DisplayName("주차 시작일 계산 - 목요일")
    void getWeekStartDate_Thursday() {
        // given
        LocalDate thursday = LocalDate.of(2024, 1, 4); // 목요일
        
        // when
        LocalDate result = weekCalculator.getWeekStartDate(thursday);
        
        // then
        assertThat(result).isEqualTo(thursday);
    }
    
    @Test
    @DisplayName("주차 시작일 계산 - 금요일")
    void getWeekStartDate_Friday() {
        // given
        LocalDate friday = LocalDate.of(2024, 1, 5); // 금요일
        
        // when
        LocalDate result = weekCalculator.getWeekStartDate(friday);
        
        // then
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 4)); // 목요일
    }
    
    @Test
    @DisplayName("주차 시작일 계산 - 토요일")
    void getWeekStartDate_Saturday() {
        // given
        LocalDate saturday = LocalDate.of(2024, 1, 6); // 토요일
        
        // when
        LocalDate result = weekCalculator.getWeekStartDate(saturday);
        
        // then
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 4)); // 목요일
    }
    
    @Test
    @DisplayName("주차 시작일 계산 - 일요일")
    void getWeekStartDate_Sunday() {
        // given
        LocalDate sunday = LocalDate.of(2024, 1, 7); // 일요일
        
        // when
        LocalDate result = weekCalculator.getWeekStartDate(sunday);
        
        // then
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 4)); // 목요일
    }
    
    @Test
    @DisplayName("주차 시작일 계산 - 월요일")
    void getWeekStartDate_Monday() {
        // given
        LocalDate monday = LocalDate.of(2024, 1, 8); // 월요일
        
        // when
        LocalDate result = weekCalculator.getWeekStartDate(monday);
        
        // then
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 4)); // 목요일
    }
    
    @Test
    @DisplayName("주차 시작일 계산 - 화요일")
    void getWeekStartDate_Tuesday() {
        // given
        LocalDate tuesday = LocalDate.of(2024, 1, 9); // 화요일
        
        // when
        LocalDate result = weekCalculator.getWeekStartDate(tuesday);
        
        // then
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 4)); // 목요일
    }
    
    @Test
    @DisplayName("주차 시작일 계산 - 수요일")
    void getWeekStartDate_Wednesday() {
        // given
        LocalDate wednesday = LocalDate.of(2024, 1, 10); // 수요일
        
        // when
        LocalDate result = weekCalculator.getWeekStartDate(wednesday);
        
        // then
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 4)); // 목요일
    }
    
    @Test
    @DisplayName("다음 리셋 날짜 계산 - 목요일 이전")
    void getNextResetDate_BeforeThursday() {
        // given
        LocalDate wednesday = LocalDate.of(2024, 1, 10); // 수요일
        
        // when
        LocalDate result = weekCalculator.getNextResetDate(wednesday);
        
        // then
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 11)); // 다음 주 목요일
    }
    
    @Test
    @DisplayName("다음 리셋 날짜 계산 - 목요일")
    void getNextResetDate_OnThursday() {
        // given
        LocalDate thursday = LocalDate.of(2024, 1, 4); // 목요일
        
        // when
        LocalDate result = weekCalculator.getNextResetDate(thursday);
        
        // then
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 11)); // 다음 주 목요일
    }
    
    @Test
    @DisplayName("다음 리셋 날짜 계산 - 목요일 이후")
    void getNextResetDate_AfterThursday() {
        // given
        LocalDate friday = LocalDate.of(2024, 1, 5); // 금요일
        
        // when
        LocalDate result = weekCalculator.getNextResetDate(friday);
        
        // then
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 11)); // 다음 주 목요일
    }
    
    @Test
    @DisplayName("리셋까지 남은 일수 계산 - 목요일 이전")
    void getRemainingDays_BeforeThursday() {
        // given
        LocalDate wednesday = LocalDate.of(2024, 1, 10); // 수요일
        
        // when
        int result = weekCalculator.getRemainingDays(wednesday);
        
        // then
        assertThat(result).isEqualTo(1); // 1일 남음
    }
    
    @Test
    @DisplayName("리셋까지 남은 일수 계산 - 목요일")
    void getRemainingDays_OnThursday() {
        // given
        LocalDate thursday = LocalDate.of(2024, 1, 4); // 목요일
        
        // when
        int result = weekCalculator.getRemainingDays(thursday);
        
        // then
        assertThat(result).isEqualTo(7); // 7일 남음 (다음 주 목요일)
    }
    
    @Test
    @DisplayName("리셋까지 남은 일수 계산 - 목요일 이후")
    void getRemainingDays_AfterThursday() {
        // given
        LocalDate friday = LocalDate.of(2024, 1, 5); // 금요일
        
        // when
        int result = weekCalculator.getRemainingDays(friday);
        
        // then
        assertThat(result).isEqualTo(6); // 6일 남음
    }
    
    @Test
    @DisplayName("리셋까지 남은 일수 계산 - 일요일")
    void getRemainingDays_OnSunday() {
        // given
        LocalDate sunday = LocalDate.of(2024, 1, 7); // 일요일
        
        // when
        int result = weekCalculator.getRemainingDays(sunday);
        
        // then
        assertThat(result).isEqualTo(4); // 4일 남음
    }
    
    @Test
    @DisplayName("월말 주차 계산 - 경계 케이스")
    void getWeekStartDate_MonthBoundary() {
        // given
        LocalDate monthEnd = LocalDate.of(2024, 1, 31); // 1월 31일 (수요일)
        
        // when
        LocalDate result = weekCalculator.getWeekStartDate(monthEnd);
        
        // then
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 25)); // 1월 25일 목요일
    }
    
    @Test
    @DisplayName("연말 주차 계산 - 경계 케이스")
    void getWeekStartDate_YearBoundary() {
        // given
        LocalDate yearEnd = LocalDate.of(2024, 12, 31); // 12월 31일 (화요일)
        
        // when
        LocalDate result = weekCalculator.getWeekStartDate(yearEnd);
        
        // then
        assertThat(result).isEqualTo(LocalDate.of(2024, 12, 26)); // 12월 26일 목요일
    }
} 