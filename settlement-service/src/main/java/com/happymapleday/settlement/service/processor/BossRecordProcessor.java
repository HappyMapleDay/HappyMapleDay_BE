package com.happymapleday.settlement.service.processor;

import com.happymapleday.settlement.dto.request.BossRecordRequest;
import com.happymapleday.settlement.entity.WeeklyBossRecord;
import com.happymapleday.settlement.repository.WeeklyBossRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BossRecordProcessor {
    
    private final WeeklyBossRecordRepository weeklyBossRecordRepository;
    private final DesireItemProcessor desireItemProcessor;
    
    // 보스 기록 생성 (중복 검증 포함)
    public List<WeeklyBossRecord> createBossRecords(Long userId, LocalDate weekStartDate, 
                                                    List<BossRecordRequest> bossRequests) {
        List<WeeklyBossRecord> bossRecords = new ArrayList<>();
        
        for (BossRecordRequest bossRequest : bossRequests) {
            // 중복 보스 기록 확인
            validateBossRecordNotExists(bossRequest.getCharacterId(), bossRequest.getBossId(), weekStartDate);
            
            // 보스 기록 생성
            WeeklyBossRecord bossRecord = WeeklyBossRecord.builder()
                    .settlementId(null) // 나중에 설정
                    .userId(userId)
                    .characterId(bossRequest.getCharacterId())
                    .bossId(bossRequest.getBossId())
                    .weekStartDate(weekStartDate)
                    .crystalIncome(bossRequest.getCrystalIncome())
                    .partySize(bossRequest.getPartySize())
                    .desireItemIncome(BigInteger.ZERO)
                    .totalIncome(bossRequest.getCrystalIncome())
                    .build();
            
            bossRecords.add(bossRecord);
        }
        
        return bossRecords;
    }
    
    // 보스 기록 저장 및 물욕템 처리
    public List<WeeklyBossRecord> saveBossRecordsWithDesireItems(Long settlementId, 
                                                                List<WeeklyBossRecord> bossRecords,
                                                                List<BossRecordRequest> bossRequests) {
        List<WeeklyBossRecord> savedRecords = new ArrayList<>();
        
        for (int i = 0; i < bossRecords.size(); i++) {
            WeeklyBossRecord bossRecord = bossRecords.get(i);
            BossRecordRequest bossRequest = bossRequests.get(i);
            
            // settlementId 설정
            bossRecord = WeeklyBossRecord.builder()
                    .settlementId(settlementId)
                    .userId(bossRecord.getUserId())
                    .characterId(bossRecord.getCharacterId())
                    .bossId(bossRecord.getBossId())
                    .weekStartDate(bossRecord.getWeekStartDate())
                    .crystalIncome(bossRecord.getCrystalIncome())
                    .partySize(bossRecord.getPartySize())
                    .desireItemIncome(BigInteger.ZERO)
                    .totalIncome(bossRecord.getCrystalIncome())
                    .build();
            
            // 보스 기록 저장
            bossRecord = weeklyBossRecordRepository.save(bossRecord);
            
            // 물욕템 처리
            BigInteger desireItemIncome = desireItemProcessor.processDesireItems(
                    bossRecord.getId(), bossRequest.getDesireItems());
            
            // 물욕템 수입 포함하여 보스 기록 업데이트
            if (desireItemIncome.compareTo(BigInteger.ZERO) > 0) {
                bossRecord = WeeklyBossRecord.builder()
                        .settlementId(bossRecord.getSettlementId())
                        .userId(bossRecord.getUserId())
                        .characterId(bossRecord.getCharacterId())
                        .bossId(bossRecord.getBossId())
                        .weekStartDate(bossRecord.getWeekStartDate())
                        .crystalIncome(bossRecord.getCrystalIncome())
                        .partySize(bossRecord.getPartySize())
                        .desireItemIncome(desireItemIncome)
                        .totalIncome(bossRecord.getCrystalIncome().add(desireItemIncome))
                        .build();
                
                bossRecord = weeklyBossRecordRepository.save(bossRecord);
            }
            
            savedRecords.add(bossRecord);
        }
        
        return savedRecords;
    }
    
    // 기존 보스 기록 삭제
    public void deleteExistingBossRecords(Long settlementId) {
        List<WeeklyBossRecord> existingBossRecords = weeklyBossRecordRepository.findBySettlementIdOrderByCreatedAtAsc(settlementId);
        
        for (WeeklyBossRecord bossRecord : existingBossRecords) {
            desireItemProcessor.deleteDesireItemsByBossRecordId(bossRecord.getId());
        }
        
        weeklyBossRecordRepository.deleteBySettlementId(settlementId);
    }
    
    // 중복 보스 기록 검증
    private void validateBossRecordNotExists(Long characterId, Long bossId, LocalDate weekStartDate) {
        boolean bossRecordExists = weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                characterId, bossId, weekStartDate);
        
        if (bossRecordExists) {
            throw new IllegalArgumentException(
                    String.format("캐릭터 %d의 보스 %d는 이미 이번 주에 완료된 기록이 있습니다.", 
                            characterId, bossId));
        }
    }
} 