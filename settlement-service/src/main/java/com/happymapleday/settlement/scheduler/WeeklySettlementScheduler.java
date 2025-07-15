package com.happymapleday.settlement.scheduler;

import com.happymapleday.settlement.entity.SettlementStatus;
import com.happymapleday.settlement.entity.WeeklySettlement;
import com.happymapleday.settlement.repository.WeeklySettlementRepository;
import com.happymapleday.settlement.service.util.WeekCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeeklySettlementScheduler {
    
    private final WeeklySettlementRepository weeklySettlementRepository;
    private final WeekCalculator weekCalculator;
    
    @Scheduled(cron = "${scheduling.weekly-settlement.cron}")
    @Transactional
    public void processWeeklyAutoSettlement() {
        log.info("주간 자동 정산 시작");
        
        LocalDate today = LocalDate.now();
        LocalDate previousWeekStartDate = weekCalculator.getWeekStartDate(today).minusWeeks(1);
        
        try {
            int processedCount = processAutoSettlementForPreviousWeek(previousWeekStartDate);
            log.info("주간 자동 정산 완료: {}, 처리된 정산 수: {}", previousWeekStartDate, processedCount);
        } catch (Exception e) {
            log.error("주간 자동 정산 중 오류 발생: {}", e.getMessage(), e);
        }
    }
    
    private int processAutoSettlementForPreviousWeek(LocalDate weekStartDate) {
        log.info("자동 정산 대상 조회 시작: {}", weekStartDate);
        
        List<WeeklySettlement> pendingSettlements = weeklySettlementRepository
                .findByWeekStartDateAndStatus(weekStartDate, SettlementStatus.PENDING);
        
        log.info("자동 정산 대상 수: {}", pendingSettlements.size());
        
        for (WeeklySettlement settlement : pendingSettlements) {
            try {
                completePendingSettlement(settlement);
                log.debug("자동 정산 완료: 사용자 {}, 정산 ID {}", settlement.getUserId(), settlement.getId());
            } catch (Exception e) {
                log.error("자동 정산 실패: 사용자 {}, 정산 ID {}, 오류: {}", 
                        settlement.getUserId(), settlement.getId(), e.getMessage());
            }
        }
        
        return pendingSettlements.size();
    }
    
    private void completePendingSettlement(WeeklySettlement settlement) {
        WeeklySettlement updatedSettlement = WeeklySettlement.builder()
                .id(settlement.getId())
                .userId(settlement.getUserId())
                .worldName(settlement.getWorldName())
                .weekStartDate(settlement.getWeekStartDate())
                .totalCrystalIncome(settlement.getTotalCrystalIncome())
                .totalDesireItemIncome(settlement.getTotalDesireItemIncome())
                .totalIncome(settlement.getTotalIncome())
                .totalBossCount(settlement.getTotalBossCount())
                .characterCount(settlement.getCharacterCount())
                .status(SettlementStatus.COMPLETED)
                .bossRecords(settlement.getBossRecords())
                .build();
                
        weeklySettlementRepository.save(updatedSettlement);
    }
} 