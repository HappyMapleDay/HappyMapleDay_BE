package com.happymapleday.settlement.repository;

import com.happymapleday.settlement.entity.WeeklyBossRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyBossRecordRepository extends JpaRepository<WeeklyBossRecord, Long> {
    
    // 정산별 보스 기록 조회
    List<WeeklyBossRecord> findBySettlementIdOrderByCreatedAtAsc(Long settlementId);
    
    // 사용자별, 주차별 보스 기록 조회
    List<WeeklyBossRecord> findByUserIdAndWeekStartDateOrderByCreatedAtAsc(
            Long userId, LocalDate weekStartDate);
    
    // 캐릭터별, 주차별 보스 기록 조회
    List<WeeklyBossRecord> findByCharacterIdAndWeekStartDateOrderByCreatedAtAsc(
            Long characterId, LocalDate weekStartDate);
    
    // 특정 보스 기록 조회 (중복 확인용)
    Optional<WeeklyBossRecord> findByCharacterIdAndBossIdAndWeekStartDate(
            Long characterId, Long bossId, LocalDate weekStartDate);
    
    // 보스 기록 존재 여부 확인
    boolean existsByCharacterIdAndBossIdAndWeekStartDate(
            Long characterId, Long bossId, LocalDate weekStartDate);
    
    // 정산별 보스 기록 삭제
    void deleteBySettlementId(Long settlementId);
    
    // 사용자별 보스 기록 통계
    @Query("SELECT COUNT(wbr) FROM WeeklyBossRecord wbr " +
           "WHERE wbr.userId = :userId " +
           "AND wbr.weekStartDate BETWEEN :startDate AND :endDate")
    long countBossRecordsByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    // 캐릭터별 보스 수익 통계
    @Query("SELECT SUM(wbr.totalIncome) FROM WeeklyBossRecord wbr " +
           "WHERE wbr.characterId = :characterId " +
           "AND wbr.weekStartDate BETWEEN :startDate AND :endDate")
    Optional<Long> getTotalIncomeByCharacterIdAndPeriod(
            @Param("characterId") Long characterId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    // 보스별 수익 통계
    @Query("SELECT AVG(wbr.totalIncome) FROM WeeklyBossRecord wbr " +
           "WHERE wbr.bossId = :bossId " +
           "AND wbr.weekStartDate BETWEEN :startDate AND :endDate")
    Optional<Double> getAverageIncomeByBossIdAndPeriod(
            @Param("bossId") Long bossId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    // 주차별 보스 완료 횟수
    @Query("SELECT COUNT(wbr) FROM WeeklyBossRecord wbr " +
           "WHERE wbr.weekStartDate = :weekStartDate")
    long countBossRecordsByWeek(@Param("weekStartDate") LocalDate weekStartDate);
    
    // 사용자의 특정 주차 캐릭터 수 조회
    @Query("SELECT COUNT(DISTINCT wbr.characterId) FROM WeeklyBossRecord wbr " +
           "WHERE wbr.userId = :userId AND wbr.weekStartDate = :weekStartDate")
    long countDistinctCharactersByUserIdAndWeek(
            @Param("userId") Long userId, @Param("weekStartDate") LocalDate weekStartDate);
} 