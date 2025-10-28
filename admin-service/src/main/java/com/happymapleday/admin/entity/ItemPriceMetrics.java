package com.happymapleday.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_price_metrics",
    indexes = {
        @Index(name = "idx_item_price_metrics_date", columnList = "metric_date"),
        @Index(name = "idx_item_price_metrics_boss_id", columnList = "boss_id"),
        @Index(name = "idx_item_price_metrics_item_id", columnList = "item_id"),
        @Index(name = "idx_item_price_metrics_item_name", columnList = "item_name")
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPriceMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "boss_id")
    private Long bossId;

    @Column(name = "boss_name", length = 100)
    private String bossName;

    @Column(name = "boss_name_en", length = 100)
    private String bossNameEn;

    @Column(name = "difficulty", length = 20)
    private String difficulty;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "item_name_en", nullable = false, length = 100)
    private String itemNameEn;

    @Column(name = "avg_price", nullable = false, precision = 20, scale = 2)
    @Builder.Default
    private BigDecimal avgPrice = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

