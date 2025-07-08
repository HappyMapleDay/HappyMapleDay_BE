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
    
    // 보스 기록 존재 여부 확인
    boolean existsByCharacterIdAndBossIdAndWeekStartDate(
            Long characterId, Long bossId, LocalDate weekStartDate);
    
    // 정산별 보스 기록 삭제
    void deleteBySettlementId(Long settlementId);
} 