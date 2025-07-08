package com.happymapleday.settlement.service;

import com.happymapleday.settlement.dto.request.BossRecordModifyRequest;
import com.happymapleday.settlement.dto.request.BossRecordRequest;
import com.happymapleday.settlement.dto.request.DesireItemModifyRequest;
import com.happymapleday.settlement.dto.request.DesireItemRequest;
import com.happymapleday.settlement.dto.request.SettlementCompleteRequest;
import com.happymapleday.settlement.dto.request.SettlementModifyRequest;
import com.happymapleday.settlement.dto.response.CurrentWeekStatusResponse;
import com.happymapleday.settlement.dto.response.SettlementCompleteResponse;
import com.happymapleday.settlement.dto.response.SettlementModifyResponse;
import com.happymapleday.settlement.entity.WeeklyBossRecord;
import com.happymapleday.settlement.entity.WeeklySettlement;
import com.happymapleday.settlement.repository.DesireItemRecordRepository;
import com.happymapleday.settlement.repository.WeeklyBossRecordRepository;
import com.happymapleday.settlement.repository.WeeklySettlementRepository;
import com.happymapleday.settlement.service.impl.SettlementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @InjectMocks
    private SettlementServiceImpl settlementService;

    @Mock
    private WeeklySettlementRepository weeklySettlementRepository;

    @Mock
    private WeeklyBossRecordRepository weeklyBossRecordRepository;

    @Mock
    private DesireItemRecordRepository desireItemRecordRepository;

    private LocalDate weekStartDate;
    private Long userId;
    private String worldName;

    @BeforeEach
    void setUp() {
        weekStartDate = LocalDate.now();
        userId = 1L;
        worldName = "크로아";
    }

    @Test
    @DisplayName("정산 완료 - 성공")
    void completeSettlement_Success() {
        // given
        SettlementCompleteRequest request = createSettlementCompleteRequest();
        WeeklySettlement settlement = createWeeklySettlement();
        WeeklyBossRecord bossRecord = createWeeklyBossRecord(settlement.getId());
        
        given(weeklySettlementRepository.existsByUserIdAndWorldNameAndWeekStartDateAndIsFinalizedTrue(
                userId, worldName, weekStartDate)).willReturn(false);
        given(weeklySettlementRepository.save(any(WeeklySettlement.class))).willReturn(settlement);
        given(weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                any(), any(), any())).willReturn(false);
        given(weeklyBossRecordRepository.save(any(WeeklyBossRecord.class))).willReturn(bossRecord);
        
        // when
        SettlementCompleteResponse response = settlementService.completeSettlement(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSettlementId()).isEqualTo(settlement.getId());
        assertThat(response.getTotalCrystalIncome()).isEqualTo(bossRecord.getCrystalIncome());
        verify(weeklySettlementRepository, times(2)).save(any(WeeklySettlement.class));
        verify(weeklyBossRecordRepository, atLeastOnce()).save(any(WeeklyBossRecord.class));
    }

    @Test
    @DisplayName("정산 완료 - 이미 완료된 정산 존재")
    void completeSettlement_AlreadyExists() {
        // given
        SettlementCompleteRequest request = createSettlementCompleteRequest();
        given(weeklySettlementRepository.existsByUserIdAndWorldNameAndWeekStartDateAndIsFinalizedTrue(
                userId, worldName, weekStartDate)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> settlementService.completeSettlement(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 이번 주 정산이 완료되었습니다.");
    }

    @Test
    @DisplayName("정산 수정 - 성공")
    void modifySettlement_Success() {
        // given
        Long settlementId = 1L;
        WeeklySettlement settlement = createWeeklySettlement();
        WeeklyBossRecord bossRecord = createWeeklyBossRecord(settlement.getId());
        List<WeeklyBossRecord> bossRecords = List.of(bossRecord);

        given(weeklySettlementRepository.findById(settlementId)).willReturn(Optional.of(settlement));
        given(weeklyBossRecordRepository.findById(bossRecord.getId())).willReturn(Optional.of(bossRecord));
        given(weeklyBossRecordRepository.findBySettlementIdOrderByCreatedAtAsc(settlementId))
                .willReturn(bossRecords);
        given(weeklyBossRecordRepository.save(any(WeeklyBossRecord.class))).willReturn(bossRecord);
        given(weeklySettlementRepository.save(any(WeeklySettlement.class))).willReturn(settlement);

        // when
        SettlementModifyResponse response = settlementService.modifySettlement(
                settlementId,
                createSettlementModifyRequest(bossRecord.getId())
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSettlementId()).isEqualTo(settlementId);
        verify(desireItemRecordRepository).deleteByWeeklyBossRecordId(bossRecord.getId());
        verify(weeklyBossRecordRepository).save(any(WeeklyBossRecord.class));
        verify(weeklySettlementRepository).save(any(WeeklySettlement.class));
    }

    @Test
    @DisplayName("정산 수정 - 존재하지 않는 정산")
    void modifySettlement_NotFound() {
        // given
        Long settlementId = 999L;
        given(weeklySettlementRepository.findById(settlementId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> settlementService.modifySettlement(
                settlementId,
                createSettlementModifyRequest(1L)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 정산입니다.");
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
        verify(weeklySettlementRepository).delete(settlement);
    }

    @Test
    @DisplayName("정산 삭제 - 권한 없음")
    void deleteSettlement_Unauthorized() {
        // given
        Long settlementId = 1L;
        Long unauthorizedUserId = 999L;
        WeeklySettlement settlement = createWeeklySettlement();
        given(weeklySettlementRepository.findById(settlementId)).willReturn(Optional.of(settlement));

        // when & then
        assertThatThrownBy(() -> settlementService.deleteSettlement(settlementId, unauthorizedUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("다른 사용자의 정산은 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("현재 주차 상태 조회 - 정산 완료")
    void getCurrentWeekStatus_Completed() {
        // given
        LocalDate currentWeekStart = getWeekStartDate(LocalDate.now());
        WeeklySettlement settlement = WeeklySettlement.builder()
                .id(1L)
                .userId(userId)
                .worldName(worldName)
                .weekStartDate(currentWeekStart)
                .isFinalized(true)
                .finalizedAt(LocalDateTime.now())
                .build();
        List<WeeklySettlement> settlements = List.of(settlement);
        
        given(weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId))
                .willReturn(settlements);

        // when
        CurrentWeekStatusResponse response = settlementService.getCurrentWeekStatus(userId);

        // then
        assertThat(response.getIsCompleted()).isTrue();
        assertThat(response.getCurrentWeekStart()).isEqualTo(currentWeekStart);
        assertThat(response.getNextWeekStart()).isEqualTo(currentWeekStart.plusWeeks(1));
        assertThat(response.getNextResetDate()).isNotNull();
    }

    @Test
    @DisplayName("현재 주차 상태 조회 - 정산 미완료")
    void getCurrentWeekStatus_NotCompleted() {
        // given
        given(weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId))
                .willReturn(List.of());

        // when
        CurrentWeekStatusResponse response = settlementService.getCurrentWeekStatus(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsCompleted()).isFalse();
        assertThat(response.getCurrentWeekStart()).isNotNull();
        assertThat(response.getNextWeekStart()).isNotNull();
        assertThat(response.getRemainingDays()).isNotNull();
        assertThat(response.getNextResetDate()).isNotNull();
    }

    private SettlementCompleteRequest createSettlementCompleteRequest() {
        List<DesireItemRequest> desireItems = List.of(
                DesireItemRequest.builder()
                        .desireItemId(1L)
                        .salePrice(BigInteger.valueOf(1000000000))
                        .build()
        );

        List<BossRecordRequest> bossRecords = List.of(
                BossRecordRequest.builder()
                        .characterId(1L)
                        .bossId(1L)
                        .partySize(1)
                        .crystalIncome(BigInteger.valueOf(96750000))
                        .desireItems(desireItems)
                        .build()
        );

        return SettlementCompleteRequest.builder()
                .userId(userId)
                .worldName(worldName)
                .weekStartDate(weekStartDate)
                .bossRecords(bossRecords)
                .build();
    }

    private WeeklySettlement createWeeklySettlement() {
        return WeeklySettlement.builder()
                .id(1L)
                .userId(userId)
                .worldName(worldName)
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(BigInteger.valueOf(96750000))
                .totalDesireItemIncome(BigInteger.valueOf(1000000000))
                .totalIncome(BigInteger.valueOf(1096750000))
                .totalBossCount(1)
                .characterCount(1)
                .isFinalized(true)
                .finalizedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private WeeklyBossRecord createWeeklyBossRecord(Long settlementId) {
        return WeeklyBossRecord.builder()
                .id(1L)
                .settlementId(settlementId)
                .userId(userId)
                .characterId(1L)
                .bossId(1L)
                .weekStartDate(weekStartDate)
                .crystalIncome(BigInteger.valueOf(96750000))
                .partySize(1)
                .desireItemIncome(BigInteger.valueOf(1000000000))
                .totalIncome(BigInteger.valueOf(1096750000))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private SettlementModifyRequest createSettlementModifyRequest(Long bossRecordId) {
        List<DesireItemModifyRequest> desireItems = List.of(
                DesireItemModifyRequest.builder()
                        .desireItemId(1L)
                        .salePrice(BigInteger.valueOf(2000000000))
                        .build()
        );

        List<BossRecordModifyRequest> bossRecords = List.of(
                BossRecordModifyRequest.builder()
                        .weeklyBossRecordId(bossRecordId)
                        .characterId(1L)
                        .bossId(1L)
                        .partySize(1)
                        .crystalIncome(BigInteger.valueOf(96750000))
                        .desireItems(desireItems)
                        .build()
        );

        return SettlementModifyRequest.builder()
                .bossRecords(bossRecords)
                .build();
    }

    private LocalDate getWeekStartDate(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue(); // 월=1, 화=2, 수=3, 목=4, 금=5, 토=6, 일=7
        int daysFromThursday = (dayOfWeek + 3) % 7; // 목요일을 0으로 만들기 위한 계산
        return date.minusDays(daysFromThursday);
    }
} 