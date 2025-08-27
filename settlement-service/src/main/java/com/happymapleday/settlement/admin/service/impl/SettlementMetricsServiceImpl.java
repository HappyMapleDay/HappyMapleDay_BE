package com.happymapleday.settlement.admin.service.impl;

import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesBigIntegerResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesDecimalResponse;
import com.happymapleday.settlement.admin.repository.AdminDesireItemRecordQueryRepository;
import com.happymapleday.settlement.admin.repository.AdminWeeklyBossRecordQueryRepository;
import com.happymapleday.settlement.admin.repository.AdminWeeklySettlementQueryRepository;
import com.happymapleday.settlement.admin.repository.projection.DateBigIntegerValue;
import com.happymapleday.settlement.admin.repository.projection.DateBigDecimalValue;
import com.happymapleday.settlement.admin.repository.projection.DateLongValue;
import com.happymapleday.settlement.admin.repository.projection.IdLongValue;
import com.happymapleday.settlement.admin.service.SettlementMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SettlementMetricsServiceImpl implements SettlementMetricsService {

    private final AdminWeeklySettlementQueryRepository weeklySettlementRepository;
    private final AdminWeeklyBossRecordQueryRepository weeklyBossRecordRepository;
    private final AdminDesireItemRecordQueryRepository desireItemRecordRepository;

    // 주차별 평균 총수익
    @Override
    public List<TimeSeriesDecimalResponse> getAverageTotalIncomeByWeek(LocalDate from, LocalDate to) {
        List<DateBigDecimalValue> rows = weeklySettlementRepository.findAverageTotalIncomeByWeek(from, to);
        return rows.stream()
                .map(r -> TimeSeriesDecimalResponse.builder().date(r.getDate()).value(r.getValue()).build())
                .collect(Collectors.toList());
    }

    // 주차별 평균 결정석 수익
    @Override
    public List<TimeSeriesDecimalResponse> getAverageCrystalIncomeByWeek(LocalDate from, LocalDate to) {
        List<DateBigDecimalValue> rows = weeklySettlementRepository.findAverageCrystalIncomeByWeek(from, to);
        return rows.stream()
                .map(r -> TimeSeriesDecimalResponse.builder().date(r.getDate()).value(r.getValue()).build())
                .collect(Collectors.toList());
    }

    // 주차별 평균 물욕템 수익
    @Override
    public List<TimeSeriesDecimalResponse> getAverageDesireItemIncomeByWeek(LocalDate from, LocalDate to) {
        List<DateBigDecimalValue> rows = weeklySettlementRepository.findAverageDesireItemIncomeByWeek(from, to);
        return rows.stream()
                .map(r -> TimeSeriesDecimalResponse.builder().date(r.getDate()).value(r.getValue()).build())
                .collect(Collectors.toList());
    }

    // 주차별 보스 처치 횟수
    @Override
    public List<TimeSeriesLongResponse> getBossKillCountsByWeek(Long bossId, LocalDate from, LocalDate to) {
        List<DateLongValue> rows = weeklyBossRecordRepository.findBossKillCountsByWeek(bossId, from, to);
        return rows.stream()
                .map(r -> TimeSeriesLongResponse.builder().date(r.getDate()).value(r.getValue()).build())
                .collect(Collectors.toList());
    }

    // 주차별 물욕템 드랍 횟수
    @Override
    public List<TimeSeriesLongResponse> getItemDropCountByWeek(Long itemId, LocalDate from, LocalDate to) {
        List<DateLongValue> rows = desireItemRecordRepository.findItemDropCountByWeek(itemId, from, to);
        return rows.stream()
                .map(r -> TimeSeriesLongResponse.builder().date(r.getDate()).value(r.getValue()).build())
                .collect(Collectors.toList());
    }

    // 주차별 물욕템 평균 판매가격
    @Override
    public List<TimeSeriesBigIntegerResponse> getItemAveragePriceByWeek(Long itemId, LocalDate from, LocalDate to) {
        List<DateBigIntegerValue> rows = desireItemRecordRepository.findItemAveragePriceByWeek(itemId, from, to);
        return rows.stream()
                .map(r -> TimeSeriesBigIntegerResponse.builder().date(r.getDate()).value(r.getValue()).build())
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTrimmedAvgCombatPowerByBossAndJob(Long bossId, String job, LocalDate from, LocalDate to) {
        return weeklyBossRecordRepository.findTrimmedAvgCombatPowerByBossAndJob(bossId, job, from, to);
    }

    @Override
    public List<Map<String, Object>> summarizeBossKillCounts(LocalDate from, LocalDate to) {
        List<IdLongValue> rows = weeklyBossRecordRepository.summarizeBossKillCounts(from, to);
        return rows.stream()
                .map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("bossId", r.getId());
                    m.put("count", r.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> summarizeItemDropsByBoss(Long bossId, LocalDate from, LocalDate to) {
        List<IdLongValue> rows = desireItemRecordRepository.summarizeItemDropsByBoss(bossId, from, to);
        return rows.stream()
                .map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("itemId", r.getId());
                    m.put("count", r.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }
}


