package com.happymapleday.settlement.service.impl;

import com.happymapleday.common.client.BossServiceClient;
import com.happymapleday.common.client.CharacterServiceClient;
import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
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
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SettlementServiceImpl implements SettlementService {
    
    private final WeeklySettlementRepository weeklySettlementRepository;
    private final WeeklyBossRecordRepository weeklyBossRecordRepository;
    private final WeekCalculator weekCalculator;
    private final WeeklySettlementProcessor settlementProcessor;
    private final BossServiceClient bossServiceClient;
    private final CharacterServiceClient characterServiceClient;
    
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
        
        // 현재 주차 완료 여부 확인 (정산 존재 여부로 판단)
        Optional<WeeklySettlement> currentWeekSettlement = findSettlementByUserAndWeek(userId, currentWeekStart);
        boolean isCompleted = currentWeekSettlement.isPresent();
        
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
                    .weekStartDate(weekStartDate)
                    .bossRecords(List.of())
                    .build();
        }
        
        WeeklySettlement weeklySettlement = settlement.get();
        List<WeeklyBossRecord> bossRecords = weeklyBossRecordRepository
                .findWithDesireItemsBySettlementId(weeklySettlement.getId());
        // 외부 서비스에서 이름/난이도 조회 후 매핑
        Map<Long, String> characterIdToName = fetchCharacterNamesByIds(
                bossRecords.stream().map(WeeklyBossRecord::getCharacterId).distinct().collect(Collectors.toList())
        );
        Map<Long, BossResponse> bossIdToBoss = fetchBossByIds(
                bossRecords.stream().map(WeeklyBossRecord::getBossId).distinct().collect(Collectors.toList())
        );

        List<BossRecordDetailResponse> bossDetails = bossRecords.stream()
                .map(record -> {
                    String characterName = characterIdToName.getOrDefault(record.getCharacterId(), "-");
                    BossResponse boss = bossIdToBoss.get(record.getBossId());
                    String bossName = boss != null ? boss.getFullName() != null ? boss.getFullName() : boss.getBossName() : "-";
                    String difficulty = boss != null ? boss.getDifficulty() : null;
                    return BossRecordDetailResponse.from(record, characterName, bossName, difficulty);
                })
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

    @Override
    public SettlementCompleteResponse autoSaveSettlement(Long userId, LocalDate weekStartDate, SettlementRequest request) {
        Optional<WeeklySettlement> existingSettlement = weeklySettlementRepository
                .findByUserIdAndWorldNameAndWeekStartDate(userId, request.getWorldName(), weekStartDate);
        
        if (existingSettlement.isPresent()) {
            // 기존 정산이 있으면 수정 (PENDING 상태로)
            return settlementProcessor.updateSettlementPending(
                    existingSettlement.get().getId(), userId, weekStartDate, request);
        } else {
            // 기존 정산이 없으면 새로 생성 (PENDING 상태로)
            return settlementProcessor.createSettlementPending(userId, weekStartDate, request);
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
        List<WeeklySettlement> settlements = weeklySettlementRepository.findByUserIdAndWeekStartDate(userId, weekStartDate);
        return settlements.isEmpty() ? Optional.empty() : Optional.of(settlements.get(0));
    }

    private Map<Long, String> fetchCharacterNamesByIds(List<Long> characterIds) {
        if (characterIds == null || characterIds.isEmpty()) {
            return Map.of();
        }
        return characterIds.stream().collect(Collectors.toMap(Function.identity(), id -> {
            try {
                ApiResponse<Object> resp = characterServiceClient.getCharacterDetails(id);
                if (resp == null || resp.getData() == null) {
                    return "-";
                }
                Object data = resp.getData();
                if (data instanceof java.util.Map<?, ?> map) {
                    Object name = map.get("characterName");
                    return name != null ? String.valueOf(name) : "-";
                }
                return "-";
            } catch (Exception e) {
                return "-";
            }
        }));
    }

    private Map<Long, BossResponse> fetchBossByIds(List<Long> bossIds) {
        if (bossIds == null || bossIds.isEmpty()) {
            return Map.of();
        }
        ApiResponse<List<BossResponse>> resp = bossServiceClient.getBossesByIds(bossIds);
        List<BossResponse> data = resp != null ? resp.getData() : List.of();
        if (data == null) data = List.of();
        return data.stream().collect(Collectors.toMap(BossResponse::getBossId, Function.identity(), (a,b) -> a));
    }
} 