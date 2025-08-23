package com.happymapleday.settlement.admin.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossKillCountSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.ItemDropSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BoxContentsSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.ItemAveragePriceResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossHardnessSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.PartyRatioSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.AvgCombatPowerByBossJobResponse;
import com.happymapleday.settlement.admin.service.SettlementMetricsService;
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

    // 주차별 보스 처치 횟수
    @GetMapping("/boss/kills/time-series")
    public ResponseEntity<ApiResponse<List<TimeSeriesLongResponse>>> getBossKillCountsByWeek(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<TimeSeriesLongResponse> result = settlementMetricsService.getBossKillCountsByWeek(bossId, from, to);
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

    // 보스 하드니스 요약
    @GetMapping("/boss/hardness/summary")
    public ResponseEntity<ApiResponse<BossHardnessSummaryResponse>> summarizeBossHardness(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        BossHardnessSummaryResponse result = settlementMetricsService.summarizeBossHardness(bossId, from, to);
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


