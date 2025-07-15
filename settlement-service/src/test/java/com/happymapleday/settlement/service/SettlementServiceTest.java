package com.happymapleday.settlement.service;

import com.happymapleday.settlement.dto.request.*;
import com.happymapleday.settlement.dto.response.*;
import com.happymapleday.settlement.entity.*;
import com.happymapleday.settlement.repository.*;
import com.happymapleday.settlement.service.impl.SettlementServiceImpl;
import com.happymapleday.settlement.service.util.WeekCalculator;
import com.happymapleday.settlement.service.processor.WeeklySettlementProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @InjectMocks
    private SettlementServiceImpl settlementService;

    @Mock
    private WeeklySettlementRepository weeklySettlementRepository;

    @Mock
    private WeeklyBossRecordRepository weeklyBossRecordRepository;

    @Mock
    private WeekCalculator weekCalculator;

    @Mock
    private WeeklySettlementProcessor settlementProcessor;

    private LocalDate weekStartDate;
    private Long userId;
    private String worldName;
    private Long characterId;

    @BeforeEach
    void setUp() {
        weekStartDate = LocalDate.of(2024, 1, 4); // 2024년 1월 4일 목요일
        userId = 1L;
        worldName = "크로아";
        characterId = 1L;
    }

    @Test
    @DisplayName("정산 upsert - 새로운 정산 생성 성공")
    void upsertSettlement_CreateNew_Success() {
        // given
        SettlementRequest request = createSettlementRequest();
        SettlementCompleteResponse expectedResponse = createSettlementCompleteResponse();
        
        given(weeklySettlementRepository.findByUserIdAndWorldNameAndWeekStartDate(
                userId, worldName, weekStartDate)).willReturn(Optional.empty());
        given(settlementProcessor.createSettlement(userId, weekStartDate, request))
                .willReturn(expectedResponse);
        
        // when
        SettlementCompleteResponse response = settlementService.upsertSettlement(userId, weekStartDate, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSettlementId()).isEqualTo(expectedResponse.getSettlementId());
        assertThat(response.getTotalCrystalIncome()).isEqualTo(expectedResponse.getTotalCrystalIncome());
        verify(settlementProcessor, times(1)).createSettlement(userId, weekStartDate, request);
    }

    @Test
    @DisplayName("정산 upsert - 기존 정산 수정 성공")
    void upsertSettlement_ModifyExisting_Success() {
        // given
        SettlementRequest request = createSettlementRequest();
        WeeklySettlement existingSettlement = createWeeklySettlement();
        SettlementCompleteResponse expectedResponse = createSettlementCompleteResponse();
        
        given(weeklySettlementRepository.findByUserIdAndWorldNameAndWeekStartDate(
                userId, worldName, weekStartDate)).willReturn(Optional.of(existingSettlement));
        given(settlementProcessor.updateSettlement(existingSettlement.getId(), userId, weekStartDate, request))
                .willReturn(expectedResponse);
        
        // when
        SettlementCompleteResponse response = settlementService.upsertSettlement(userId, weekStartDate, request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getSettlementId()).isEqualTo(expectedResponse.getSettlementId());
        verify(settlementProcessor, times(1)).updateSettlement(existingSettlement.getId(), userId, weekStartDate, request);
    }

    @Test
    @DisplayName("정산 삭제 - 성공")
    void deleteSettlement_Success() {
        // given
        Long settlementId = 1L;
        WeeklySettlement settlement = createWeeklySettlement();
        
        given(weeklySettlementRepository.findById(settlementId)).willReturn(Optional.of(settlement));
        
        // when
        settlementService.deleteSettlement(settlementId, userId);
        
        // then
        verify(weeklySettlementRepository, times(1)).delete(settlement);
    }

    @Test
    @DisplayName("정산 삭제 - 존재하지 않는 정산")
    void deleteSettlement_NotFound() {
        // given
        Long settlementId = 999L;
        
        given(weeklySettlementRepository.findById(settlementId)).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> settlementService.deleteSettlement(settlementId, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 정산입니다.");
    }

    @Test
    @DisplayName("정산 삭제 - 다른 사용자 정산")
    void deleteSettlement_UnauthorizedUser() {
        // given
        Long settlementId = 1L;
        Long otherUserId = 2L;
        WeeklySettlement settlement = createWeeklySettlement();
        
        given(weeklySettlementRepository.findById(settlementId)).willReturn(Optional.of(settlement));
        
        // when & then
        assertThatThrownBy(() -> settlementService.deleteSettlement(settlementId, otherUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("다른 사용자의 정산은 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("현재 주차 상태 조회 - 성공")
    void getCurrentWeekStatus_Success() {
        // given
        LocalDate today = LocalDate.now();
        LocalDate currentWeekStart = LocalDate.of(2024, 1, 4);
        LocalDate nextWeekStart = currentWeekStart.plusWeeks(1);
        LocalDate nextResetDate = nextWeekStart;
        int remainingDays = 3;
        
        WeeklySettlement settlement = createWeeklySettlement();
        
        given(weekCalculator.getWeekStartDate(today)).willReturn(currentWeekStart);
        given(weekCalculator.getNextResetDate(today)).willReturn(nextResetDate);
        given(weekCalculator.getRemainingDays(today)).willReturn(remainingDays);
        given(weeklySettlementRepository.findByUserIdAndWeekStartDate(userId, currentWeekStart))
                .willReturn(List.of(settlement));
        
        // when
        CurrentWeekStatusResponse response = settlementService.getCurrentWeekStatus(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCurrentWeekStart()).isEqualTo(currentWeekStart);
        assertThat(response.getNextWeekStart()).isEqualTo(nextWeekStart);
        assertThat(response.getIsCompleted()).isTrue();
        assertThat(response.getRemainingDays()).isEqualTo(remainingDays);
        assertThat(response.getNextResetDate()).isEqualTo(nextResetDate.atStartOfDay());
    }

    @Test
    @DisplayName("현재 주차 상태 조회 - 미완료")
    void getCurrentWeekStatus_NotCompleted() {
        // given
        LocalDate today = LocalDate.now();
        LocalDate currentWeekStart = LocalDate.of(2024, 1, 4);
        LocalDate nextWeekStart = currentWeekStart.plusWeeks(1);
        LocalDate nextResetDate = nextWeekStart;
        int remainingDays = 3;
        
        given(weekCalculator.getWeekStartDate(today)).willReturn(currentWeekStart);
        given(weekCalculator.getNextResetDate(today)).willReturn(nextResetDate);
        given(weekCalculator.getRemainingDays(today)).willReturn(remainingDays);
        given(weeklySettlementRepository.findByUserIdAndWeekStartDate(userId, currentWeekStart))
                .willReturn(List.of());
        
        // when
        CurrentWeekStatusResponse response = settlementService.getCurrentWeekStatus(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCurrentWeekStart()).isEqualTo(currentWeekStart);
        assertThat(response.getNextWeekStart()).isEqualTo(nextWeekStart);
        assertThat(response.getIsCompleted()).isFalse();
        assertThat(response.getRemainingDays()).isEqualTo(remainingDays);
    }

    @Test
    @DisplayName("정산 상태 조회 - 정산 존재하는 경우")
    void getSettlementStatus_Success() {
        // given
        WeeklySettlement settlement = createWeeklySettlement();
        
        given(weeklySettlementRepository.findByUserIdAndWeekStartDate(userId, weekStartDate))
                .willReturn(List.of(settlement));
        
        // when
        SettlementStatusResponse response = settlementService.getSettlementStatus(userId, weekStartDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSettlementId()).isEqualTo(settlement.getId());
        assertThat(response.getUserId()).isEqualTo(settlement.getUserId());
        assertThat(response.getWeekStartDate()).isEqualTo(settlement.getWeekStartDate());
        assertThat(response.getTotalCrystalIncome()).isEqualTo(settlement.getTotalCrystalIncome());
        assertThat(response.getTotalDesireItemIncome()).isEqualTo(settlement.getTotalDesireItemIncome());
        assertThat(response.getTotalIncome()).isEqualTo(settlement.getTotalIncome());
    }

    @Test
    @DisplayName("정산 상태 조회 - 정산 존재하지 않는 경우")
    void getSettlementStatus_NotFound() {
        // given
        given(weeklySettlementRepository.findByUserIdAndWeekStartDate(userId, weekStartDate))
                .willReturn(List.of());
        
        // when
        SettlementStatusResponse response = settlementService.getSettlementStatus(userId, weekStartDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getWeekStartDate()).isEqualTo(weekStartDate);
        assertThat(response.getSettlementId()).isNull();
    }

    @Test
    @DisplayName("정산 상세 조회 - 정산 존재하는 경우")
    void getSettlementDetail_Success() {
        // given
        WeeklySettlement settlement = createWeeklySettlement();
        WeeklyBossRecord bossRecord = createWeeklyBossRecord(settlement.getId());
        
        given(weeklySettlementRepository.findByUserIdAndWeekStartDate(userId, weekStartDate))
                .willReturn(List.of(settlement));
        given(weeklyBossRecordRepository.findBySettlementId(settlement.getId()))
                .willReturn(List.of(bossRecord));
        
        // when
        SettlementDetailResponse response = settlementService.getSettlementDetail(userId, weekStartDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSettlementId()).isEqualTo(settlement.getId());
        assertThat(response.getUserId()).isEqualTo(settlement.getUserId());
        assertThat(response.getWeekStartDate()).isEqualTo(settlement.getWeekStartDate());
        assertThat(response.getBossRecords()).hasSize(1);
        
        BossRecordDetailResponse bossRecordResponse = response.getBossRecords().get(0);
        assertThat(bossRecordResponse.getBossRecordId()).isEqualTo(bossRecord.getId());
        assertThat(bossRecordResponse.getCharacterId()).isEqualTo(bossRecord.getCharacterId());
        assertThat(bossRecordResponse.getBossId()).isEqualTo(bossRecord.getBossId());
    }

    @Test
    @DisplayName("정산 상세 조회 - 정산 존재하지 않는 경우")
    void getSettlementDetail_NotFound() {
        // given
        given(weeklySettlementRepository.findByUserIdAndWeekStartDate(userId, weekStartDate))
                .willReturn(List.of());
        
        // when
        SettlementDetailResponse response = settlementService.getSettlementDetail(userId, weekStartDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getWeekStartDate()).isEqualTo(weekStartDate);
        assertThat(response.getSettlementId()).isNull();
        assertThat(response.getBossRecords()).isEmpty();
    }

    // Helper methods
    private SettlementRequest createSettlementRequest() {
        BossRecordRequest bossRecord = BossRecordRequest.builder()
                .characterId(characterId)
                .bossId(1L)
                .partySize(2)
                .crystalIncome(BigInteger.valueOf(850))
                .desireItems(List.of())
                .build();

        return SettlementRequest.builder()
                .worldName(worldName)
                .bossRecords(List.of(bossRecord))
                .build();
    }

    private WeeklySettlement createWeeklySettlement() {
        WeeklySettlement settlement = WeeklySettlement.builder()
                .userId(userId)
                .worldName(worldName)
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(BigInteger.valueOf(850))
                .totalDesireItemIncome(BigInteger.ZERO)
                .totalIncome(BigInteger.valueOf(850))
                .totalBossCount(1)
                .characterCount(1)
                .build();
        
        // 테스트를 위해 ID 설정
        try {
            java.lang.reflect.Field idField = WeeklySettlement.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(settlement, 1L);
        } catch (Exception e) {
            // 리플렉션 실패 시 무시
        }
        
        return settlement;
    }

    private WeeklyBossRecord createWeeklyBossRecord(Long settlementId) {
        WeeklyBossRecord record = WeeklyBossRecord.builder()
                .settlementId(settlementId)
                .userId(userId)
                .characterId(characterId)
                .bossId(1L)
                .weekStartDate(weekStartDate)
                .crystalIncome(BigInteger.valueOf(850))
                .partySize(2)
                .desireItemIncome(BigInteger.ZERO)
                .totalIncome(BigInteger.valueOf(850))
                .build();
        
        // 테스트를 위해 ID 설정
        try {
            java.lang.reflect.Field idField = WeeklyBossRecord.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(record, 1L);
        } catch (Exception e) {
            // 리플렉션 실패 시 무시
        }
        
        return record;
    }

    private SettlementCompleteResponse createSettlementCompleteResponse() {
        return SettlementCompleteResponse.builder()
                .settlementId(1L)
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(BigInteger.valueOf(850))
                .totalDesireItemIncome(BigInteger.ZERO)
                .totalIncome(BigInteger.valueOf(850))
                .totalBossCount(1)
                .characterCount(1)
                .build();
    }
} 