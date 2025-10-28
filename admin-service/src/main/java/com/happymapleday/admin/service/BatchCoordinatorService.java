package com.happymapleday.admin.service;

import com.happymapleday.admin.enums.ExecutionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchCoordinatorService {

    private final UserMetricsBatchService userMetricsBatchService;
    private final BossKillMetricsBatchService bossKillMetricsBatchService;
    private final BossCombatPowerMetricsBatchService bossCombatPowerMetricsBatchService;
    private final ItemDropMetricsBatchService itemDropMetricsBatchService;
    private final ItemPriceMetricsBatchService itemPriceMetricsBatchService;

    public List<BatchExecutor.BatchExecutionResult> executeAllBatches(LocalDate from, LocalDate to) {
        log.info("전체 배치 작업 시작: from={}, to={}", from, to);
        
        List<BatchExecutor.BatchExecutionResult> results = new ArrayList<>();
        
        results.add(userMetricsBatchService.executeBatch(from, to, ExecutionType.SCHEDULED));
        results.add(bossKillMetricsBatchService.executeBatch(from, to, ExecutionType.SCHEDULED));
        results.add(bossCombatPowerMetricsBatchService.executeBatch(from, to, ExecutionType.SCHEDULED));
        results.add(itemDropMetricsBatchService.executeBatch(from, to, ExecutionType.SCHEDULED));
        results.add(itemPriceMetricsBatchService.executeBatch(from, to, ExecutionType.SCHEDULED));
        
        log.info("전체 배치 작업 완료: {} 건", results.size());
        return results;
    }
}

