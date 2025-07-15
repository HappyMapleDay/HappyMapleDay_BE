package com.happymapleday.settlement.scheduler;

import com.happymapleday.settlement.entity.SettlementStatus;
import com.happymapleday.settlement.entity.WeeklySettlement;
import com.happymapleday.settlement.repository.WeeklySettlementRepository;
import com.happymapleday.settlement.service.util.WeekCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WeeklySettlementScheduler 테스트")
class WeeklySettlementSchedulerTest {

    @InjectMocks
    private WeeklySettlementScheduler scheduler;

    @Mock
    private WeeklySettlementRepository weeklySettlementRepository;

    @Mock
    private WeekCalculator weekCalculator;

    private LocalDate today;
    private LocalDate weekStartDate;
    private LocalDate previousWeekStartDate;
    private WeeklySettlement pendingSettlement;

    @BeforeEach
    void setUp() {
        today = LocalDate.of(2024, 1, 11); // 목요일
        weekStartDate = LocalDate.of(2024, 1, 11); // 현재 주 시작일
        previousWeekStartDate = LocalDate.of(2024, 1, 4); // 이전 주 시작일

        pendingSettlement = WeeklySettlement.builder()
                .id(1L)
                .userId(1L)
                .worldName("크로아")
                .weekStartDate(previousWeekStartDate)
                .totalCrystalIncome(BigInteger.valueOf(10000000))
                .totalDesireItemIncome(BigInteger.valueOf(5000000))
                .totalIncome(BigInteger.valueOf(15000000))
                .totalBossCount(5)
                .characterCount(2)
                .status(SettlementStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("자동 정산 실행 - PENDING 상태 정산 데이터가 있을 때")
    void processWeeklyAutoSettlement_WithPendingData_Success() {
        // given
        given(weekCalculator.getWeekStartDate(any(LocalDate.class))).willReturn(weekStartDate);
        given(weeklySettlementRepository.findByWeekStartDateAndStatus(
                previousWeekStartDate, SettlementStatus.PENDING))
                .willReturn(List.of(pendingSettlement));
        given(weeklySettlementRepository.save(any(WeeklySettlement.class)))
                .willReturn(pendingSettlement);

        // when
        scheduler.processWeeklyAutoSettlement();

        // then
        verify(weekCalculator).getWeekStartDate(any(LocalDate.class));
        verify(weeklySettlementRepository).findByWeekStartDateAndStatus(
                previousWeekStartDate, SettlementStatus.PENDING);
        verify(weeklySettlementRepository).save(any(WeeklySettlement.class));
    }

    @Test
    @DisplayName("자동 정산 실행 - PENDING 상태 정산 데이터가 없을 때")
    void processWeeklyAutoSettlement_WithoutPendingData_Success() {
        // given
        given(weekCalculator.getWeekStartDate(any(LocalDate.class))).willReturn(weekStartDate);
        given(weeklySettlementRepository.findByWeekStartDateAndStatus(
                previousWeekStartDate, SettlementStatus.PENDING))
                .willReturn(List.of());

        // when
        scheduler.processWeeklyAutoSettlement();

        // then
        verify(weekCalculator).getWeekStartDate(any(LocalDate.class));
        verify(weeklySettlementRepository).findByWeekStartDateAndStatus(
                previousWeekStartDate, SettlementStatus.PENDING);
        verify(weeklySettlementRepository, never()).save(any(WeeklySettlement.class));
    }

    @Test
    @DisplayName("자동 정산 실행 - 여러 PENDING 데이터 처리")
    void processWeeklyAutoSettlement_WithMultiplePendingData_Success() {
        // given
        WeeklySettlement pendingSettlement2 = WeeklySettlement.builder()
                .id(2L)
                .userId(2L)
                .worldName("리부트")
                .weekStartDate(previousWeekStartDate)
                .totalCrystalIncome(BigInteger.valueOf(8000000))
                .totalDesireItemIncome(BigInteger.valueOf(3000000))
                .totalIncome(BigInteger.valueOf(11000000))
                .totalBossCount(3)
                .characterCount(1)
                .status(SettlementStatus.PENDING)
                .build();

        given(weekCalculator.getWeekStartDate(any(LocalDate.class))).willReturn(weekStartDate);
        given(weeklySettlementRepository.findByWeekStartDateAndStatus(
                previousWeekStartDate, SettlementStatus.PENDING))
                .willReturn(List.of(pendingSettlement, pendingSettlement2));

        // when
        scheduler.processWeeklyAutoSettlement();

        // then
        verify(weekCalculator).getWeekStartDate(any(LocalDate.class));
        verify(weeklySettlementRepository).findByWeekStartDateAndStatus(
                previousWeekStartDate, SettlementStatus.PENDING);
        verify(weeklySettlementRepository, times(2)).save(any(WeeklySettlement.class));
    }

    @Test
    @DisplayName("자동 정산 실행 - 개별 정산 처리 중 오류 발생")
    void processWeeklyAutoSettlement_WithSaveError_ContinueProcessing() {
        // given
        WeeklySettlement pendingSettlement2 = WeeklySettlement.builder()
                .id(2L)
                .userId(2L)
                .worldName("리부트")
                .weekStartDate(previousWeekStartDate)
                .status(SettlementStatus.PENDING)
                .build();

        given(weekCalculator.getWeekStartDate(any(LocalDate.class))).willReturn(weekStartDate);
        given(weeklySettlementRepository.findByWeekStartDateAndStatus(
                previousWeekStartDate, SettlementStatus.PENDING))
                .willReturn(List.of(pendingSettlement, pendingSettlement2));
        
        // 첫 번째 저장은 성공, 두 번째 저장은 실패
        given(weeklySettlementRepository.save(any(WeeklySettlement.class)))
                .willReturn(pendingSettlement)
                .willThrow(new RuntimeException("DB 오류"));

        // when
        scheduler.processWeeklyAutoSettlement();

        // then
        verify(weeklySettlementRepository, times(2)).save(any(WeeklySettlement.class));
    }
} 