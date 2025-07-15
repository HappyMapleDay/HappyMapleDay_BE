package com.happymapleday.settlement.service.impl;

import com.happymapleday.settlement.dto.request.SettlementRequest;
import com.happymapleday.settlement.dto.response.BossRecordDetailResponse;
import com.happymapleday.settlement.dto.response.CurrentWeekStatusResponse;
import com.happymapleday.settlement.dto.response.SettlementCompleteResponse;
import com.happymapleday.settlement.dto.response.SettlementStatusResponse;
import com.happymapleday.settlement.dto.response.SettlementDetailResponse;
import com.happymapleday.settlement.entity.WeeklyBossRecord;
import com.happymapleday.settlement.entity.WeeklySettlement;
import com.happymapleday.settlement.repository.WeeklyBossRecordRepository;
import com.happymapleday.settlement.repository.WeeklySettlementRepository;
import com.happymapleday.settlement.service.SettlementService;
import com.happymapleday.settlement.service.util.WeekCalculator;
import com.happymapleday.settlement.service.processor.WeeklySettlementProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SettlementServiceImpl implements SettlementService {
    
    private final WeeklySettlementRepository weeklySettlementRepository;
    private final WeeklyBossRecordRepository weeklyBossRecordRepository;
    private final WeekCalculator weekCalculator;
    private final WeeklySettlementProcessor settlementProcessor;
    
    @Override
    public void deleteSettlement(Long settlementId, Long userId) {
        WeeklySettlement settlement = findSettlementById(settlementId);
        validateUserOwnership(settlement, userId);
        weeklySettlementRepository.delete(settlement);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SettlementStatusResponse getSettlementStatus(Long userId, LocalDate weekStartDate) {
        Optional<WeeklySettlement> settlement = findSettlementByUserAndWeek(userId, weekStartDate);
        
        if (settlement.isEmpty()) {
            return SettlementStatusResponse.builder()
                    .isFinalized(false)
                    .weekStartDate(weekStartDate)
                    .build();
        }
        
        return SettlementStatusResponse.from(settlement.get());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CurrentWeekStatusResponse getCurrentWeekStatus(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate currentWeekStart = weekCalculator.getWeekStartDate(today);
        
        // 현재 주차 완료 여부 확인 (효율적인 조회)
        Optional<WeeklySettlement> currentWeekSettlement = findSettlementByUserAndWeek(userId, currentWeekStart);
        boolean isCompleted = currentWeekSettlement
                .map(WeeklySettlement::getIsFinalized)
                .orElse(false);
        
        LocalDate nextWeekStart = currentWeekStart.plusWeeks(1);
        LocalDate nextResetDate = weekCalculator.getNextResetDate(today);
        int remainingDays = weekCalculator.getRemainingDays(today);
        
        return CurrentWeekStatusResponse.builder()
                .isCompleted(isCompleted)
                .currentWeekStart(currentWeekStart)
                .nextWeekStart(nextWeekStart)
                .remainingDays(remainingDays)
                .nextResetDate(nextResetDate.atStartOfDay())
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public SettlementDetailResponse getSettlementDetail(Long userId, LocalDate weekStartDate) {
        Optional<WeeklySettlement> settlement = findSettlementByUserAndWeek(userId, weekStartDate);
        
        if (settlement.isEmpty()) {
            return SettlementDetailResponse.builder()
                    .isFinalized(false)
                    .weekStartDate(weekStartDate)
                    .bossRecords(List.of())
                    .build();
        }
        
        WeeklySettlement weeklySettlement = settlement.get();
        List<WeeklyBossRecord> bossRecords = weeklyBossRecordRepository
                .findBySettlementIdOrderByCreatedAtAsc(weeklySettlement.getId());
        
        List<BossRecordDetailResponse> bossDetails = bossRecords.stream()
                .map(BossRecordDetailResponse::from)
                .collect(Collectors.toList());
        
        return SettlementDetailResponse.from(weeklySettlement, bossDetails);
    }
    
    @Override
    public SettlementCompleteResponse upsertSettlement(Long userId, LocalDate weekStartDate, SettlementRequest request) {
        Optional<WeeklySettlement> existingSettlement = weeklySettlementRepository
                .findByUserIdAndWorldNameAndWeekStartDate(userId, request.getWorldName(), weekStartDate);
        
        if (existingSettlement.isPresent()) {
            // 기존 정산이 있으면 수정
            return settlementProcessor.updateSettlement(
                    existingSettlement.get().getId(), userId, weekStartDate, request);
        } else {
            // 기존 정산이 없으면 새로 생성
            return settlementProcessor.createSettlement(userId, weekStartDate, request);
        }
    }
    
    // 공통 메서드들
    private WeeklySettlement findSettlementById(Long settlementId) {
        return weeklySettlementRepository.findById(settlementId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 정산입니다."));
    }
    
    private void validateUserOwnership(WeeklySettlement settlement, Long userId) {
        if (!settlement.getUserId().equals(userId)) {
            throw new IllegalArgumentException("다른 사용자의 정산은 삭제할 수 없습니다.");
        }
    }
    
    private Optional<WeeklySettlement> findSettlementByUserAndWeek(Long userId, LocalDate weekStartDate) {
        return weeklySettlementRepository.findFirstByUserIdAndWeekStartDateOrderByCreatedAtDesc(userId, weekStartDate);
    }
} 