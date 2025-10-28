package com.happymapleday.admin.service;

import com.happymapleday.admin.entity.BatchExecutionHistory;
import com.happymapleday.admin.entity.BatchJobStatus;
import com.happymapleday.admin.enums.BatchStatus;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.enums.ExecutionType;
import com.happymapleday.admin.repository.BatchExecutionHistoryRepository;
import com.happymapleday.admin.repository.BatchJobStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchExecutor {

    private final BatchJobStatusRepository batchJobStatusRepository;
    private final BatchExecutionHistoryRepository batchExecutionHistoryRepository;

    @Transactional
    public BatchExecutionResult execute(
        BatchType batchType, 
        LocalDate from, 
        LocalDate to, 
        ExecutionType executionType,
        BatchTask task
    ) {
        long startTime = System.currentTimeMillis();
        LocalDateTime executedAt = LocalDateTime.now();
        
        BatchJobStatus jobStatus = getOrCreateJobStatus(batchType);
        BatchExecutionHistory history = createHistory(batchType, executedAt, executionType, from, to);

        jobStatus.updateStatus(BatchStatus.RUNNING, 0, "배치 실행 중...", 0L);
        history.complete(BatchStatus.RUNNING, 0, "배치 실행 중...", 0L);
        
        batchJobStatusRepository.save(jobStatus);
        batchExecutionHistoryRepository.save(history);

        try {
            int recordCount = task.run();
            long duration = System.currentTimeMillis() - startTime;
            String message = batchType.getDescription() + " 완료";

            jobStatus.updateStatus(BatchStatus.SUCCESS, recordCount, message, duration);
            history.complete(BatchStatus.SUCCESS, recordCount, message, duration);
            
            batchJobStatusRepository.save(jobStatus);
            batchExecutionHistoryRepository.save(history);

            log.info("[{}] 배치 성공 - {} records, {}ms", batchType, recordCount, duration);
            
            return new BatchExecutionResult(batchType, BatchStatus.SUCCESS, executedAt, recordCount, message, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            String errorMessage = "배치 실행 실패: " + e.getMessage();

            jobStatus.updateStatus(BatchStatus.FAILED, 0, errorMessage, duration);
            history.complete(BatchStatus.FAILED, 0, errorMessage, duration);
            
            batchJobStatusRepository.save(jobStatus);
            batchExecutionHistoryRepository.save(history);

            log.error("[{}] 배치 실패 - {}ms: {}", batchType, duration, e.getMessage(), e);
            
            return new BatchExecutionResult(batchType, BatchStatus.FAILED, executedAt, 0, errorMessage, duration);
        }
    }

    private BatchJobStatus getOrCreateJobStatus(BatchType batchType) {
        return batchJobStatusRepository.findByBatchType(batchType)
            .orElseGet(() -> BatchJobStatus.builder()
                .batchType(batchType)
                .status(BatchStatus.PENDING)
                .build());
    }

    private BatchExecutionHistory createHistory(
        BatchType batchType, 
        LocalDateTime executedAt, 
        ExecutionType executionType,
        LocalDate from,
        LocalDate to
    ) {
        return BatchExecutionHistory.builder()
            .batchType(batchType)
            .executedAt(executedAt)
            .status(BatchStatus.RUNNING)
            .executionType(executionType)
            .targetDateFrom(from)
            .targetDateTo(to)
            .executedBy(executionType == ExecutionType.MANUAL ? "ADMIN" : "SYSTEM")
            .build();
    }

    @FunctionalInterface
    public interface BatchTask {
        int run() throws Exception;
    }

    public record BatchExecutionResult(
        BatchType batchType,
        BatchStatus status,
        LocalDateTime executedAt,
        Integer recordCount,
        String message,
        Long durationMs
    ) {}
}

