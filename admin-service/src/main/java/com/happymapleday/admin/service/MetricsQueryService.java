package com.happymapleday.admin.service;

import com.happymapleday.admin.dto.response.*;
import com.happymapleday.admin.entity.*;
import com.happymapleday.admin.enums.BatchStatus;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.enums.ExecutionType;
import com.happymapleday.admin.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricsQueryService {

    private final UserMetricsRepository userMetricsRepository;
    private final BossKillMetricsRepository bossKillMetricsRepository;
    private final BossCombatPowerMetricsRepository bossCombatPowerMetricsRepository;
    private final ItemDropMetricsRepository itemDropMetricsRepository;
    private final ItemPriceMetricsRepository itemPriceMetricsRepository;
    private final BatchJobStatusRepository batchJobStatusRepository;
    private final BatchExecutionHistoryRepository batchExecutionHistoryRepository;

    public UserMetricsResponse getUserMetrics(LocalDate from, LocalDate to, String period) {
        List<UserMetrics> metrics = (from != null && to != null) 
            ? userMetricsRepository.findByMetricDateBetweenOrderByMetricDateAsc(from, to)
            : userMetricsRepository.findAllByOrderByMetricDateDesc();

        List<UserMetricsResponse.UserCountData> userCounts = metrics.stream()
            .map(m -> UserMetricsResponse.UserCountData.builder()
                .date(m.getMetricDate())
                .cumulativeCount(m.getCumulativeCount())
                .dailyCount(m.getDailyCount())
                .build())
            .collect(Collectors.toList());

        Long totalActiveUsers = metrics.isEmpty() ? 0L : metrics.get(0).getCumulativeCount();

        return UserMetricsResponse.builder()
            .userCounts(userCounts)
            .totalActiveUsers(totalActiveUsers)
            .period(period != null ? period : "daily")
            .build();
    }

    public List<BossKillMetricsResponse> getBossKillMetrics(Long bossId, LocalDate from, LocalDate to) {
        List<BossKillMetrics> metrics;
        
        if (bossId != null && from != null && to != null) {
            metrics = bossKillMetricsRepository.findByBossIdAndMetricDateBetweenOrderByMetricDateAsc(bossId, from, to);
        } else if (from != null && to != null) {
            metrics = bossKillMetricsRepository.findByMetricDateBetweenOrderByMetricDateAsc(from, to);
        } else {
            metrics = bossKillMetricsRepository.findAllByOrderByMetricDateDesc();
        }

        return metrics.stream()
            .map(m -> BossKillMetricsResponse.builder()
                .metricDate(m.getMetricDate())
                .bossId(m.getBossId())
                .bossName(m.getBossName())
                .bossNameEn(m.getBossNameEn())
                .difficulty(m.getDifficulty())
                .totalKills(m.getTotalKills())
                .build())
            .collect(Collectors.toList());
    }

    public List<BossCombatPowerMetricsResponse> getBossCombatPowerMetrics(Long bossId, LocalDate from, LocalDate to) {
        List<BossCombatPowerMetrics> metrics;
        
        if (bossId != null && from != null && to != null) {
            metrics = bossCombatPowerMetricsRepository.findByBossIdAndMetricDateBetweenOrderByMetricDateAsc(bossId, from, to);
        } else if (from != null && to != null) {
            metrics = bossCombatPowerMetricsRepository.findByMetricDateBetweenOrderByMetricDateAsc(from, to);
        } else {
            metrics = bossCombatPowerMetricsRepository.findAllByOrderByMetricDateDesc();
        }

        return metrics.stream()
            .map(m -> BossCombatPowerMetricsResponse.builder()
                .metricDate(m.getMetricDate())
                .bossId(m.getBossId())
                .bossName(m.getBossName())
                .bossNameEn(m.getBossNameEn())
                .difficulty(m.getDifficulty())
                .characterClass(m.getCharacterClass())
                .avgCombatPower(m.getAvgCombatPower())
                .build())
            .collect(Collectors.toList());
    }

    public List<ItemDropMetricsResponse> getItemDropMetrics(Long bossId, Long itemId, LocalDate from, LocalDate to) {
        List<ItemDropMetrics> metrics;
        
        if (bossId != null && itemId != null && from != null && to != null) {
            metrics = itemDropMetricsRepository.findByBossIdAndItemIdAndMetricDateBetweenOrderByMetricDateAsc(bossId, itemId, from, to);
        } else if (bossId != null && from != null && to != null) {
            metrics = itemDropMetricsRepository.findByBossIdAndMetricDateBetweenOrderByMetricDateAsc(bossId, from, to);
        } else if (itemId != null && from != null && to != null) {
            metrics = itemDropMetricsRepository.findByItemIdAndMetricDateBetweenOrderByMetricDateAsc(itemId, from, to);
        } else if (from != null && to != null) {
            metrics = itemDropMetricsRepository.findByMetricDateBetweenOrderByMetricDateAsc(from, to);
        } else {
            metrics = itemDropMetricsRepository.findAllByOrderByMetricDateDesc();
        }

        return metrics.stream()
            .map(m -> ItemDropMetricsResponse.builder()
                .metricDate(m.getMetricDate())
                .bossId(m.getBossId())
                .bossName(m.getBossName())
                .bossNameEn(m.getBossNameEn())
                .difficulty(m.getDifficulty())
                .itemId(m.getItemId())
                .itemName(m.getItemName())
                .itemNameEn(m.getItemNameEn())
                .dropCount(m.getDropCount())
                .build())
            .collect(Collectors.toList());
    }

    public List<ItemPriceMetricsResponse> getItemPriceMetrics(Long bossId, Long itemId, LocalDate from, LocalDate to) {
        List<ItemPriceMetrics> metrics;
        
        if (bossId != null && itemId != null && from != null && to != null) {
            metrics = itemPriceMetricsRepository.findByBossIdAndItemIdAndMetricDateBetweenOrderByMetricDateAsc(bossId, itemId, from, to);
        } else if (bossId != null && from != null && to != null) {
            metrics = itemPriceMetricsRepository.findByBossIdAndMetricDateBetweenOrderByMetricDateAsc(bossId, from, to);
        } else if (itemId != null && from != null && to != null) {
            metrics = itemPriceMetricsRepository.findByItemIdAndMetricDateBetweenOrderByMetricDateAsc(itemId, from, to);
        } else if (from != null && to != null) {
            metrics = itemPriceMetricsRepository.findByMetricDateBetweenOrderByMetricDateAsc(from, to);
        } else {
            metrics = itemPriceMetricsRepository.findAllByOrderByMetricDateDesc();
        }

        return metrics.stream()
            .map(m -> ItemPriceMetricsResponse.builder()
                .metricDate(m.getMetricDate())
                .bossId(m.getBossId())
                .bossName(m.getBossName())
                .bossNameEn(m.getBossNameEn())
                .difficulty(m.getDifficulty())
                .itemId(m.getItemId())
                .itemName(m.getItemName())
                .itemNameEn(m.getItemNameEn())
                .avgPrice(m.getAvgPrice())
                .build())
            .collect(Collectors.toList());
    }

    public List<BatchStatusResponse> getBatchStatus() {
        return batchJobStatusRepository.findAll().stream()
            .map(status -> BatchStatusResponse.builder()
                .batchType(status.getBatchType())
                .lastExecutedAt(status.getLastExecutedAt())
                .status(status.getStatus())
                .nextScheduledAt(status.getNextScheduledAt())
                .recordCount(status.getRecordCount())
                .message(status.getMessage())
                .durationMs(status.getDurationMs())
                .build())
            .collect(Collectors.toList());
    }

    public Page<BatchHistoryResponse> getBatchHistory(
        BatchType batchType,
        ExecutionType executionType,
        BatchStatus status,
        LocalDate from,
        LocalDate to,
        Pageable pageable
    ) {
        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = to != null ? to.atTime(23, 59, 59) : null;

        Page<BatchExecutionHistory> histories = batchExecutionHistoryRepository.findByFilters(
            batchType, executionType, status, fromDateTime, toDateTime, pageable
        );

        return histories.map(history -> BatchHistoryResponse.builder()
            .id(history.getId())
            .batchType(history.getBatchType())
            .executedAt(history.getExecutedAt())
            .status(history.getStatus())
            .executionType(history.getExecutionType())
            .targetDateFrom(history.getTargetDateFrom())
            .targetDateTo(history.getTargetDateTo())
            .recordCount(history.getRecordCount())
            .message(history.getMessage())
            .durationMs(history.getDurationMs())
            .executedBy(history.getExecutedBy())
            .build());
    }
}

