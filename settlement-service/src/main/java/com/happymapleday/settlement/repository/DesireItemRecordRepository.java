package com.happymapleday.settlement.repository;

import com.happymapleday.settlement.entity.DesireItemRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesireItemRecordRepository extends JpaRepository<DesireItemRecord, Long> {
    
    // 보스 기록별 물욕템 조회
    List<DesireItemRecord> findByWeeklyBossRecordIdOrderByAcquiredAtAsc(Long weeklyBossRecordId);
    
    // 보스 기록별 물욕템 삭제 (정산 삭제시 사용)
    void deleteByWeeklyBossRecordId(Long weeklyBossRecordId);
} 