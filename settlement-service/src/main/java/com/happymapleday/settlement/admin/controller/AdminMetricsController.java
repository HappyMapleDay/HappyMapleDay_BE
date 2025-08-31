package com.happymapleday.settlement.admin.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesBossLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossKillCountSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossItemCountResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossItemAvgPriceResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossPartyRatioResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.AvgCombatPowerByBossJobResponse;
import com.happymapleday.settlement.admin.service.SettlementMetricsService;
import com.happymapleday.settlement.admin.service.util.MetricsQueryHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/settlement/admin/metrics")
@RequiredArgsConstructor
public class AdminMetricsController {

    private final SettlementMetricsService settlementMetricsService;
    private final MetricsQueryHelper metricsQueryHelper;

    // 주차별 보스 처치 횟수
    @GetMapping("/boss/kills/time-series")
    public ResponseEntity<ApiResponse<?>> getBossKillCountsByWeek(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String range) {
        LocalDate normalizedTo = metricsQueryHelper.normalizeTo(to);
        LocalDate normalizedFrom = metricsQueryHelper.normalizeFrom(from, normalizedTo, range);

        List<TimeSeriesBossLongResponse> result = settlementMetricsService.getBossKillCountsByWeek(bossId, normalizedFrom, normalizedTo);
        return ResponseEntity.ok(ApiResponse.success(result));
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
    public ResponseEntity<ApiResponse<?>> summarizeItemDropsByBoss(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<BossItemCountResponse> result = settlementMetricsService.summarizeItemDropsByBoss(bossId, from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 보스별 박스 내용물 요약
    @GetMapping("/box/contents/summary")
    public ResponseEntity<ApiResponse<?>> summarizeBoxContentsByBoss(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) Long boxItemId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<BossItemCountResponse> result = settlementMetricsService.summarizeBoxContentsByBoss(bossId, boxItemId, from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 아이템별 평균 판매가 요약
    @GetMapping("/item/average-price/summary")
    public ResponseEntity<ApiResponse<?>> summarizeItemAveragePrice(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<BossItemAvgPriceResponse> result = settlementMetricsService.summarizeItemAveragePrice(bossId, itemId, from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 보스 솔로/파티 비율 요약
    @GetMapping("/boss/party-ratio/summary")
    public ResponseEntity<ApiResponse<?>> summarizePartyRatio(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<BossPartyRatioResponse> result = settlementMetricsService.summarizePartyRatio(bossId, from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}


