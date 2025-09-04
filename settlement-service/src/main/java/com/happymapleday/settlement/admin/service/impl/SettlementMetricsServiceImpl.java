package com.happymapleday.settlement.admin.service.impl;

import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesBossLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossKillCountSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.AvgCombatPowerByBossJobResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossItemCountResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossItemAvgPriceResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossPartyRatioResponse;
import com.happymapleday.settlement.admin.repository.AdminDesireItemRecordQueryRepository;
import com.happymapleday.settlement.admin.repository.AdminWeeklyBossRecordQueryRepository;
import com.happymapleday.settlement.admin.repository.projection.DateLongValue;
import com.happymapleday.settlement.admin.service.SettlementMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SettlementMetricsServiceImpl implements SettlementMetricsService {

    private final AdminWeeklyBossRecordQueryRepository weeklyBossRecordRepository;
    private final AdminDesireItemRecordQueryRepository desireItemRecordRepository;

    // 주차별 보스 처치 횟수 - bossId 없으면 전체 보스별 반환
    @Override
    public List<TimeSeriesBossLongResponse> getBossKillCountsByWeek(Long bossId, LocalDate from, LocalDate to) {
        if (bossId != null) {
            List<DateLongValue> rows = weeklyBossRecordRepository.findBossKillCountsByWeek(bossId, from, to);
            // 특정 보스(31, 32)는 월별 집계로 전환
            if (bossId == 31L || bossId == 32L) {
                Map<YearMonth, Long> ymToSum = new LinkedHashMap<>();
                for (DateLongValue r : rows) {
                    YearMonth ym = YearMonth.from(r.getDate());
                    Long v = r.getValue() == null ? 0L : r.getValue();
                    ymToSum.put(ym, ymToSum.getOrDefault(ym, 0L) + v);
                }
                List<TimeSeriesBossLongResponse> monthly = new ArrayList<>();
                for (Map.Entry<YearMonth, Long> e : ymToSum.entrySet()) {
                    monthly.add(TimeSeriesBossLongResponse.builder()
                            .bossId(bossId)
                            .date(e.getKey().atDay(1))
                            .value(e.getValue())
                            .build());
                }
                return monthly;
            }
            return rows.stream()
                    .map(r -> TimeSeriesBossLongResponse.builder()
                            .bossId(bossId)
                            .date(r.getDate())
                            .value(r.getValue())
                            .build())
                    .collect(Collectors.toList());
        }
        List<Map<String, Object>> rows = weeklyBossRecordRepository.findBossKillCountsByWeekGroupByBoss(null, from, to);
        return rows.stream()
                .map(r -> {
                    Long bossIdVal = ((Number) r.get("bossId")).longValue();
                    Object dateObj = r.get("date");
                    LocalDate dateVal;
                    if (dateObj instanceof Date) {
                        dateVal = ((Date) dateObj).toLocalDate();
                    } else if (dateObj instanceof LocalDate) {
                        dateVal = (LocalDate) dateObj;
                    } else {
                        dateVal = LocalDate.parse(String.valueOf(dateObj));
                    }
                    Long valueVal = ((Number) r.get("value")).longValue();
                    return TimeSeriesBossLongResponse.builder()
                            .bossId(bossIdVal)
                            .date(dateVal)
                            .value(valueVal)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BossKillCountSummaryResponse> summarizeBossKillCounts(LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = weeklyBossRecordRepository.summarizeBossKillCounts(from, to);
        return rows.stream()
                .map(r -> BossKillCountSummaryResponse.builder()
                        .bossId(((Number) r.get("bossId")).longValue())
                        .count(((Number) r.get("count")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<BossItemCountResponse> summarizeItemDropsByBoss(Long bossId, LocalDate from, LocalDate to) {
        if (bossId != null) {
            List<Map<String, Object>> rows = desireItemRecordRepository.summarizeItemDropsByBoss(bossId, from, to);
            return rows.stream()
                    .map(r -> BossItemCountResponse.builder()
                            .bossId(bossId)
                            .itemId(((Number) r.get("itemId")).longValue())
                            .count(((Number) r.get("count")).longValue())
                            .build())
                    .collect(Collectors.toList());
        }
        List<Map<String, Object>> rows = desireItemRecordRepository.summarizeItemDropsGroupByBoss(null, from, to);
        return rows.stream()
                .map(r -> BossItemCountResponse.builder()
                        .bossId(((Number) r.get("bossId")).longValue())
                        .itemId(((Number) r.get("itemId")).longValue())
                        .count(((Number) r.get("count")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<BossItemCountResponse> summarizeBoxContentsByBoss(Long bossId, Long boxItemId, LocalDate from, LocalDate to) {
        if (bossId != null) {
            List<Map<String, Object>> rows = desireItemRecordRepository.summarizeBoxContentsByBoss(bossId, boxItemId, from, to);
            return rows.stream()
                    .map(r -> BossItemCountResponse.builder()
                            .bossId(bossId)
                            .itemId(((Number) r.get("itemId")).longValue())
                            .count(((Number) r.get("count")).longValue())
                            .build())
                    .collect(Collectors.toList());
        }
        List<Map<String, Object>> rows = desireItemRecordRepository.summarizeBoxContentsGroupByBoss(null, boxItemId, from, to);
        return rows.stream()
                .map(r -> BossItemCountResponse.builder()
                        .bossId(((Number) r.get("bossId")).longValue())
                        .itemId(((Number) r.get("itemId")).longValue())
                        .count(((Number) r.get("count")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<BossItemAvgPriceResponse> summarizeItemAveragePrice(Long bossId, Long itemId, LocalDate from, LocalDate to) {
        if (bossId != null) {
            List<Map<String, Object>> rows = desireItemRecordRepository.summarizeItemAveragePrice(bossId, itemId, from, to);
            return rows.stream()
                    .map(r -> BossItemAvgPriceResponse.builder()
                            .bossId(bossId)
                            .itemId(((Number) r.get("itemId")).longValue())
                            .avgPrice(new BigDecimal(r.get("avgPrice").toString()))
                            .build())
                    .collect(Collectors.toList());
        }
        List<Map<String, Object>> rows = desireItemRecordRepository.summarizeItemAveragePriceGroupByBoss(null, itemId, from, to);
        return rows.stream()
                .map(r -> BossItemAvgPriceResponse.builder()
                        .bossId(((Number) r.get("bossId")).longValue())
                        .itemId(((Number) r.get("itemId")).longValue())
                        .avgPrice(new BigDecimal(String.valueOf(r.get("avgPrice"))))
                        .build())
                .collect(Collectors.toList());
    }
    

    @Override
    public List<AvgCombatPowerByBossJobResponse> getTrimmedAvgCombatPowerGroupByBossAndJob(LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = weeklyBossRecordRepository.findTrimmedAvgCombatPowerByBossGroupByJob(from, to);
        return rows.stream()
                .map(r -> new AvgCombatPowerByBossJobResponse(
                        ((Number) r.get("bossId")).longValue(),
                        (String) r.get("job"),
                        r.get("avg_power") == null ? null : Double.valueOf(r.get("avg_power").toString())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<BossPartyRatioResponse> summarizePartyRatio(Long bossId, LocalDate from, LocalDate to) {
        if (bossId != null) {
            Map<String, Object> row = weeklyBossRecordRepository.summarizePartyRatio(bossId, from, to);
            Long solo = 0L;
            Long party = 0L;
            if (row != null) {
                if (row.get("soloCount") != null) solo = ((Number) row.get("soloCount")).longValue();
                if (row.get("partyCount") != null) party = ((Number) row.get("partyCount")).longValue();
            }
            return java.util.List.of(BossPartyRatioResponse.builder()
                    .bossId(bossId)
                    .soloCount(solo)
                    .partyCount(party)
                    .build());
        }
        List<Map<String, Object>> rows = weeklyBossRecordRepository.summarizePartyRatioGroupByBoss(null, from, to);
        return rows.stream()
                .map(r -> BossPartyRatioResponse.builder()
                        .bossId(((Number) r.get("bossId")).longValue())
                        .soloCount(((Number) r.get("soloCount")).longValue())
                        .partyCount(((Number) r.get("partyCount")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    
}


