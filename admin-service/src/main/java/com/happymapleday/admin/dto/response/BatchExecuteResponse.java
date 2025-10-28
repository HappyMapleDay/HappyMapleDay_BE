package com.happymapleday.admin.dto.response;

import com.happymapleday.admin.enums.BatchStatus;
import com.happymapleday.admin.enums.BatchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchExecuteResponse {
    private BatchType batchType;
    private BatchStatus status;
    private LocalDateTime executedAt;
    private Integer recordCount;
    private String message;
    private Long durationMs;
}

