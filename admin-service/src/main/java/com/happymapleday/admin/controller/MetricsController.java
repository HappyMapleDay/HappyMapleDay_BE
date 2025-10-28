package com.happymapleday.admin.controller;

import com.happymapleday.admin.dto.response.*;
import com.happymapleday.admin.service.MetricsQueryService;
import com.happymapleday.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/metrics")
@RequiredArgsConstructor
@Slf4j
public class MetricsController {

    private final MetricsQueryService metricsQueryService;

    // 2.1 가입 유저 수 현황 조회
    @GetMapping("/users")
    public ApiResponse<UserMetricsResponse> getUserMetrics(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(required = false, defaultValue = "daily") String period
    ) {
        UserMetricsResponse response = metricsQueryService.getUserMetrics(from, to, period);
        return ApiResponse.success(response);
    }

    // 2.2 보스별 총 격파 횟수 조회
    @GetMapping("/bosses/kills")
    public ApiResponse<List<BossKillMetricsResponse>> getBossKillMetrics(
        @RequestParam(required = false) Long bossId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<BossKillMetricsResponse> response = metricsQueryService.getBossKillMetrics(bossId, from, to);
        return ApiResponse.success(response);
    }

    // 2.3 보스별 캐릭터 격파 전투력 평균 조회
    @GetMapping("/bosses/combat-power")
    public ApiResponse<List<BossCombatPowerMetricsResponse>> getBossCombatPowerMetrics(
        @RequestParam(required = false) Long bossId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<BossCombatPowerMetricsResponse> response = metricsQueryService.getBossCombatPowerMetrics(bossId, from, to);
        return ApiResponse.success(response);
    }

    // 2.4 보스별 물욕템 드롭 현황 조회
    @GetMapping("/bosses/items/drops")
    public ApiResponse<List<ItemDropMetricsResponse>> getItemDropMetrics(
        @RequestParam(required = false) Long bossId,
        @RequestParam(required = false) Long itemId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<ItemDropMetricsResponse> response = metricsQueryService.getItemDropMetrics(bossId, itemId, from, to);
        return ApiResponse.success(response);
    }

    // 2.5 보스별 물욕템 판매 가격 현황 조회
    @GetMapping("/bosses/items/prices")
    public ApiResponse<List<ItemPriceMetricsResponse>> getItemPriceMetrics(
        @RequestParam(required = false) Long bossId,
        @RequestParam(required = false) Long itemId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<ItemPriceMetricsResponse> response = metricsQueryService.getItemPriceMetrics(bossId, itemId, from, to);
        return ApiResponse.success(response);
    }
}

