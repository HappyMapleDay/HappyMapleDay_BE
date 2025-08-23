package com.happymapleday.settlement.admin.service;

import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesBigIntegerResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesDecimalResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SettlementMetricsService {

    List<TimeSeriesDecimalResponse> getAverageTotalIncomeByWeek(LocalDate from, LocalDate to);

    List<TimeSeriesDecimalResponse> getAverageCrystalIncomeByWeek(LocalDate from, LocalDate to);

    List<TimeSeriesDecimalResponse> getAverageDesireItemIncomeByWeek(LocalDate from, LocalDate to);

    List<TimeSeriesLongResponse> getBossKillCountsByWeek(Long bossId, LocalDate from, LocalDate to);

    List<TimeSeriesLongResponse> getItemDropCountByWeek(Long itemId, LocalDate from, LocalDate to);

    List<TimeSeriesBigIntegerResponse> getItemAveragePriceByWeek(Long itemId, LocalDate from, LocalDate to);

    List<Map<String, Object>> getTrimmedAvgCombatPowerByBossAndJob(Long bossId, String job,
                                                                                       LocalDate from, LocalDate to);

    // 보스별 총 처치 수 요약
    List<Map<String, Object>> summarizeBossKillCounts(LocalDate from, LocalDate to);

    // 보스별 아이템별 총 드랍 수 요약 (결과 아이템 기준)
    List<Map<String, Object>> summarizeItemDropsByBoss(Long bossId, LocalDate from, LocalDate to);
}


