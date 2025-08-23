package com.happymapleday.settlement.admin.service.impl;

import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesLongResponse;
import com.happymapleday.settlement.admin.repository.AdminDesireItemRecordQueryRepository;
import com.happymapleday.settlement.admin.repository.AdminWeeklyBossRecordQueryRepository;
import com.happymapleday.settlement.admin.repository.projection.DateLongValue;
import com.happymapleday.settlement.admin.service.SettlementMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SettlementMetricsServiceImpl implements SettlementMetricsService {

    private final AdminWeeklyBossRecordQueryRepository weeklyBossRecordRepository;
    private final AdminDesireItemRecordQueryRepository desireItemRecordRepository;

    // 평균 총/결정석/물욕템 수익 타임시리즈 제거

    // 주차별 보스 처치 횟수
    @Override
    public List<TimeSeriesLongResponse> getBossKillCountsByWeek(Long bossId, LocalDate from, LocalDate to) {
        List<DateLongValue> rows = weeklyBossRecordRepository.findBossKillCountsByWeek(bossId, from, to);
        return rows.stream()
                .map(r -> TimeSeriesLongResponse.builder().date(r.getDate()).value(r.getValue()).build())
                .collect(Collectors.toList());
    }

    // 아이템 드랍/평균가 타임시리즈 제거
    @Override
    public List<Map<String, Object>> summarizeBossKillCounts(LocalDate from, LocalDate to) {
        return weeklyBossRecordRepository.summarizeBossKillCounts(from, to);
    }

    @Override
    public List<Map<String, Object>> summarizeItemDropsByBoss(Long bossId, LocalDate from, LocalDate to) {
        return desireItemRecordRepository.summarizeItemDropsByBoss(bossId, from, to);
    }

    @Override
    public List<Map<String, Object>> summarizeBoxContentsByBoss(Long bossId, Long boxItemId, LocalDate from, LocalDate to) {
        return desireItemRecordRepository.summarizeBoxContentsByBoss(bossId, boxItemId, from, to);
    }

    @Override
    public List<Map<String, Object>> summarizeItemAveragePrice(Long bossId, Long itemId, LocalDate from, LocalDate to) {
        return desireItemRecordRepository.summarizeItemAveragePrice(bossId, itemId, from, to);
    }

    @Override
    public Map<String, Object> summarizeBossHardness(Long bossId, LocalDate from, LocalDate to) {
        Map<String, Object> total = weeklyBossRecordRepository.summarizeBossHardnessTotal(bossId, from, to);
        List<Map<String, Object>> byJob = weeklyBossRecordRepository.summarizeBossHardnessByJob(bossId, from, to);
        java.util.HashMap<String, Object> result = new java.util.HashMap<>();
        result.put("totalCount", total != null ? total.get("totalCount") : 0L);
        result.put("byJob", byJob);
        return result;
    }

    @Override
    public List<Map<String, Object>> getTrimmedAvgCombatPowerGroupByBossAndJob(LocalDate from, LocalDate to) {
        return weeklyBossRecordRepository.findTrimmedAvgCombatPowerByBossGroupByJob(from, to);
    }
}


