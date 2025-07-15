package com.happymapleday.settlement.service.processor;

import com.happymapleday.settlement.dto.request.SettlementRequest;
import com.happymapleday.settlement.dto.response.SettlementCompleteResponse;
import com.happymapleday.settlement.entity.SettlementStatus;
import com.happymapleday.settlement.entity.WeeklyBossRecord;
import com.happymapleday.settlement.entity.WeeklySettlement;
import com.happymapleday.settlement.repository.WeeklySettlementRepository;
import com.happymapleday.settlement.service.validator.CrystalLimitValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDate;
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
        
        // 초기 정산 메타데이터 생성 및 저장
        WeeklySettlement settlement = createInitialSettlementMetadata(userId, weekStartDate, request, bossRecords);
        settlement = weeklySettlementRepository.save(settlement);
        
        // 보스 기록 저장 및 물욕템 처리
        List<WeeklyBossRecord> savedBossRecords = bossRecordProcessor.saveBossRecordsWithDesireItems(
                settlement.getId(), bossRecords, request.getBossRecords());
        
        // 최종 정산 메타데이터 업데이트 (물욕템 수입 포함)
        settlement = updateSettlementWithFinalData(settlement, savedBossRecords);
        
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
        
        // 정산 메타데이터 완전 업데이트
        WeeklySettlement settlement = createCompleteSettlementMetadata(
                settlementId, userId, weekStartDate, request, savedBossRecords);
        
        return createSettlementResponse(settlement);
    }

    // 새로운 정산 생성 (PENDING 상태)
    public SettlementCompleteResponse createSettlementPending(Long userId, LocalDate weekStartDate, 
                                                             SettlementRequest request) {
        // 보스 기록 생성 및 검증
        List<WeeklyBossRecord> bossRecords = bossRecordProcessor.createBossRecords(
                userId, weekStartDate, request.getBossRecords());
        
        // 결정석 제한 검증
        crystalLimitValidator.validateCrystalLimits(bossRecords);
        
        // 초기 정산 메타데이터 생성 및 저장 (PENDING 상태)
        WeeklySettlement settlement = createInitialSettlementMetadataPending(userId, weekStartDate, request, bossRecords);
        settlement = weeklySettlementRepository.save(settlement);
        
        // 보스 기록 저장 및 물욕템 처리
        List<WeeklyBossRecord> savedBossRecords = bossRecordProcessor.saveBossRecordsWithDesireItems(
                settlement.getId(), bossRecords, request.getBossRecords());
        
        // 최종 정산 메타데이터 업데이트 (물욕템 수입 포함, PENDING 상태)
        settlement = updateSettlementWithFinalDataPending(settlement, savedBossRecords);
        
        return createSettlementResponse(settlement);
    }
    
    // 기존 정산 수정 (PENDING 상태)
    public SettlementCompleteResponse updateSettlementPending(Long settlementId, Long userId, 
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
        
        // 정산 메타데이터 완전 업데이트 (PENDING 상태)
        WeeklySettlement settlement = createCompleteSettlementMetadataPending(
                settlementId, userId, weekStartDate, request, savedBossRecords);
        
        return createSettlementResponse(settlement);
    }
    
    // 초기 정산 메타데이터 생성 (결정석 수입만 계산)
    private WeeklySettlement createInitialSettlementMetadata(Long userId, LocalDate weekStartDate, 
                                                            SettlementRequest request, List<WeeklyBossRecord> bossRecords) {
        BigInteger totalCrystalIncome = calculateTotalCrystalIncome(bossRecords);
        
        return WeeklySettlement.builder()
                .userId(userId)
                .worldName(request.getWorldName())
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(totalCrystalIncome != null ? totalCrystalIncome : BigInteger.ZERO)
                .totalDesireItemIncome(BigInteger.ZERO)
                .totalIncome(totalCrystalIncome != null ? totalCrystalIncome : BigInteger.ZERO)
                .totalBossCount(bossRecords.size())
                .characterCount(calculateCharacterCount(bossRecords))
                .status(SettlementStatus.COMPLETED)
                .build();
    }
    
    // 초기 정산 메타데이터 생성 (PENDING 상태)
    private WeeklySettlement createInitialSettlementMetadataPending(Long userId, LocalDate weekStartDate, 
                                                                     SettlementRequest request, List<WeeklyBossRecord> bossRecords) {
        BigInteger totalCrystalIncome = calculateTotalCrystalIncome(bossRecords);
        
        return WeeklySettlement.builder()
                .userId(userId)
                .worldName(request.getWorldName())
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(totalCrystalIncome != null ? totalCrystalIncome : BigInteger.ZERO)
                .totalDesireItemIncome(BigInteger.ZERO)
                .totalIncome(totalCrystalIncome != null ? totalCrystalIncome : BigInteger.ZERO)
                .totalBossCount(bossRecords.size())
                .characterCount(calculateCharacterCount(bossRecords))
                .status(SettlementStatus.PENDING)
                .build();
    }
    
    // 최종 정산 메타데이터 업데이트 (물욕템 수입 포함)
    private WeeklySettlement updateSettlementWithFinalData(WeeklySettlement settlement, 
                                                           List<WeeklyBossRecord> savedBossRecords) {
        BigInteger totalCrystalIncome = calculateTotalCrystalIncome(savedBossRecords);
        BigInteger totalDesireItemIncome = calculateTotalDesireItemIncome(savedBossRecords);
        BigInteger totalIncome = calculateTotalIncome(savedBossRecords);
        
        WeeklySettlement updatedSettlement = WeeklySettlement.builder()
                .id(settlement.getId())
                .userId(settlement.getUserId())
                .worldName(settlement.getWorldName())
                .weekStartDate(settlement.getWeekStartDate())
                .totalCrystalIncome(totalCrystalIncome)
                .totalDesireItemIncome(totalDesireItemIncome)
                .totalIncome(totalIncome)
                .totalBossCount(savedBossRecords.size())
                .characterCount(calculateCharacterCount(savedBossRecords))
                .status(SettlementStatus.COMPLETED)
                .build();
        
        return weeklySettlementRepository.save(updatedSettlement);
    }
    
    // 최종 정산 메타데이터 업데이트 (물욕템 수입 포함, PENDING 상태)
    private WeeklySettlement updateSettlementWithFinalDataPending(WeeklySettlement settlement, 
                                                                   List<WeeklyBossRecord> savedBossRecords) {
        BigInteger totalCrystalIncome = calculateTotalCrystalIncome(savedBossRecords);
        BigInteger totalDesireItemIncome = calculateTotalDesireItemIncome(savedBossRecords);
        BigInteger totalIncome = calculateTotalIncome(savedBossRecords);
        
        WeeklySettlement updatedSettlement = WeeklySettlement.builder()
                .id(settlement.getId())
                .userId(settlement.getUserId())
                .worldName(settlement.getWorldName())
                .weekStartDate(settlement.getWeekStartDate())
                .totalCrystalIncome(totalCrystalIncome)
                .totalDesireItemIncome(totalDesireItemIncome)
                .totalIncome(totalIncome)
                .totalBossCount(savedBossRecords.size())
                .characterCount(calculateCharacterCount(savedBossRecords))
                .status(SettlementStatus.PENDING)
                .build();
        
        return weeklySettlementRepository.save(updatedSettlement);
    }
    
    // 완전한 정산 메타데이터 생성 (수정시 사용)
    private WeeklySettlement createCompleteSettlementMetadata(Long settlementId, Long userId, 
                                                             LocalDate weekStartDate, SettlementRequest request, 
                                                             List<WeeklyBossRecord> bossRecords) {
        WeeklySettlement settlement = WeeklySettlement.builder()
                .id(settlementId)
                .userId(userId)
                .worldName(request.getWorldName())
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(calculateTotalCrystalIncome(bossRecords))
                .totalDesireItemIncome(calculateTotalDesireItemIncome(bossRecords))
                .totalIncome(calculateTotalIncome(bossRecords))
                .totalBossCount(bossRecords.size())
                .characterCount(calculateCharacterCount(bossRecords))
                .status(SettlementStatus.COMPLETED)
                .build();
        
        return weeklySettlementRepository.save(settlement);
    }
    
    // 완전한 정산 메타데이터 생성 (수정시 사용, PENDING 상태)
    private WeeklySettlement createCompleteSettlementMetadataPending(Long settlementId, Long userId, 
                                                                     LocalDate weekStartDate, SettlementRequest request, 
                                                                     List<WeeklyBossRecord> bossRecords) {
        WeeklySettlement settlement = WeeklySettlement.builder()
                .id(settlementId)
                .userId(userId)
                .worldName(request.getWorldName())
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(calculateTotalCrystalIncome(bossRecords))
                .totalDesireItemIncome(calculateTotalDesireItemIncome(bossRecords))
                .totalIncome(calculateTotalIncome(bossRecords))
                .totalBossCount(bossRecords.size())
                .characterCount(calculateCharacterCount(bossRecords))
                .status(SettlementStatus.PENDING)
                .build();
        
        return weeklySettlementRepository.save(settlement);
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