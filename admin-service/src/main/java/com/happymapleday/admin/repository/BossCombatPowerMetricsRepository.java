package com.happymapleday.admin.repository;

import com.happymapleday.admin.entity.BossCombatPowerMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BossCombatPowerMetricsRepository extends JpaRepository<BossCombatPowerMetrics, Long> {
    
    Optional<BossCombatPowerMetrics> findByMetricDateAndBossIdAndCharacterClass(
        LocalDate metricDate, Long bossId, String characterClass);
    
    List<BossCombatPowerMetrics> findByMetricDateBetweenOrderByMetricDateAsc(LocalDate from, LocalDate to);
    
    List<BossCombatPowerMetrics> findByBossIdAndMetricDateBetweenOrderByMetricDateAsc(
        Long bossId, LocalDate from, LocalDate to);
    
    List<BossCombatPowerMetrics> findAllByOrderByMetricDateDesc();
}

