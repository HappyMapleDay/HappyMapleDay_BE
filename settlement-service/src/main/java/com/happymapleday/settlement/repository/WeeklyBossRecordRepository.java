package com.happymapleday.settlement.repository;

import com.happymapleday.settlement.entity.WeeklyBossRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeeklyBossRecordRepository extends JpaRepository<WeeklyBossRecord, Long> {
    
    // 정산별 보스 기록 조회
    List<WeeklyBossRecord> findBySettlementId(Long settlementId);
    
    // 정산별 보스 기록 삭제
    void deleteBySettlementId(Long settlementId);
    
    // 보스 기록 존재 여부 확인
    boolean existsByCharacterIdAndBossIdAndWeekStartDate(
            Long characterId, Long bossId, LocalDate weekStartDate);
}