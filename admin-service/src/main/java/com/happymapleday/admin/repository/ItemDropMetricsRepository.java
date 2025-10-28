package com.happymapleday.admin.repository;

import com.happymapleday.admin.entity.ItemDropMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemDropMetricsRepository extends JpaRepository<ItemDropMetrics, Long> {
    
    List<ItemDropMetrics> findByMetricDateBetweenOrderByMetricDateAsc(LocalDate from, LocalDate to);
    
    List<ItemDropMetrics> findByBossIdAndMetricDateBetweenOrderByMetricDateAsc(
        Long bossId, LocalDate from, LocalDate to);
    
    List<ItemDropMetrics> findByItemIdAndMetricDateBetweenOrderByMetricDateAsc(
        Long itemId, LocalDate from, LocalDate to);
    
    List<ItemDropMetrics> findByBossIdAndItemIdAndMetricDateBetweenOrderByMetricDateAsc(
        Long bossId, Long itemId, LocalDate from, LocalDate to);
    
    List<ItemDropMetrics> findAllByOrderByMetricDateDesc();
}

