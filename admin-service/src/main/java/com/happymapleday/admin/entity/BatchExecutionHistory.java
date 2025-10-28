package com.happymapleday.admin.entity;

import com.happymapleday.admin.enums.BatchStatus;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.enums.ExecutionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "batch_execution_history",
    indexes = {
        @Index(name = "idx_batch_execution_type_time", columnList = "batch_type, executed_at"),
        @Index(name = "idx_batch_execution_status", columnList = "status"),
        @Index(name = "idx_batch_execution_executed_at", columnList = "executed_at"),
        @Index(name = "idx_batch_execution_executed_by", columnList = "executed_by")
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "batch_type", nullable = false, length = 50)
    private BatchType batchType;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BatchStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "execution_type", nullable = false, length = 20)
    @Builder.Default
    private ExecutionType executionType = ExecutionType.SCHEDULED;

    @Column(name = "target_date_from")
    private LocalDate targetDateFrom;

    @Column(name = "target_date_to")
    private LocalDate targetDateTo;

    @Column(name = "record_count")
    @Builder.Default
    private Integer recordCount = 0;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "executed_by", length = 100)
    @Builder.Default
    private String executedBy = "SYSTEM";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void complete(BatchStatus status, Integer recordCount, String message, Long durationMs) {
        this.status = status;
        this.recordCount = recordCount;
        this.message = message;
        this.durationMs = durationMs;
    }
}

