package com.happymapleday.settlement.service.processor;

import com.happymapleday.settlement.dto.request.SettlementRequest;
import com.happymapleday.settlement.dto.response.SettlementCompleteResponse;
import com.happymapleday.settlement.entity.WeeklyBossRecord;
import com.happymapleday.settlement.entity.WeeklySettlement;
import com.happymapleday.settlement.repository.WeeklySettlementRepository;
import com.happymapleday.settlement.service.validator.CrystalLimitValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WeeklySettlementProcessor {
    
    private final WeeklySettlementRepository weeklySettlementRepository;
    private final BossRecordProcessor bossRecordProcessor;
    private final CrystalLimitValidator crystalLimitValidator;
    
    // 새로운 정산 생성
    public SettlementCompleteResponse createSettlement(Long userId, LocalDate weekStartDate, 
                                                       SettlementRequest request) {
        // 보스 기록 생성 및 검증
        List<WeeklyBossRecord> bossRecords = bossRecordProcessor.createBossRecords(
                userId, weekStartDate, request.getBossRecords());
        
        // 결정석 제한 검증
        crystalLimitValidator.validateCrystalLimits(bossRecords);
        
        // 정산 메타데이터 생성
        WeeklySettlement settlement = createSettlementMetadata(userId, weekStartDate, request, bossRecords);
        settlement = weeklySettlementRepository.save(settlement);
        
        // 보스 기록 저장 및 물욕템 처리
        List<WeeklyBossRecord> savedBossRecords = bossRecordProcessor.saveBossRecordsWithDesireItems(
                settlement.getId(), bossRecords, request.getBossRecords());
        
        // 정산 메타데이터 업데이트
        updateSettlementMetadata(settlement, savedBossRecords);
        
        return createSettlementResponse(settlement);
    }
    
    // 기존 정산 수정
    public SettlementCompleteResponse updateSettlement(Long settlementId, Long userId, 
                                                       LocalDate weekStartDate, SettlementRequest request) {
        // 기존 보스 기록 삭제
        bossRecordProcessor.deleteExistingBossRecords(settlementId);
        
        // 새로운 보스 기록 생성 및 검증
        List<WeeklyBossRecord> bossRecords = bossRecordProcessor.createBossRecords(
                userId, weekStartDate, request.getBossRecords());
        
        // 결정석 제한 검증
        crystalLimitValidator.validateCrystalLimits(bossRecords);
        
        // 보스 기록 저장 및 물욕템 처리
        List<WeeklyBossRecord> savedBossRecords = bossRecordProcessor.saveBossRecordsWithDesireItems(
                settlementId, bossRecords, request.getBossRecords());
        
        // 정산 메타데이터 업데이트
        WeeklySettlement settlement = updateSettlementMetadata(settlementId, userId, weekStartDate, 
                request, savedBossRecords);
        
        return createSettlementResponse(settlement);
    }
    
    // 정산 메타데이터 생성
    private WeeklySettlement createSettlementMetadata(Long userId, LocalDate weekStartDate, 
                                                      SettlementRequest request, List<WeeklyBossRecord> bossRecords) {
        return WeeklySettlement.builder()
                .userId(userId)
                .worldName(request.getWorldName())
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(calculateTotalCrystalIncome(bossRecords))
                .totalDesireItemIncome(BigInteger.ZERO)
                .totalIncome(calculateTotalCrystalIncome(bossRecords))
                .totalBossCount(bossRecords.size())
                .characterCount(calculateCharacterCount(bossRecords))
                .isFinalized(true)
                .finalizedAt(LocalDateTime.now())
                .build();
    }
    
    // 정산 메타데이터 업데이트
    private void updateSettlementMetadata(WeeklySettlement settlement, List<WeeklyBossRecord> bossRecords) {
        WeeklySettlement updatedSettlement = WeeklySettlement.builder()
                .userId(settlement.getUserId())
                .worldName(settlement.getWorldName())
                .weekStartDate(settlement.getWeekStartDate())
                .totalCrystalIncome(calculateTotalCrystalIncome(bossRecords))
                .totalDesireItemIncome(calculateTotalDesireItemIncome(bossRecords))
                .totalIncome(calculateTotalIncome(bossRecords))
                .totalBossCount(bossRecords.size())
                .characterCount(calculateCharacterCount(bossRecords))
                .isFinalized(true)
                .finalizedAt(LocalDateTime.now())
                .build();
        
        weeklySettlementRepository.save(updatedSettlement);
    }
    
    // 정산 메타데이터 업데이트 (ID로)
    private WeeklySettlement updateSettlementMetadata(Long settlementId, Long userId, LocalDate weekStartDate, 
                                                      SettlementRequest request, List<WeeklyBossRecord> bossRecords) {
        WeeklySettlement updatedSettlement = WeeklySettlement.builder()
                .userId(userId)
                .worldName(request.getWorldName())
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(calculateTotalCrystalIncome(bossRecords))
                .totalDesireItemIncome(calculateTotalDesireItemIncome(bossRecords))
                .totalIncome(calculateTotalIncome(bossRecords))
                .totalBossCount(bossRecords.size())
                .characterCount(calculateCharacterCount(bossRecords))
                .isFinalized(true)
                .finalizedAt(LocalDateTime.now())
                .build();
        
        return weeklySettlementRepository.save(updatedSettlement);
    }
    
    // 응답 생성
    private SettlementCompleteResponse createSettlementResponse(WeeklySettlement settlement) {
        return SettlementCompleteResponse.builder()
                .settlementId(settlement.getId())
                .weekStartDate(settlement.getWeekStartDate())
                .totalCrystalIncome(settlement.getTotalCrystalIncome())
                .totalDesireItemIncome(settlement.getTotalDesireItemIncome())
                .totalIncome(settlement.getTotalIncome())
                .totalBossCount(settlement.getTotalBossCount())
                .characterCount(settlement.getCharacterCount())
                .finalizedAt(settlement.getFinalizedAt())
                .build();
    }
    
    // 총 결정석 수입 계산
    private BigInteger calculateTotalCrystalIncome(List<WeeklyBossRecord> bossRecords) {
        return bossRecords.stream()
                .map(WeeklyBossRecord::getCrystalIncome)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }
    
    // 총 물욕템 수입 계산
    private BigInteger calculateTotalDesireItemIncome(List<WeeklyBossRecord> bossRecords) {
        return bossRecords.stream()
                .map(WeeklyBossRecord::getDesireItemIncome)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }
    
    // 총 수입 계산
    private BigInteger calculateTotalIncome(List<WeeklyBossRecord> bossRecords) {
        return bossRecords.stream()
                .map(WeeklyBossRecord::getTotalIncome)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }
    
    // 캐릭터 수 계산
    private int calculateCharacterCount(List<WeeklyBossRecord> bossRecords) {
        return (int) bossRecords.stream()
                .map(WeeklyBossRecord::getCharacterId)
                .distinct()
                .count();
    }
} 