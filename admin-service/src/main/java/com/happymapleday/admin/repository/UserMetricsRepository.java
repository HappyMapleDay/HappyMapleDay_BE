package com.happymapleday.admin.repository;

import com.happymapleday.admin.entity.UserMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMetricsRepository extends JpaRepository<UserMetrics, Long> {
    
    Optional<UserMetrics> findByMetricDate(LocalDate metricDate);
    
    List<UserMetrics> findByMetricDateBetweenOrderByMetricDateAsc(LocalDate from, LocalDate to);
    
    List<UserMetrics> findAllByOrderByMetricDateDesc();
}

