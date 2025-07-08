package com.happymapleday.settlement.repository;

import com.happymapleday.settlement.entity.WeeklySettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklySettlementRepository extends JpaRepository<WeeklySettlement, Long> {
    // 사용자별 정산 조회 (최신순)
    List<WeeklySettlement> findByUserIdOrderByWeekStartDateDesc(Long userId);
} 