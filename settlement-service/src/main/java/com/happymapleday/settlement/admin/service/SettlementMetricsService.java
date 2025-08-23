package com.happymapleday.settlement.admin.service;

import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesBigIntegerResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesDecimalResponse;

import java.time.LocalDate;
import java.util.List;

public interface SettlementMetricsService {

    List<TimeSeriesDecimalResponse> getAverageTotalIncomeByWeek(LocalDate from, LocalDate to);

    List<TimeSeriesDecimalResponse> getAverageCrystalIncomeByWeek(LocalDate from, LocalDate to);

    List<TimeSeriesDecimalResponse> getAverageDesireItemIncomeByWeek(LocalDate from, LocalDate to);

    List<TimeSeriesLongResponse> getBossKillCountsByWeek(Long bossId, LocalDate from, LocalDate to);

    List<TimeSeriesLongResponse> getItemDropCountByWeek(Long itemId, LocalDate from, LocalDate to);

    List<TimeSeriesBigIntegerResponse> getItemAveragePriceByWeek(Long itemId, LocalDate from, LocalDate to);

    java.util.List<java.util.Map<String, Object>> getTrimmedAvgCombatPowerByBossAndJob(Long bossId, String job,
                                                                                       LocalDate from, LocalDate to);
}


