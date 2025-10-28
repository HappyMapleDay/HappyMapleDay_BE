package com.happymapleday.admin.controller;

import com.happymapleday.admin.dto.request.BatchExecuteAllRequest;
import com.happymapleday.admin.dto.request.BatchExecuteRequest;
import com.happymapleday.admin.dto.response.*;
import com.happymapleday.admin.enums.BatchStatus;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.enums.ExecutionType;
import com.happymapleday.admin.service.*;
import com.happymapleday.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/batch")
@RequiredArgsConstructor
@Slf4j
public class BatchController {

    private final UserMetricsBatchService userMetricsBatchService;
    private final BossKillMetricsBatchService bossKillMetricsBatchService;
    private final BossCombatPowerMetricsBatchService bossCombatPowerMetricsBatchService;
    private final ItemDropMetricsBatchService itemDropMetricsBatchService;
    private final ItemPriceMetricsBatchService itemPriceMetricsBatchService;
    private final BatchCoordinatorService batchCoordinatorService;
    private final MetricsQueryService metricsQueryService;

    // 1.6 전체 배치 일괄 실행
    @PostMapping("/execute-all")
    public ApiResponse<BatchExecuteAllResponse> executeAllBatches(@RequestBody(required = false) BatchExecuteAllRequest request) {
        LocalDate from = request != null && request.getFrom() != null ? request.getFrom() : LocalDate.now().minusDays(7);
        LocalDate to = request != null && request.getTo() != null ? request.getTo() : LocalDate.now().minusDays(1);

        long startTime = System.currentTimeMillis();
        List<BatchExecutor.BatchExecutionResult> results = batchCoordinatorService.executeAllBatches(from, to);
        long totalDuration = System.currentTimeMillis() - startTime;

        List<BatchExecuteResponse> responses = results.stream()
            .map(result -> BatchExecuteResponse.builder()
                .batchType(result.batchType())
                .status(result.status())
                .recordCount(result.recordCount())
                .message(result.message())
                .durationMs(result.durationMs())
                .build())
            .collect(Collectors.toList());

        BatchExecuteAllResponse response = BatchExecuteAllResponse.builder()
            .executedAt(LocalDateTime.now())
            .totalDurationMs(totalDuration)
            .results(responses)
            .build();

        return ApiResponse.success(response);
    }

    // 1.7 배치 상태 조회
    @GetMapping("/status")
    public ApiResponse<List<BatchStatusResponse>> getBatchStatus() {
        List<BatchStatusResponse> status = metricsQueryService.getBatchStatus();
        return ApiResponse.success(status);
    }

    // 1.8 개별 배치 수동 실행
    @PostMapping("/execute")
    public ApiResponse<BatchExecuteResponse> executeBatch(@RequestBody BatchExecuteRequest request) {
        LocalDate from = request.getFrom() != null ? request.getFrom() : LocalDate.now().minusDays(7);
        LocalDate to = request.getTo() != null ? request.getTo() : LocalDate.now().minusDays(1);

        BatchExecutor.BatchExecutionResult result = switch (request.getBatchType()) {
            case USER_METRICS -> userMetricsBatchService.executeBatch(from, to, ExecutionType.MANUAL);
            case BOSS_KILLS -> bossKillMetricsBatchService.executeBatch(from, to, ExecutionType.MANUAL);
            case COMBAT_POWER -> bossCombatPowerMetricsBatchService.executeBatch(from, to, ExecutionType.MANUAL);
            case ITEM_DROPS -> itemDropMetricsBatchService.executeBatch(from, to, ExecutionType.MANUAL);
            case ITEM_SALES -> itemPriceMetricsBatchService.executeBatch(from, to, ExecutionType.MANUAL);
        };

        BatchExecuteResponse response = BatchExecuteResponse.builder()
            .batchType(result.batchType())
            .status(result.status())
            .executedAt(result.executedAt())
            .recordCount(result.recordCount())
            .message(result.message())
            .durationMs(result.durationMs())
            .build();

        return ApiResponse.success(response);
    }

    // 1.9 배치 실행 이력 조회
    @GetMapping("/history")
    public ApiResponse<Page<BatchHistoryResponse>> getBatchHistory(
        @RequestParam(required = false) BatchType batchType,
        @RequestParam(required = false) ExecutionType executionType,
        @RequestParam(required = false) BatchStatus status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BatchHistoryResponse> history = metricsQueryService.getBatchHistory(
            batchType, executionType, status, from, to, pageable
        );
        return ApiResponse.success(history);
    }
}

