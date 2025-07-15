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
            WeeklyBossRecord bossRecord = createBossRecord(userId, weekStartDate, bossRequest, null);
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
            
            // 물욕템 수입 미리 계산
            BigInteger desireItemIncome = calculateDesireItemIncome(bossRequest);
            
            // 완전한 보스 기록 생성 (settlementId 포함)
            WeeklyBossRecord completeBossRecord = createCompleteBossRecord(
                    bossRecord, settlementId, desireItemIncome);
            
            // 보스 기록 저장
            WeeklyBossRecord savedRecord = weeklyBossRecordRepository.save(completeBossRecord);
            
            // 물욕템 처리 (물욕템이 있는 경우에만)
            if (desireItemIncome.compareTo(BigInteger.ZERO) > 0) {
                desireItemProcessor.processDesireItems(savedRecord.getId(), bossRequest.getDesireItems());
            }
            
            savedRecords.add(savedRecord);
        }
        
        return savedRecords;
    }
    
    // 기존 보스 기록 삭제
    public void deleteExistingBossRecords(Long settlementId) {
        List<WeeklyBossRecord> existingBossRecords = weeklyBossRecordRepository
                .findBySettlementId(settlementId);
        
        // 물욕템 기록 삭제
        existingBossRecords.forEach(bossRecord -> 
                desireItemProcessor.deleteDesireItemsByBossRecordId(bossRecord.getId()));
        
        // 보스 기록 삭제
        weeklyBossRecordRepository.deleteBySettlementId(settlementId);
    }
    
    // 보스 기록 생성 헬퍼 메서드
    private WeeklyBossRecord createBossRecord(Long userId, LocalDate weekStartDate, 
                                              BossRecordRequest bossRequest, Long settlementId) {
        return WeeklyBossRecord.builder()
                .settlementId(settlementId)
                .userId(userId)
                .characterId(bossRequest.getCharacterId())
                .bossId(bossRequest.getBossId())
                .weekStartDate(weekStartDate)
                .crystalIncome(bossRequest.getCrystalIncome())
                .partySize(bossRequest.getPartySize())
                .desireItemIncome(BigInteger.ZERO)
                .totalIncome(bossRequest.getCrystalIncome())
                .build();
    }
    
    // 완전한 보스 기록 생성 (물욕템 수입 포함)
    private WeeklyBossRecord createCompleteBossRecord(WeeklyBossRecord bossRecord, Long settlementId, 
                                                      BigInteger desireItemIncome) {
        BigInteger totalIncome = bossRecord.getCrystalIncome().add(desireItemIncome);
        
        return WeeklyBossRecord.builder()
                .settlementId(settlementId)
                .userId(bossRecord.getUserId())
                .characterId(bossRecord.getCharacterId())
                .bossId(bossRecord.getBossId())
                .weekStartDate(bossRecord.getWeekStartDate())
                .crystalIncome(bossRecord.getCrystalIncome())
                .partySize(bossRecord.getPartySize())
                .desireItemIncome(desireItemIncome)
                .totalIncome(totalIncome)
                .build();
    }
    
    // 물욕템 수입 미리 계산
    private BigInteger calculateDesireItemIncome(BossRecordRequest bossRequest) {
        if (bossRequest.getDesireItems() == null || bossRequest.getDesireItems().isEmpty()) {
            return BigInteger.ZERO;
        }
        
        return bossRequest.getDesireItems().stream()
                .map(desireItem -> desireItem.getSalePrice())
                .reduce(BigInteger.ZERO, BigInteger::add);
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