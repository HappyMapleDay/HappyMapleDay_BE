package com.happymapleday.admin.repository;

import com.happymapleday.admin.entity.BossKillMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BossKillMetricsRepository extends JpaRepository<BossKillMetrics, Long> {
    
    Optional<BossKillMetrics> findByMetricDateAndBossId(LocalDate metricDate, Long bossId);
    
    List<BossKillMetrics> findByMetricDateBetweenOrderByMetricDateAsc(LocalDate from, LocalDate to);
    
    List<BossKillMetrics> findByBossIdAndMetricDateBetweenOrderByMetricDateAsc(Long bossId, LocalDate from, LocalDate to);
    
    List<BossKillMetrics> findAllByOrderByMetricDateDesc();
}

