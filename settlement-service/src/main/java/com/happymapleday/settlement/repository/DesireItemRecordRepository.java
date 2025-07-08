package com.happymapleday.settlement.repository;

import com.happymapleday.settlement.entity.DesireItemRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DesireItemRecordRepository extends JpaRepository<DesireItemRecord, Long> {
    
    // 보스 기록별 물욕템 조회
    List<DesireItemRecord> findByWeeklyBossRecordIdOrderByAcquiredAtAsc(Long weeklyBossRecordId);
    
    // 보스 기록별 물욕템 삭제 (정산 삭제시 사용)
    void deleteByWeeklyBossRecordId(Long weeklyBossRecordId);
} 