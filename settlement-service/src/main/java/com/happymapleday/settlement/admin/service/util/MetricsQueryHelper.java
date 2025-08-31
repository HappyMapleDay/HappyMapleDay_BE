package com.happymapleday.settlement.admin.service.util;

import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesBossLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesLongResponse;
import com.happymapleday.settlement.service.util.WeekCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class MetricsQueryHelper {

    private final WeekCalculator weekCalculator;

    public LocalDate normalizeTo(LocalDate to) {
        LocalDate base = to != null ? to : LocalDate.now();
        return weekCalculator.getWeekStartDate(base);
    }

    public LocalDate normalizeFrom(LocalDate from, LocalDate normalizedTo, String range) {
        if (from != null) {
            return weekCalculator.getWeekStartDate(from);
        }
        if (range == null || range.isEmpty()) {
            return normalizedTo.minusWeeks(3);
        }
        String r = range.toLowerCase();
        if (r.equals("all")) {
            return null;
        }
        if (r.endsWith("w")) {
            int weeks = parseNumber(r.substring(0, r.length() - 1), 4);
            int back = Math.max(1, weeks);
            return normalizedTo.minusWeeks(back - 1);
        }
        if (r.endsWith("m")) {
            int months = parseNumber(r.substring(0, r.length() - 1), 3);
            LocalDate candidate = normalizedTo.minusMonths(Math.max(1, months));
            return weekCalculator.getWeekStartDate(candidate);
        }
        return normalizedTo.minusWeeks(3);
    }

    public List<TimeSeriesLongResponse> aggregateToMonth(List<TimeSeriesLongResponse> weekly) {
        LinkedHashMap<YearMonth, Long> ymToSum = new LinkedHashMap<>();
        for (TimeSeriesLongResponse w : weekly) {
            YearMonth ym = YearMonth.from(w.getDate());
            ymToSum.put(ym, ymToSum.getOrDefault(ym, 0L) + (w.getValue() != null ? w.getValue() : 0L));
        }
        List<TimeSeriesLongResponse> result = new ArrayList<>();
        for (java.util.Map.Entry<YearMonth, Long> e : ymToSum.entrySet()) {
            result.add(TimeSeriesLongResponse.builder()
                    .date(e.getKey().atDay(1))
                    .value(e.getValue())
                    .build());
        }
        return result;
    }

    // 월간 보스 식별: 검은 마법사(하드=31, 익스트림=32)
    private static final Set<Long> MONTHLY_BOSS_IDS = new HashSet<>(Arrays.asList(31L, 32L));

    public boolean isMonthlyBoss(Long bossId) {
        return bossId != null && MONTHLY_BOSS_IDS.contains(bossId);
    }

    // 주별 보스 집계를 월별로 합산 (bossId, YearMonth 단위 그룹)
    public List<TimeSeriesBossLongResponse> aggregateBossWeeklyToMonth(List<TimeSeriesBossLongResponse> weekly) {
        if (weekly == null || weekly.isEmpty()) {
            return Collections.emptyList();
        }

        LinkedHashMap<Long, LinkedHashMap<YearMonth, Long>> bossMonthSums = new LinkedHashMap<>();
        for (TimeSeriesBossLongResponse row : weekly) {
            Long bId = row.getBossId();
            YearMonth ym = YearMonth.from(row.getDate());
            bossMonthSums
                .computeIfAbsent(bId, k -> new LinkedHashMap<>())
                .merge(ym, row.getValue() != null ? row.getValue() : 0L, Long::sum);
        }

        List<TimeSeriesBossLongResponse> result = new ArrayList<>();
        for (Map.Entry<Long, LinkedHashMap<YearMonth, Long>> e : bossMonthSums.entrySet()) {
            Long bId = e.getKey();
            for (Map.Entry<YearMonth, Long> me : e.getValue().entrySet()) {
                result.add(TimeSeriesBossLongResponse.builder()
                        .bossId(bId)
                        .date(me.getKey().atDay(1))
                        .value(me.getValue())
                        .build());
            }
        }
        return result;
    }

    public boolean isGroupByBoss(String groupBy) {
        return groupBy != null && groupBy.equalsIgnoreCase("boss");
    }

    public boolean isBucketMonth(String bucket) {
        return bucket != null && bucket.equalsIgnoreCase("month");
    }

    private int parseNumber(String s, int defaultVal) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return defaultVal;
        }
    }
}


