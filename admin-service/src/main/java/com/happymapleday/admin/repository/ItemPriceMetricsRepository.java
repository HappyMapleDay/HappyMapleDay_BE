package com.happymapleday.admin.repository;

import com.happymapleday.admin.entity.ItemPriceMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemPriceMetricsRepository extends JpaRepository<ItemPriceMetrics, Long> {
    
    List<ItemPriceMetrics> findByMetricDateBetweenOrderByMetricDateAsc(LocalDate from, LocalDate to);
    
    List<ItemPriceMetrics> findByBossIdAndMetricDateBetweenOrderByMetricDateAsc(
        Long bossId, LocalDate from, LocalDate to);
    
    List<ItemPriceMetrics> findByItemIdAndMetricDateBetweenOrderByMetricDateAsc(
        Long itemId, LocalDate from, LocalDate to);
    
    List<ItemPriceMetrics> findByBossIdAndItemIdAndMetricDateBetweenOrderByMetricDateAsc(
        Long bossId, Long itemId, LocalDate from, LocalDate to);
    
    List<ItemPriceMetrics> findAllByOrderByMetricDateDesc();
}

