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
    
    // 사용자별, 월드별, 주차별 정산 조회
    Optional<WeeklySettlement> findByUserIdAndWorldNameAndWeekStartDate(
            Long userId, String worldName, LocalDate weekStartDate);
    
    // 사용자별, 월드별 정산 조회 (최신순)
    List<WeeklySettlement> findByUserIdAndWorldNameOrderByWeekStartDateDesc(
            Long userId, String worldName);
    
    // 사용자별 정산 조회 (최신순)
    List<WeeklySettlement> findByUserIdOrderByWeekStartDateDesc(Long userId);
    
    // 특정 기간 정산 조회
    @Query("SELECT ws FROM WeeklySettlement ws " +
           "WHERE ws.userId = :userId " +
           "AND ws.weekStartDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ws.weekStartDate DESC")
    List<WeeklySettlement> findByUserIdAndWeekStartDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    // 정산 완료 여부 확인
    boolean existsByUserIdAndWorldNameAndWeekStartDateAndIsFinalizedTrue(
            Long userId, String worldName, LocalDate weekStartDate);
    
    // 사용자의 월드별 최근 정산일 조회
    @Query("SELECT MAX(ws.weekStartDate) FROM WeeklySettlement ws " +
           "WHERE ws.userId = :userId AND ws.worldName = :worldName " +
           "AND ws.isFinalized = true")
    Optional<LocalDate> findLatestFinalizedWeekByUserIdAndWorldName(
            @Param("userId") Long userId, @Param("worldName") String worldName);
    
    // 사용자별 월드별 총 수익 통계
    @Query("SELECT SUM(ws.totalIncome) FROM WeeklySettlement ws " +
           "WHERE ws.userId = :userId AND ws.worldName = :worldName " +
           "AND ws.isFinalized = true")
    Optional<Long> getTotalIncomeByUserIdAndWorldName(
            @Param("userId") Long userId, @Param("worldName") String worldName);
    
    // 특정 주차에 완료된 정산 개수 (시스템 통계용)
    @Query("SELECT COUNT(ws) FROM WeeklySettlement ws " +
           "WHERE ws.weekStartDate = :weekStartDate AND ws.isFinalized = true")
    long countFinalizedSettlementsByWeek(@Param("weekStartDate") LocalDate weekStartDate);
} 