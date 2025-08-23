package com.happymapleday.settlement.admin.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesBigIntegerResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesDecimalResponse;
import com.happymapleday.settlement.admin.service.SettlementMetricsService;
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
public class AdminMetricsController {

    private final SettlementMetricsService settlementMetricsService;

    public AdminMetricsController(SettlementMetricsService settlementMetricsService) {
        this.settlementMetricsService = settlementMetricsService;
    }

    // 주차별 평균 총수익
    @GetMapping("/average-total-income")
    public ResponseEntity<ApiResponse<List<TimeSeriesDecimalResponse>>> getAverageTotalIncomeByWeek(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<TimeSeriesDecimalResponse> result = settlementMetricsService.getAverageTotalIncomeByWeek(from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 주차별 평균 결정석 수익
    @GetMapping("/average-crystal-income")
    public ResponseEntity<ApiResponse<List<TimeSeriesDecimalResponse>>> getAverageCrystalIncomeByWeek(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<TimeSeriesDecimalResponse> result = settlementMetricsService.getAverageCrystalIncomeByWeek(from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 주차별 평균 물욕템 수익
    @GetMapping("/average-desire-income")
    public ResponseEntity<ApiResponse<List<TimeSeriesDecimalResponse>>> getAverageDesireItemIncomeByWeek(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<TimeSeriesDecimalResponse> result = settlementMetricsService.getAverageDesireItemIncomeByWeek(from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 주차별 보스 처치 횟수
    @GetMapping("/boss-kills")
    public ResponseEntity<ApiResponse<List<TimeSeriesLongResponse>>> getBossKillCountsByWeek(
            @RequestParam(required = false) Long bossId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<TimeSeriesLongResponse> result = settlementMetricsService.getBossKillCountsByWeek(bossId, from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 주차별 물욕템 드랍 횟수
    @GetMapping("/item-drops")
    public ResponseEntity<ApiResponse<List<TimeSeriesLongResponse>>> getItemDropCountByWeek(
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<TimeSeriesLongResponse> result = settlementMetricsService.getItemDropCountByWeek(itemId, from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 주차별 물욕템 평균 판매가격
    @GetMapping("/item-average-price")
    public ResponseEntity<ApiResponse<List<TimeSeriesBigIntegerResponse>>> getItemAveragePriceByWeek(
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<TimeSeriesBigIntegerResponse> result = settlementMetricsService.getItemAveragePriceByWeek(itemId, from, to);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}


