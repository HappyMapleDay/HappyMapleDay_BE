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
public class BatchStatusResponse {
    private BatchType batchType;
    private LocalDateTime lastExecutedAt;
    private BatchStatus status;
    private LocalDateTime nextScheduledAt;
    private Integer recordCount;
    private String message;
    private Long durationMs;
}

