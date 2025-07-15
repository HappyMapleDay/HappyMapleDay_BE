package com.happymapleday.settlement.repository;

import com.happymapleday.settlement.entity.WeeklySettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklySettlementRepository extends JpaRepository<WeeklySettlement, Long> {
    // 사용자별 정산 조회 (최신순)
    List<WeeklySettlement> findByUserIdOrderByWeekStartDateDesc(Long userId);
    
    // 특정 사용자의 특정 주차 정산 데이터 조회
    Optional<WeeklySettlement> findByUserIdAndWorldNameAndWeekStartDate(
            Long userId, String worldName, LocalDate weekStartDate);
    
    // 특정 사용자의 특정 주차 정산 데이터 조회 (월드 구분 없이)
    List<WeeklySettlement> findByUserIdAndWeekStartDate(Long userId, LocalDate weekStartDate);
} 