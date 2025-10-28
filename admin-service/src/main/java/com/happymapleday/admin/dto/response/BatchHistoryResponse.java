package com.happymapleday.admin.dto.response;

import com.happymapleday.admin.enums.BatchStatus;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.enums.ExecutionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchHistoryResponse {
    private Long id;
    private BatchType batchType;
    private LocalDateTime executedAt;
    private BatchStatus status;
    private ExecutionType executionType;
    private LocalDate targetDateFrom;
    private LocalDate targetDateTo;
    private Integer recordCount;
    private String message;
    private Long durationMs;
    private String executedBy;
}

