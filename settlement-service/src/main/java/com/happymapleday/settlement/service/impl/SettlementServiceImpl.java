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
import com.happymapleday.settlement.service.WeekCalculator;
import com.happymapleday.settlement.service.WeeklySettlementProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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
        // 정산 존재 확인
        WeeklySettlement settlement = weeklySettlementRepository.findById(settlementId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 정산입니다."));
        
        // 권한 확인
        if (!settlement.getUserId().equals(userId)) {
            throw new IllegalArgumentException("다른 사용자의 정산은 삭제할 수 없습니다.");
        }
        
        // 연관된 데이터들이 CASCADE로 자동 삭제됨
        weeklySettlementRepository.delete(settlement);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SettlementStatusResponse getSettlementStatus(Long userId, LocalDate weekStartDate) {
        // 사용자의 해당 주차 정산 찾기
        List<WeeklySettlement> settlements = weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId)
                .stream()
                .filter(s -> s.getWeekStartDate().equals(weekStartDate))
                .toList();
        
        if (settlements.isEmpty()) {
            return SettlementStatusResponse.builder()
                    .isFinalized(false)
                    .weekStartDate(weekStartDate)
                    .build();
        }
        
        WeeklySettlement settlement = settlements.get(0);
        return SettlementStatusResponse.from(settlement);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CurrentWeekStatusResponse getCurrentWeekStatus(Long userId) {
        List<WeeklySettlement> settlements = weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId);
        LocalDate currentWeekStart = weekCalculator.getWeekStartDate(LocalDate.now());
        
        // 현재 주차 완료 여부 확인
        boolean isCompleted = settlements.stream()
                .filter(s -> s.getWeekStartDate().equals(currentWeekStart))
                .anyMatch(WeeklySettlement::getIsFinalized);
        
        LocalDate nextWeekStart = currentWeekStart.plusWeeks(1);
        LocalDate today = LocalDate.now();
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
        // 사용자의 해당 주차 정산 찾기
        List<WeeklySettlement> settlements = weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId)
                .stream()
                .filter(s -> s.getWeekStartDate().equals(weekStartDate))
                .toList();
        
        if (settlements.isEmpty()) {
            return SettlementDetailResponse.builder()
                    .isFinalized(false)
                    .weekStartDate(weekStartDate)
                    .bossRecords(List.of())
                    .build();
        }
        
        WeeklySettlement settlement = settlements.get(0);
        
        // 보스 레코드 정보 가져오기
        List<WeeklyBossRecord> bossRecords = weeklyBossRecordRepository.findBySettlementIdOrderByCreatedAtAsc(settlement.getId());
        
        // 보스 레코드 상세 정보 생성
        List<BossRecordDetailResponse> bossDetails = bossRecords.stream()
                .map(BossRecordDetailResponse::from)
                .collect(Collectors.toList());
        
        return SettlementDetailResponse.from(settlement, bossDetails);
    }
    
    @Override
    public SettlementCompleteResponse upsertSettlement(Long userId, LocalDate weekStartDate, SettlementRequest request) {
        // 기존 정산 확인
        WeeklySettlement existingSettlement = weeklySettlementRepository
                .findByUserIdAndWorldNameAndWeekStartDate(userId, request.getWorldName(), weekStartDate)
                .orElse(null);
        
        if (existingSettlement != null) {
            // 기존 정산이 있으면 수정
            return settlementProcessor.updateSettlement(existingSettlement.getId(), userId, weekStartDate, request);
        } else {
            // 기존 정산이 없으면 새로 생성
            return settlementProcessor.createSettlement(userId, weekStartDate, request);
        }
    }
} 