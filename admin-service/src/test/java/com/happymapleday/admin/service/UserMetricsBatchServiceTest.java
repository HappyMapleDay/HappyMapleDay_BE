package com.happymapleday.admin.service;

import com.happymapleday.admin.client.UserServiceClient;
import com.happymapleday.admin.dto.external.UserServiceUserMetricsDto;
import com.happymapleday.admin.entity.UserMetrics;
import com.happymapleday.admin.enums.ExecutionType;
import com.happymapleday.admin.repository.UserMetricsRepository;
import com.happymapleday.common.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserMetricsBatchService 테스트")
class UserMetricsBatchServiceTest {

    @InjectMocks
    private UserMetricsBatchService userMetricsBatchService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private UserMetricsRepository userMetricsRepository;

    @Mock
    private BatchExecutor batchExecutor;

    private LocalDate from;
    private LocalDate to;

    @BeforeEach
    void setUp() {
        from = LocalDate.of(2024, 1, 1);
        to = LocalDate.of(2024, 1, 3);
    }

    @Test
    @DisplayName("유저 통계 배치 실행 - API 응답 null")
    void executeBatch_NullResponse() {
        // given
        given(userServiceClient.getNormalUsersMetrics("1d", from, to))
            .willReturn(null);

        // BatchExecutor.execute 메서드가 실제로 태스크를 실행하고 예외를 처리하도록 모킹
        given(batchExecutor.execute(any(), any(), any(), any(), any()))
            .willAnswer(invocation -> {
                BatchExecutor.BatchTask task = invocation.getArgument(4);
                try {
                    task.run();
                } catch (Exception e) {
                    // 예외 발생 시 FAILED 결과 반환
                    return new BatchExecutor.BatchExecutionResult(
                        invocation.getArgument(0),
                        com.happymapleday.admin.enums.BatchStatus.FAILED,
                        java.time.LocalDateTime.now(),
                        0,
                        "배치 실행 실패: " + e.getMessage(),
                        100L
                    );
                }
                return new BatchExecutor.BatchExecutionResult(
                    invocation.getArgument(0),
                    com.happymapleday.admin.enums.BatchStatus.SUCCESS,
                    java.time.LocalDateTime.now(),
                    0,
                    "완료",
                    100L
                );
            });

        // when
        BatchExecutor.BatchExecutionResult result = userMetricsBatchService.executeBatch(from, to, ExecutionType.MANUAL);

        // then
        assertThat(result.status()).isEqualTo(com.happymapleday.admin.enums.BatchStatus.FAILED);
        assertThat(result.message()).contains("User Service 응답 데이터가 없습니다");
        verify(userMetricsRepository, never()).save(any(UserMetrics.class));
    }

    @Test
    @DisplayName("유저 통계 배치 실행 - API 데이터 null")
    void executeBatch_NullData() {
        // given
        ApiResponse<UserServiceUserMetricsDto> response = new ApiResponse<>();
        response.setData(null);

        given(userServiceClient.getNormalUsersMetrics("1d", from, to))
            .willReturn(response);

        given(batchExecutor.execute(any(), any(), any(), any(), any()))
            .willAnswer(invocation -> {
                BatchExecutor.BatchTask task = invocation.getArgument(4);
                try {
                    task.run();
                } catch (Exception e) {
                    return new BatchExecutor.BatchExecutionResult(
                        invocation.getArgument(0),
                        com.happymapleday.admin.enums.BatchStatus.FAILED,
                        java.time.LocalDateTime.now(),
                        0,
                        "배치 실행 실패: " + e.getMessage(),
                        100L
                    );
                }
                return new BatchExecutor.BatchExecutionResult(
                    invocation.getArgument(0),
                    com.happymapleday.admin.enums.BatchStatus.SUCCESS,
                    java.time.LocalDateTime.now(),
                    0,
                    "완료",
                    100L
                );
            });

        // when
        BatchExecutor.BatchExecutionResult result = userMetricsBatchService.executeBatch(from, to, ExecutionType.MANUAL);

        // then
        assertThat(result.status()).isEqualTo(com.happymapleday.admin.enums.BatchStatus.FAILED);
        verify(userMetricsRepository, never()).save(any(UserMetrics.class));
    }
}

