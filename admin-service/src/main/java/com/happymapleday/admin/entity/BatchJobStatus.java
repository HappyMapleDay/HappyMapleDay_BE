package com.happymapleday.admin.entity;

import com.happymapleday.admin.enums.BatchStatus;
import com.happymapleday.admin.enums.BatchType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "batch_job_status",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_batch_job_status_type", columnNames = "batch_type")
    },
    indexes = {
        @Index(name = "idx_batch_job_status_executed_at", columnList = "last_executed_at"),
        @Index(name = "idx_batch_job_status_status", columnList = "status")
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchJobStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "batch_type", nullable = false, length = 50)
    private BatchType batchType;

    @Column(name = "last_executed_at")
    private LocalDateTime lastExecutedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BatchStatus status = BatchStatus.PENDING;

    @Column(name = "next_scheduled_at")
    private LocalDateTime nextScheduledAt;

    @Column(name = "record_count")
    @Builder.Default
    private Integer recordCount = 0;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "duration_ms")
    private Long durationMs;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void updateStatus(BatchStatus status, Integer recordCount, String message, Long durationMs) {
        this.status = status;
        this.recordCount = recordCount;
        this.message = message;
        this.durationMs = durationMs;
        this.lastExecutedAt = LocalDateTime.now();
    }
}

