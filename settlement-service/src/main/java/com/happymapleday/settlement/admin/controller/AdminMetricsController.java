package com.happymapleday.settlement.admin.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossKillCountSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.ItemDropSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BoxContentsSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.ItemAveragePriceResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.PartyRatioSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.AvgCombatPowerByBossJobResponse;
import com.happymapleday.settlement.admin.service.SettlementMetricsService;
import com.happymapleday.settlement.service.util.WeekCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/settlement/admin/metrics")
@RequiredArgsConstructor
public class AdminMetricsController {

    private final SettlementMetricsService settlementMetricsService;
    private final WeekCalculator weekCalculator;

    // 주차별 보스 처치 횟수
    @GetMapping("/boss/kills/time-series")
    public ResponseEntity<ApiResponse<List<TimeSeriesLongResponse>>> getBossKillCountsByWeek(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String range,
            @RequestParam(required = false, defaultValue = "week") String bucket) {
        LocalDate normalizedTo = to != null ? weekCalculator.getWeekStartDate(to) : weekCalculator.getWeekStartDate(LocalDate.now());
        LocalDate normalizedFrom = computeFromByRangeOrNormalize(from, normalizedTo, range);

        List<TimeSeriesLongResponse> weekly = settlementMetricsService.getBossKillCountsByWeek(bossId, normalizedFrom, normalizedTo);
        if (bucket == null || bucket.equalsIgnoreCase("week")) {
            return ResponseEntity.ok(ApiResponse.success(weekly));
        }
        if (bucket.equalsIgnoreCase("month")) {
            List<TimeSeriesLongResponse> monthly = aggregateByMonth(weekly);
            return ResponseEntity.ok(ApiResponse.success(monthly));
        }
        return ResponseEntity.ok(ApiResponse.success(weekly));
    }

    private LocalDate computeFromByRangeOrNormalize(LocalDate from, LocalDate normalizedTo, String range) {
        if (from != null) {
            return weekCalculator.getWeekStartDate(from);
        }
        if (range == null || range.isEmpty()) {
            // 기본 4주
            return normalizedTo.minusWeeks(3);
        }
        String r = range.toLowerCase();
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

    private int parseNumber(String s, int defaultVal) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private List<TimeSeriesLongResponse> aggregateByMonth(List<TimeSeriesLongResponse> weekly) {
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

    // 보스별 직업별 트림 평균 투력(전체 보스)
    @GetMapping("/boss/hardness/avg-combat-power")
    public ResponseEntity<ApiResponse<List<AvgCombatPowerByBossJobResponse>>> getTrimmedAvgCombatPowerGroupByBossAndJob(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<AvgCombatPowerByBossJobResponse> result = settlementMetricsService.getTrimmedAvgCombatPowerGroupByBossAndJob(from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 보스별 총 처치 수 요약
    @GetMapping("/boss/kills/summary")
    public ResponseEntity<ApiResponse<List<BossKillCountSummaryResponse>>> summarizeBossKillCounts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<BossKillCountSummaryResponse> result = settlementMetricsService.summarizeBossKillCounts(from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 보스별 아이템별 총 드랍 수 요약
    @GetMapping("/item/drops/summary")
    public ResponseEntity<ApiResponse<List<ItemDropSummaryResponse>>> summarizeItemDropsByBoss(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<ItemDropSummaryResponse> result = settlementMetricsService.summarizeItemDropsByBoss(bossId, from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 보스별 박스 내용물 요약
    @GetMapping("/box/contents/summary")
    public ResponseEntity<ApiResponse<List<BoxContentsSummaryResponse>>> summarizeBoxContentsByBoss(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) Long boxItemId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<BoxContentsSummaryResponse> result = settlementMetricsService.summarizeBoxContentsByBoss(bossId, boxItemId, from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 아이템별 평균 판매가 요약
    @GetMapping("/item/average-price/summary")
    public ResponseEntity<ApiResponse<List<ItemAveragePriceResponse>>> summarizeItemAveragePrice(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<ItemAveragePriceResponse> result = settlementMetricsService.summarizeItemAveragePrice(bossId, itemId, from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 보스 솔로/파티 비율 요약
    @GetMapping("/boss/party-ratio/summary")
    public ResponseEntity<ApiResponse<PartyRatioSummaryResponse>> summarizePartyRatio(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        PartyRatioSummaryResponse result = settlementMetricsService.summarizePartyRatio(bossId, from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}


