package com.happymapleday.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_metrics", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_metrics_date", columnNames = "metric_date")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "cumulative_count", nullable = false)
    private Long cumulativeCount;

    @Column(name = "daily_count", nullable = false)
    @Builder.Default
    private Integer dailyCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

