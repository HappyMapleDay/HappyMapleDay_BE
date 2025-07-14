package com.happymapleday.settlement.service.impl;

import com.happymapleday.settlement.dto.request.BossRecordRequest;
import com.happymapleday.settlement.dto.request.DesireItemRequest;
import com.happymapleday.settlement.dto.request.SettlementRequest;
import com.happymapleday.settlement.dto.response.BossRecordDetailResponse;
import com.happymapleday.settlement.dto.response.CurrentWeekStatusResponse;
import com.happymapleday.settlement.dto.response.SettlementCompleteResponse;
import com.happymapleday.settlement.dto.response.SettlementStatusResponse;
import com.happymapleday.settlement.entity.DesireItemRecord;
import com.happymapleday.settlement.entity.WeeklyBossRecord;
import com.happymapleday.settlement.entity.WeeklySettlement;
import com.happymapleday.settlement.repository.DesireItemRecordRepository;
import com.happymapleday.settlement.repository.WeeklyBossRecordRepository;
import com.happymapleday.settlement.repository.WeeklySettlementRepository;

import com.happymapleday.settlement.service.SettlementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            return SettlementStatusResponse.builder()
                    .isFinalized(false)
                    .weekStartDate(weekStartDate)
                    .build();
        }
        
        WeeklySettlement settlement = settlements.get(0);
        List<WeeklyBossRecord> bossRecords = weeklyBossRecordRepository.findBySettlementIdOrderByCreatedAtAsc(settlement.getId());
        
        // Boss Service와 Character Service 호출하여 상세 정보 가져와야 함
        List<BossRecordDetailResponse> bossDetails = new ArrayList<>();
        for (WeeklyBossRecord record : bossRecords) {
            // 임시로 하드코딩된 값 사용 (실제로는 다른 서비스에서 가져와야 함)
            BossRecordDetailResponse detail = BossRecordDetailResponse.builder()
                    .characterName("캐릭터" + record.getCharacterId())
                    .bossName("보스" + record.getBossId())
                    .difficulty("하드")
                    .partySize(record.getPartySize())
                    .crystalIncome(record.getCrystalIncome())
                    .desireItemIncome(record.getDesireItemIncome())
                    .totalIncome(record.getTotalIncome())
                    .build();
            bossDetails.add(detail);
        }
        
        return SettlementStatusResponse.builder()
                .isFinalized(true)
                .settlementId(settlement.getId())
                .userId(settlement.getUserId())
                .worldName(settlement.getWorldName())
                .weekStartDate(settlement.getWeekStartDate())
                .totalCrystalIncome(settlement.getTotalCrystalIncome())
                .totalDesireItemIncome(settlement.getTotalDesireItemIncome())
                .totalIncome(settlement.getTotalIncome())
                .totalBossCount(settlement.getTotalBossCount())
                .characterCount(settlement.getCharacterCount())
                .characterCrystalCounts(settlement.getCharacterCrystalCounts())
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public CurrentWeekStatusResponse getCurrentWeekStatus(Long userId) {
        List<WeeklySettlement> settlements = weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId);
        LocalDate currentWeekStart = getWeekStartDate(LocalDate.now());
        
        boolean isCompleted = settlements.stream()
                .filter(s -> s.getWeekStartDate().equals(currentWeekStart))
                .anyMatch(WeeklySettlement::getIsFinalized);
        
        LocalDate nextWeekStart = currentWeekStart.plusWeeks(1);
        
        // 다음 리셋 날짜 계산 (목요일 기준)
        LocalDate today = LocalDate.now();
        LocalDate nextResetDate;
        
        if (today.isBefore(currentWeekStart)) {
            // 현재 날짜가 이번 주 목요일 이전이면 → 이번 주 목요일
            nextResetDate = currentWeekStart;
        } else {
            // 현재 날짜가 이번 주 목요일 이후이거나 같으면 → 다음 주 목요일
            nextResetDate = nextWeekStart;
        }
        
        long remainingDays = ChronoUnit.DAYS.between(today, nextResetDate);
        
        return CurrentWeekStatusResponse.builder()
                .isCompleted(isCompleted)
                .currentWeekStart(currentWeekStart)
                .nextWeekStart(nextWeekStart)
                .remainingDays((int) remainingDays)
                .nextResetDate(nextResetDate.atStartOfDay())
                .build();
    }
    
    @Override
    public SettlementCompleteResponse upsertSettlement(Long userId, LocalDate weekStartDate, SettlementRequest request) {
        // 기존 정산 확인
        WeeklySettlement existingSettlement = weeklySettlementRepository
                .findByUserIdAndWorldNameAndWeekStartDate(userId, request.getWorldName(), weekStartDate)
                .orElse(null);
        
        if (existingSettlement != null) {
            // 기존 정산이 있으면 수정
            return modifyExistingSettlement(existingSettlement.getId(), userId, weekStartDate, request);
        } else {
            // 기존 정산이 없으면 새로 생성
            return createNewSettlement(userId, weekStartDate, request);
        }
    }
    
    private SettlementCompleteResponse createNewSettlement(Long userId, LocalDate weekStartDate, SettlementRequest request) {
        // 보스 기록들을 먼저 생성하여 결정석 제한 검증
        List<WeeklyBossRecord> bossRecords = new ArrayList<>();
        for (BossRecordRequest bossRequest : request.getBossRecords()) {
            // 중복 보스 기록 확인
            boolean bossRecordExists = weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                    bossRequest.getCharacterId(), bossRequest.getBossId(), weekStartDate);
            
            if (bossRecordExists) {
                throw new IllegalArgumentException(
                        String.format("캐릭터 %d의 보스 %d는 이미 이번 주에 완료된 기록이 있습니다.", 
                                bossRequest.getCharacterId(), bossRequest.getBossId()));
            }
            
            // 보스 기록 생성 (임시로 settlementId 없이)
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
        
        // 결정석 제한 검증
        Set<Long> characterIds = bossRecords.stream()
                .map(WeeklyBossRecord::getCharacterId)
                .collect(Collectors.toSet());
        
        for (Long characterId : characterIds) {
            if (WeeklyBossRecord.isCharacterOverCrystalLimit(bossRecords, characterId)) {
                throw new IllegalStateException("캐릭터당 주간 결정석 판매 제한을 초과했습니다.");
            }
        }
        
        if (WeeklyBossRecord.isWorldOverCrystalLimit(bossRecords)) {
            throw new IllegalStateException("월드 전체 주간 결정석 판매 제한을 초과했습니다.");
        }
        
        // 주간 정산 메타데이터 생성
        WeeklySettlement settlement = WeeklySettlement.builder()
                .userId(userId)
                .worldName(request.getWorldName())
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(bossRecords.stream()
                        .map(WeeklyBossRecord::getCrystalIncome)
                        .reduce(BigInteger.ZERO, BigInteger::add))
                .totalDesireItemIncome(BigInteger.ZERO)
                .totalIncome(bossRecords.stream()
                        .map(WeeklyBossRecord::getCrystalIncome)
                        .reduce(BigInteger.ZERO, BigInteger::add))
                .totalBossCount((int) bossRecords.size())
                .characterCount((int) bossRecords.stream()
                        .map(WeeklyBossRecord::getCharacterId)
                        .distinct()
                        .count())
                .isFinalized(true)
                .finalizedAt(LocalDateTime.now())
                .build();
        settlement = weeklySettlementRepository.save(settlement);
        
        // 보스 기록들을 저장하고 물욕템 기록들을 처리
        List<WeeklyBossRecord> finalBossRecords = new ArrayList<>();
        for (int i = 0; i < bossRecords.size(); i++) {
            WeeklyBossRecord bossRecord = bossRecords.get(i);
            BossRecordRequest bossRequest = request.getBossRecords().get(i);
            
            // settlementId 설정 후 저장
            bossRecord = WeeklyBossRecord.builder()
                    .settlementId(settlement.getId())
                    .userId(bossRecord.getUserId())
                    .characterId(bossRecord.getCharacterId())
                    .bossId(bossRecord.getBossId())
                    .weekStartDate(bossRecord.getWeekStartDate())
                    .crystalIncome(bossRecord.getCrystalIncome())
                    .partySize(bossRecord.getPartySize())
                    .desireItemIncome(BigInteger.ZERO)
                    .totalIncome(bossRecord.getCrystalIncome())
                    .build();
            
            // 물욕템 기록들을 저장
            if (bossRequest.getDesireItems() != null && !bossRequest.getDesireItems().isEmpty()) {
                bossRecord = weeklyBossRecordRepository.save(bossRecord);
                
                BigInteger totalDesireItemIncome = BigInteger.ZERO;
                for (DesireItemRequest desireRequest : bossRequest.getDesireItems()) {
                    DesireItemRecord desireRecord = DesireItemRecord.builder()
                            .weeklyBossRecordId(bossRecord.getId())
                            .desireItemId(desireRequest.getDesireItemId())
                            .salePrice(desireRequest.getSalePrice())
                            .build();
                    desireItemRecordRepository.save(desireRecord);
                    totalDesireItemIncome = totalDesireItemIncome.add(desireRequest.getSalePrice());
                }
                
                // 물욕템 수입 포함하여 보스 기록 업데이트
                bossRecord = WeeklyBossRecord.builder()
                        .settlementId(bossRecord.getSettlementId())
                        .userId(bossRecord.getUserId())
                        .characterId(bossRecord.getCharacterId())
                        .bossId(bossRecord.getBossId())
                        .weekStartDate(bossRecord.getWeekStartDate())
                        .crystalIncome(bossRecord.getCrystalIncome())
                        .partySize(bossRecord.getPartySize())
                        .desireItemIncome(totalDesireItemIncome)
                        .totalIncome(bossRecord.getCrystalIncome().add(totalDesireItemIncome))
                        .build();
            }
            
            bossRecord = weeklyBossRecordRepository.save(bossRecord);
            finalBossRecords.add(bossRecord);
        }
        
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
    
    private SettlementCompleteResponse modifyExistingSettlement(Long settlementId, Long userId, LocalDate weekStartDate, SettlementRequest request) {
        // 기존 정산 데이터 전체 삭제
        List<WeeklyBossRecord> existingBossRecords = weeklyBossRecordRepository.findBySettlementIdOrderByCreatedAtAsc(settlementId);
        for (WeeklyBossRecord bossRecord : existingBossRecords) {
            desireItemRecordRepository.deleteByWeeklyBossRecordId(bossRecord.getId());
        }
        weeklyBossRecordRepository.deleteBySettlementId(settlementId);
        
        // 새로운 보스 기록들을 먼저 생성하여 결정석 제한 검증
        List<WeeklyBossRecord> newBossRecords = new ArrayList<>();
        for (BossRecordRequest bossRequest : request.getBossRecords()) {
            // 중복 보스 기록 확인 (같은 주차에 다른 정산으로 기록된 경우)
            boolean bossRecordExists = weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                    bossRequest.getCharacterId(), bossRequest.getBossId(), weekStartDate);
            
            if (bossRecordExists) {
                throw new IllegalArgumentException(
                        String.format("캐릭터 %d의 보스 %d는 이미 이번 주에 완료된 기록이 있습니다.", 
                                bossRequest.getCharacterId(), bossRequest.getBossId()));
            }
            
            // 보스 기록 생성 (임시로 settlementId 포함)
            WeeklyBossRecord bossRecord = WeeklyBossRecord.builder()
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
            
            newBossRecords.add(bossRecord);
        }
        
        // 결정석 제한 검증
        Set<Long> characterIds = newBossRecords.stream()
                .map(WeeklyBossRecord::getCharacterId)
                .collect(Collectors.toSet());
        
        for (Long characterId : characterIds) {
            if (WeeklyBossRecord.isCharacterOverCrystalLimit(newBossRecords, characterId)) {
                throw new IllegalStateException("캐릭터당 주간 결정석 판매 제한을 초과했습니다.");
            }
        }
        
        if (WeeklyBossRecord.isWorldOverCrystalLimit(newBossRecords)) {
            throw new IllegalStateException("월드 전체 주간 결정석 판매 제한을 초과했습니다.");
        }
        
        // 새로운 보스 기록들 저장 및 물욕템 기록 처리
        List<WeeklyBossRecord> finalBossRecords = new ArrayList<>();
        for (int i = 0; i < newBossRecords.size(); i++) {
            WeeklyBossRecord bossRecord = newBossRecords.get(i);
            BossRecordRequest bossRequest = request.getBossRecords().get(i);
            
            // 보스 기록 저장
            bossRecord = weeklyBossRecordRepository.save(bossRecord);
            
            // 물욕템 기록들 저장
            BigInteger totalDesireItemIncome = BigInteger.ZERO;
            if (bossRequest.getDesireItems() != null && !bossRequest.getDesireItems().isEmpty()) {
                for (DesireItemRequest desireRequest : bossRequest.getDesireItems()) {
                    DesireItemRecord desireRecord = DesireItemRecord.builder()
                            .weeklyBossRecordId(bossRecord.getId())
                            .desireItemId(desireRequest.getDesireItemId())
                            .salePrice(desireRequest.getSalePrice())
                            .build();
                    desireItemRecordRepository.save(desireRecord);
                    totalDesireItemIncome = totalDesireItemIncome.add(desireRequest.getSalePrice());
                }
                
                // 물욕템 수입 포함하여 보스 기록 업데이트
                bossRecord = WeeklyBossRecord.builder()
                        .settlementId(bossRecord.getSettlementId())
                        .userId(bossRecord.getUserId())
                        .characterId(bossRecord.getCharacterId())
                        .bossId(bossRecord.getBossId())
                        .weekStartDate(bossRecord.getWeekStartDate())
                        .crystalIncome(bossRecord.getCrystalIncome())
                        .partySize(bossRecord.getPartySize())
                        .desireItemIncome(totalDesireItemIncome)
                        .totalIncome(bossRecord.getCrystalIncome().add(totalDesireItemIncome))
                        .build();
                bossRecord = weeklyBossRecordRepository.save(bossRecord);
            }
            
            finalBossRecords.add(bossRecord);
        }
        
        // 정산 메타데이터 업데이트
        WeeklySettlement updatedSettlement = WeeklySettlement.builder()
                .userId(userId)
                .worldName(request.getWorldName())
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(finalBossRecords.stream()
                        .map(WeeklyBossRecord::getCrystalIncome)
                        .reduce(BigInteger.ZERO, BigInteger::add))
                .totalDesireItemIncome(finalBossRecords.stream()
                        .map(WeeklyBossRecord::getDesireItemIncome)
                        .reduce(BigInteger.ZERO, BigInteger::add))
                .totalIncome(finalBossRecords.stream()
                        .map(WeeklyBossRecord::getTotalIncome)
                        .reduce(BigInteger.ZERO, BigInteger::add))
                .totalBossCount(finalBossRecords.size())
                .characterCount((int) finalBossRecords.stream()
                        .map(WeeklyBossRecord::getCharacterId)
                        .distinct()
                        .count())
                .isFinalized(true)
                .finalizedAt(LocalDateTime.now())
                .build();
        weeklySettlementRepository.save(updatedSettlement);
        
        return SettlementCompleteResponse.builder()
                .settlementId(settlementId)
                .weekStartDate(updatedSettlement.getWeekStartDate())
                .totalCrystalIncome(updatedSettlement.getTotalCrystalIncome())
                .totalDesireItemIncome(updatedSettlement.getTotalDesireItemIncome())
                .totalIncome(updatedSettlement.getTotalIncome())
                .totalBossCount(updatedSettlement.getTotalBossCount())
                .characterCount(updatedSettlement.getCharacterCount())
                .finalizedAt(updatedSettlement.getFinalizedAt())
                .build();
    }
    
    // 목요일을 기준으로 주차 시작일 계산
    private LocalDate getWeekStartDate(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue(); // 월=1, 화=2, 수=3, 목=4, 금=5, 토=6, 일=7
        int daysFromThursday = (dayOfWeek + 3) % 7; // 목요일을 0으로 만들기 위한 계산
        return date.minusDays(daysFromThursday);
    }
    

} 