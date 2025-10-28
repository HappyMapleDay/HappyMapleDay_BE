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
@Table(name = "boss_kill_metrics", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_boss_kill_metrics_date_boss", columnNames = {"metric_date", "boss_id"})
    },
    indexes = {
        @Index(name = "idx_boss_kill_metrics_date", columnList = "metric_date"),
        @Index(name = "idx_boss_kill_metrics_boss_id", columnList = "boss_id"),
        @Index(name = "idx_boss_kill_metrics_boss_name", columnList = "boss_name")
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossKillMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "boss_id", nullable = false)
    private Long bossId;

    @Column(name = "boss_name", nullable = false, length = 100)
    private String bossName;

    @Column(name = "boss_name_en", nullable = false, length = 100)
    private String bossNameEn;

    @Column(name = "difficulty", nullable = false, length = 20)
    private String difficulty;

    @Column(name = "total_kills", nullable = false)
    @Builder.Default
    private Long totalKills = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

