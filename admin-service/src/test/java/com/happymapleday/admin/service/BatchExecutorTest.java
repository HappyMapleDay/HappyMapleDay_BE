package com.happymapleday.admin.service;

import com.happymapleday.admin.entity.BatchExecutionHistory;
import com.happymapleday.admin.entity.BatchJobStatus;
import com.happymapleday.admin.enums.BatchStatus;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.enums.ExecutionType;
import com.happymapleday.admin.repository.BatchExecutionHistoryRepository;
import com.happymapleday.admin.repository.BatchJobStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BatchExecutor 테스트")
class BatchExecutorTest {

    @InjectMocks
    private BatchExecutor batchExecutor;

    @Mock
    private BatchJobStatusRepository batchJobStatusRepository;

    @Mock
    private BatchExecutionHistoryRepository batchExecutionHistoryRepository;

    private BatchJobStatus mockJobStatus;
    private BatchExecutionHistory mockHistory;

    @BeforeEach
    void setUp() {
        mockJobStatus = BatchJobStatus.builder()
            .id(1L)
            .batchType(BatchType.USER_METRICS)
            .status(BatchStatus.PENDING)
            .build();

        mockHistory = BatchExecutionHistory.builder()
            .id(1L)
            .batchType(BatchType.USER_METRICS)
            .status(BatchStatus.RUNNING)
            .executionType(ExecutionType.SCHEDULED)
            .build();
    }

    @Test
    @DisplayName("배치 실행 성공")
    void execute_Success() {
        // given
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 7);
        int expectedRecordCount = 100;

        given(batchJobStatusRepository.findByBatchType(BatchType.USER_METRICS))
            .willReturn(Optional.of(mockJobStatus));
        given(batchJobStatusRepository.save(any(BatchJobStatus.class)))
            .willReturn(mockJobStatus);
        given(batchExecutionHistoryRepository.save(any(BatchExecutionHistory.class)))
            .willReturn(mockHistory);

        BatchExecutor.BatchTask task = () -> expectedRecordCount;

        // when
        BatchExecutor.BatchExecutionResult result = batchExecutor.execute(
            BatchType.USER_METRICS,
            from,
            to,
            ExecutionType.SCHEDULED,
            task
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.batchType()).isEqualTo(BatchType.USER_METRICS);
        assertThat(result.status()).isEqualTo(BatchStatus.SUCCESS);
        assertThat(result.recordCount()).isEqualTo(expectedRecordCount);
        assertThat(result.durationMs()).isGreaterThanOrEqualTo(0);

        // 배치 상태가 2번 저장되는지 확인 (RUNNING → SUCCESS)
        verify(batchJobStatusRepository, times(2)).save(any(BatchJobStatus.class));
        verify(batchExecutionHistoryRepository, times(2)).save(any(BatchExecutionHistory.class));
    }

    @Test
    @DisplayName("배치 실행 실패 - 예외 발생")
    void execute_Failure_Exception() {
        // given
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 7);
        String errorMessage = "Test exception";

        given(batchJobStatusRepository.findByBatchType(BatchType.USER_METRICS))
            .willReturn(Optional.of(mockJobStatus));
        given(batchJobStatusRepository.save(any(BatchJobStatus.class)))
            .willReturn(mockJobStatus);
        given(batchExecutionHistoryRepository.save(any(BatchExecutionHistory.class)))
            .willReturn(mockHistory);

        BatchExecutor.BatchTask task = () -> {
            throw new RuntimeException(errorMessage);
        };

        // when
        BatchExecutor.BatchExecutionResult result = batchExecutor.execute(
            BatchType.USER_METRICS,
            from,
            to,
            ExecutionType.SCHEDULED,
            task
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.batchType()).isEqualTo(BatchType.USER_METRICS);
        assertThat(result.status()).isEqualTo(BatchStatus.FAILED);
        assertThat(result.recordCount()).isEqualTo(0);
        assertThat(result.message()).contains("배치 실행 실패");
        assertThat(result.message()).contains(errorMessage);

        // 배치 상태가 2번 저장되는지 확인 (RUNNING → FAILED)
        verify(batchJobStatusRepository, times(2)).save(any(BatchJobStatus.class));
        verify(batchExecutionHistoryRepository, times(2)).save(any(BatchExecutionHistory.class));
    }

    @Test
    @DisplayName("배치 실행 - 새로운 BatchJobStatus 생성")
    void execute_CreateNewJobStatus() {
        // given
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 7);

        given(batchJobStatusRepository.findByBatchType(BatchType.USER_METRICS))
            .willReturn(Optional.empty());
        given(batchJobStatusRepository.save(any(BatchJobStatus.class)))
            .willReturn(mockJobStatus);
        given(batchExecutionHistoryRepository.save(any(BatchExecutionHistory.class)))
            .willReturn(mockHistory);

        BatchExecutor.BatchTask task = () -> 10;

        // when
        BatchExecutor.BatchExecutionResult result = batchExecutor.execute(
            BatchType.USER_METRICS,
            from,
            to,
            ExecutionType.MANUAL,
            task
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(BatchStatus.SUCCESS);

        ArgumentCaptor<BatchJobStatus> statusCaptor = ArgumentCaptor.forClass(BatchJobStatus.class);
        verify(batchJobStatusRepository, times(2)).save(statusCaptor.capture());

        BatchJobStatus savedStatus = statusCaptor.getAllValues().get(0);
        assertThat(savedStatus.getBatchType()).isEqualTo(BatchType.USER_METRICS);
    }

    @Test
    @DisplayName("배치 실행 - ExecutionType에 따른 executedBy 설정")
    void execute_ExecutedByBasedOnExecutionType() {
        // given
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 7);

        given(batchJobStatusRepository.findByBatchType(BatchType.USER_METRICS))
            .willReturn(Optional.of(mockJobStatus));
        given(batchJobStatusRepository.save(any(BatchJobStatus.class)))
            .willReturn(mockJobStatus);
        given(batchExecutionHistoryRepository.save(any(BatchExecutionHistory.class)))
            .willReturn(mockHistory);

        BatchExecutor.BatchTask task = () -> 5;

        // when - MANUAL 실행
        batchExecutor.execute(
            BatchType.USER_METRICS,
            from,
            to,
            ExecutionType.MANUAL,
            task
        );

        // then
        ArgumentCaptor<BatchExecutionHistory> historyCaptor = ArgumentCaptor.forClass(BatchExecutionHistory.class);
        verify(batchExecutionHistoryRepository, atLeastOnce()).save(historyCaptor.capture());

        BatchExecutionHistory savedHistory = historyCaptor.getAllValues().get(0);
        assertThat(savedHistory.getExecutionType()).isEqualTo(ExecutionType.MANUAL);
        assertThat(savedHistory.getExecutedBy()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("배치 실행 - 실행 시간 측정")
    void execute_MeasureDuration() throws Exception {
        // given
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 7);
        long sleepTime = 100;

        given(batchJobStatusRepository.findByBatchType(BatchType.USER_METRICS))
            .willReturn(Optional.of(mockJobStatus));
        given(batchJobStatusRepository.save(any(BatchJobStatus.class)))
            .willReturn(mockJobStatus);
        given(batchExecutionHistoryRepository.save(any(BatchExecutionHistory.class)))
            .willReturn(mockHistory);

        BatchExecutor.BatchTask task = () -> {
            Thread.sleep(sleepTime);
            return 1;
        };

        // when
        BatchExecutor.BatchExecutionResult result = batchExecutor.execute(
            BatchType.USER_METRICS,
            from,
            to,
            ExecutionType.SCHEDULED,
            task
        );

        // then
        assertThat(result.durationMs()).isGreaterThanOrEqualTo(sleepTime);
    }
}

