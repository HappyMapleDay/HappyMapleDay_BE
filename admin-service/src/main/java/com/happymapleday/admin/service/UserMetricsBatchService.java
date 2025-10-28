package com.happymapleday.admin.service;

import com.happymapleday.admin.client.UserServiceClient;
import com.happymapleday.admin.dto.external.UserServiceUserMetricsDto;
import com.happymapleday.admin.entity.UserMetrics;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.enums.ExecutionType;
import com.happymapleday.admin.repository.UserMetricsRepository;
import com.happymapleday.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMetricsBatchService {

    private final UserServiceClient userServiceClient;
    private final UserMetricsRepository userMetricsRepository;
    private final BatchExecutor batchExecutor;

    @Transactional
    public BatchExecutor.BatchExecutionResult executeBatch(LocalDate from, LocalDate to, ExecutionType executionType) {
        return batchExecutor.execute(BatchType.USER_METRICS, from, to, executionType, () -> {
            ApiResponse<UserServiceUserMetricsDto> response = userServiceClient.getNormalUsersMetrics(
                "1d", from, to
            );

            if (response == null || response.getData() == null) {
                throw new RuntimeException("User Service 응답 데이터가 없습니다");
            }

            UserServiceUserMetricsDto data = response.getData();
            int count = 0;

            for (UserServiceUserMetricsDto.UserCountData userCount : data.getUserCounts()) {
                UserMetrics metrics = userMetricsRepository.findByMetricDate(userCount.getDate())
                    .orElseGet(() -> UserMetrics.builder()
                        .metricDate(userCount.getDate())
                        .build());

                UserMetrics newMetrics = UserMetrics.builder()
                    .id(metrics.getId())
                    .metricDate(userCount.getDate())
                    .cumulativeCount(userCount.getCumulativeCount())
                    .dailyCount(userCount.getDailyCount())
                    .build();

                userMetricsRepository.save(newMetrics);
                count++;
            }

            return count;
        });
    }
}

