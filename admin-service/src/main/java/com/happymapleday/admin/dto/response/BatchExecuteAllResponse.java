package com.happymapleday.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchExecuteAllResponse {
    private LocalDateTime executedAt;
    private Long totalDurationMs;
    private List<BatchExecuteResponse> results;
}

