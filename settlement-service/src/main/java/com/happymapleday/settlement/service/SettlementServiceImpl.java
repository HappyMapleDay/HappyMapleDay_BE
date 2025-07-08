package com.happymapleday.settlement.service;

import com.happymapleday.settlement.dto.request.BossRecordModifyRequest;
import com.happymapleday.settlement.dto.request.BossRecordRequest;
import com.happymapleday.settlement.dto.request.DesireItemModifyRequest;
import com.happymapleday.settlement.dto.request.DesireItemRequest;
import com.happymapleday.settlement.dto.request.SettlementCompleteRequest;
import com.happymapleday.settlement.dto.request.SettlementModifyRequest;
import com.happymapleday.settlement.dto.response.BossRecordDetailResponse;
import com.happymapleday.settlement.dto.response.CurrentWeekStatusResponse;
import com.happymapleday.settlement.dto.response.SettlementCompleteResponse;
import com.happymapleday.settlement.dto.response.SettlementModifyResponse;
import com.happymapleday.settlement.dto.response.SettlementStatusResponse;
import com.happymapleday.settlement.entity.DesireItemRecord;
import com.happymapleday.settlement.entity.WeeklyBossRecord;
import com.happymapleday.settlement.entity.WeeklySettlement;
import com.happymapleday.settlement.repository.DesireItemRecordRepository;
import com.happymapleday.settlement.repository.WeeklyBossRecordRepository;
import com.happymapleday.settlement.repository.WeeklySettlementRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SettlementServiceImpl implements SettlementService {
    
    private final WeeklySettlementRepository weeklySettlementRepository;
    private final WeeklyBossRecordRepository weeklyBossRecordRepository;
    private final DesireItemRecordRepository desireItemRecordRepository;
    
    public SettlementServiceImpl(WeeklySettlementRepository weeklySettlementRepository,
                                WeeklyBossRecordRepository weeklyBossRecordRepository,
                                DesireItemRecordRepository desireItemRecordRepository) {
        this.weeklySettlementRepository = weeklySettlementRepository;
        this.weeklyBossRecordRepository = weeklyBossRecordRepository;
        this.desireItemRecordRepository = desireItemRecordRepository;
    }
    
    @Override
    public SettlementCompleteResponse completeSettlement(SettlementCompleteRequest request) {
        // 중복 정산 확인
        boolean alreadyExists = weeklySettlementRepository.existsByUserIdAndWorldNameAndWeekStartDateAndIsFinalizedTrue(
                request.getUserId(), request.getWorldName(), request.getWeekStartDate());
        
        if (alreadyExists) {
            throw new IllegalStateException("이미 이번 주 정산이 완료되었습니다.");
        }
        
        // 주간 정산 메타데이터 생성
        WeeklySettlement settlement = new WeeklySettlement(
                request.getUserId(), request.getWorldName(), request.getWeekStartDate());
        settlement = weeklySettlementRepository.save(settlement);
        
        // 보스 기록들을 저장
        List<WeeklyBossRecord> bossRecords = new ArrayList<>();
        for (BossRecordRequest bossRequest : request.getBossRecords()) {
            // 중복 보스 기록 확인
            boolean bossRecordExists = weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                    bossRequest.getCharacterId(), bossRequest.getBossId(), request.getWeekStartDate());
            
            if (bossRecordExists) {
                throw new IllegalArgumentException(
                        String.format("캐릭터 %d의 보스 %d는 이미 이번 주에 완료된 기록이 있습니다.", 
                                bossRequest.getCharacterId(), bossRequest.getBossId()));
            }
            
            // 보스 기록 생성
            WeeklyBossRecord bossRecord = new WeeklyBossRecord(
                    settlement.getId(),
                    request.getUserId(),
                    bossRequest.getCharacterId(),
                    bossRequest.getBossId(),
                    request.getWeekStartDate(),
                    bossRequest.getCrystalIncome(),
                    bossRequest.getPartySize()
            );
            bossRecord = weeklyBossRecordRepository.save(bossRecord);
            
            // 물욕템 기록들을 저장
            if (bossRequest.getDesireItems() != null && !bossRequest.getDesireItems().isEmpty()) {
                List<DesireItemRecord> desireItemRecords = new ArrayList<>();
                BigInteger totalDesireItemIncome = BigInteger.ZERO;
                
                for (DesireItemRequest desireRequest : bossRequest.getDesireItems()) {
                    DesireItemRecord desireRecord = new DesireItemRecord(
                            bossRecord.getId(),
                            desireRequest.getDesireItemId(),
                            desireRequest.getSalePrice()
                    );
                    desireItemRecords.add(desireRecord);
                    totalDesireItemIncome = totalDesireItemIncome.add(desireRequest.getSalePrice());
                }
                
                desireItemRecordRepository.saveAll(desireItemRecords);
                bossRecord.setDesireItemIncome(totalDesireItemIncome);
                bossRecord.calculateTotalIncome();
                weeklyBossRecordRepository.save(bossRecord);
            }
            
            bossRecords.add(bossRecord);
        }
        
        // 정산 총계 계산 및 저장
        settlement.setBossRecords(bossRecords);
        settlement.calculateTotals();
        settlement.setFinalizedAt(LocalDateTime.now());
        weeklySettlementRepository.save(settlement);
        
        return new SettlementCompleteResponse(
                settlement.getId(),
                settlement.getWeekStartDate(),
                settlement.getTotalCrystalIncome(),
                settlement.getTotalDesireItemIncome(),
                settlement.getTotalIncome(),
                settlement.getTotalBossCount(),
                settlement.getCharacterCount(),
                settlement.getFinalizedAt()
        );
    }
    
    @Override
    public SettlementModifyResponse modifySettlement(Long settlementId, SettlementModifyRequest request) {
        // 정산 존재 확인
        WeeklySettlement settlement = weeklySettlementRepository.findById(settlementId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 정산입니다."));
        
        for (BossRecordModifyRequest modifyRequest : request.getBossRecords()) {
            // 보스 기록 존재 확인
            WeeklyBossRecord bossRecord = weeklyBossRecordRepository.findById(modifyRequest.getWeeklyBossRecordId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스 기록입니다."));
            
            // 권한 확인 (같은 정산에 속한 기록인지)
            if (!bossRecord.getSettlementId().equals(settlementId)) {
                throw new IllegalArgumentException("해당 보스 기록은 이 정산에 속하지 않습니다.");
            }
            
            // 보스 기록 수정
            bossRecord.setCharacterId(modifyRequest.getCharacterId());
            bossRecord.setBossId(modifyRequest.getBossId());
            bossRecord.setPartySize(modifyRequest.getPartySize());
            bossRecord.setCrystalIncome(modifyRequest.getCrystalIncome());
            
            // 기존 물욕템 기록 삭제
            desireItemRecordRepository.deleteByWeeklyBossRecordId(bossRecord.getId());
            
            // 새로운 물욕템 기록 추가
            if (modifyRequest.getDesireItems() != null && !modifyRequest.getDesireItems().isEmpty()) {
                List<DesireItemRecord> newDesireItemRecords = new ArrayList<>();
                BigInteger totalDesireItemIncome = BigInteger.ZERO;
                
                for (DesireItemModifyRequest desireRequest : modifyRequest.getDesireItems()) {
                    DesireItemRecord desireRecord = new DesireItemRecord(
                            bossRecord.getId(),
                            desireRequest.getDesireItemId(),
                            desireRequest.getSalePrice()
                    );
                    newDesireItemRecords.add(desireRecord);
                    totalDesireItemIncome = totalDesireItemIncome.add(desireRequest.getSalePrice());
                }
                
                desireItemRecordRepository.saveAll(newDesireItemRecords);
                bossRecord.setDesireItemIncome(totalDesireItemIncome);
            } else {
                bossRecord.setDesireItemIncome(BigInteger.ZERO);
            }
            
            bossRecord.calculateTotalIncome();
            weeklyBossRecordRepository.save(bossRecord);
        }
        
        // 정산 총계 재계산
        List<WeeklyBossRecord> allBossRecords = weeklyBossRecordRepository.findBySettlementIdOrderByCreatedAtAsc(settlementId);
        settlement.setBossRecords(allBossRecords);
        settlement.calculateTotals();
        weeklySettlementRepository.save(settlement);
        
        return new SettlementModifyResponse(
                settlement.getId(),
                settlement.getTotalIncome(),
                settlement.getTotalDesireItemIncome(),
                LocalDateTime.now()
        );
    }
    
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
        // 사용자의 모든 정산에서 해당 주차 찾기
        List<WeeklySettlement> settlements = weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId)
                .stream()
                .filter(s -> s.getWeekStartDate().equals(weekStartDate))
                .toList();
        
        if (settlements.isEmpty()) {
            return new SettlementStatusResponse(false, weekStartDate, "아직 이번 주 보돌을 완료하지 않았습니다.");
        }
        
        WeeklySettlement settlement = settlements.get(0);
        List<WeeklyBossRecord> bossRecords = weeklyBossRecordRepository.findBySettlementIdOrderByCreatedAtAsc(settlement.getId());
        
        // Boss Service와 Character Service 호출하여 상세 정보 가져와야 함
        List<BossRecordDetailResponse> bossDetails = new ArrayList<>();
        for (WeeklyBossRecord record : bossRecords) {
            // 임시로 하드코딩된 값 사용 (실제로는 다른 서비스에서 가져와야 함)
            BossRecordDetailResponse detail = new BossRecordDetailResponse(
                    "캐릭터" + record.getCharacterId(),
                    "보스" + record.getBossId(),
                    "하드",
                    record.getPartySize(),
                    record.getCrystalIncome(),
                    record.getDesireItemIncome(),
                    record.getTotalIncome()
            );
            bossDetails.add(detail);
        }
        
        return new SettlementStatusResponse(
                true,
                settlement.getId(),
                settlement.getWeekStartDate(),
                settlement.getFinalizedAt(),
                settlement.getTotalIncome(),
                bossDetails
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public CurrentWeekStatusResponse getCurrentWeekStatus(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDate currentWeekStart = getWeekStartDate(now);
        LocalDate nextWeekStart = currentWeekStart.plusWeeks(1);
        
        // 이번 주 정산 완료 여부 확인 (모든 월드 기준으로 확인)
        List<WeeklySettlement> thisWeekSettlements = weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId)
                .stream()
                .filter(s -> s.getWeekStartDate().equals(currentWeekStart))
                .toList();
        boolean isCompleted = !thisWeekSettlements.isEmpty() && 
                             thisWeekSettlements.stream().anyMatch(s -> s.getIsFinalized());
        
        // 다음 리셋까지 남은 일수 계산
        long remainingDays = ChronoUnit.DAYS.between(now, nextWeekStart);
        LocalDateTime nextResetDate = nextWeekStart.atStartOfDay();
        
        return new CurrentWeekStatusResponse(
                currentWeekStart,
                nextWeekStart,
                isCompleted,
                (int) remainingDays,
                nextResetDate
        );
    }
    
    // 목요일을 기준으로 주차 시작일 계산
    private LocalDate getWeekStartDate(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue(); // 월=1, 화=2, 수=3, 목=4, 금=5, 토=6, 일=7
        int daysFromThursday = (dayOfWeek + 3) % 7; // 목요일을 0으로 만들기 위한 계산
        return date.minusDays(daysFromThursday);
    }
    

} 