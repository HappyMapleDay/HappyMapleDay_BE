package com.happymapleday.settlement.service;

import com.happymapleday.settlement.dto.request.*;
import com.happymapleday.settlement.dto.response.*;
import com.happymapleday.settlement.entity.*;
import com.happymapleday.settlement.repository.*;
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
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    private DesireItemRecordRepository desireItemRecordRepository;

    private LocalDate weekStartDate;
    private Long userId;
    private String worldName;
    private Long characterId;

    @BeforeEach
    void setUp() {
        weekStartDate = getWeekStartDate(LocalDate.now()); // 올바른 주차 시작일 계산
        userId = 1L;
        worldName = "크로아";
        characterId = 1L;
    }

    @Test
    @DisplayName("정산 upsert - 새로운 정산 생성 성공")
    void upsertSettlement_CreateNew_Success() {
        // given
        SettlementRequest request = createSettlementRequest();
        WeeklySettlement settlement = createWeeklySettlement();
        WeeklyBossRecord bossRecord = createWeeklyBossRecord(settlement.getId());
        
        given(weeklySettlementRepository.findByUserIdAndWorldNameAndWeekStartDate(
                userId, worldName, weekStartDate)).willReturn(Optional.empty());
        given(weeklySettlementRepository.save(any(WeeklySettlement.class))).willReturn(settlement);
        given(weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                any(), any(), any())).willReturn(false);
        given(weeklyBossRecordRepository.save(any(WeeklyBossRecord.class))).willReturn(bossRecord);
        
        // when
        SettlementCompleteResponse response = settlementService.upsertSettlement(userId, weekStartDate, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSettlementId()).isEqualTo(settlement.getId());
        assertThat(response.getTotalCrystalIncome()).isEqualTo(bossRecord.getCrystalIncome());
        verify(weeklySettlementRepository, times(1)).save(any(WeeklySettlement.class));
        verify(weeklyBossRecordRepository, times(1)).save(any(WeeklyBossRecord.class));
    }

    @Test
    @DisplayName("정산 upsert - 기존 정산 수정 성공")
    void upsertSettlement_ModifyExisting_Success() {
        // given
        SettlementRequest request = createSettlementRequest();
        WeeklySettlement existingSettlement = createWeeklySettlement();
        WeeklyBossRecord bossRecord = createWeeklyBossRecord(existingSettlement.getId());
        
        given(weeklySettlementRepository.findByUserIdAndWorldNameAndWeekStartDate(
                userId, worldName, weekStartDate)).willReturn(Optional.of(existingSettlement));
        given(weeklyBossRecordRepository.findBySettlementIdOrderByCreatedAtAsc(existingSettlement.getId()))
                .willReturn(List.of(bossRecord));
        given(weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                any(), any(), any())).willReturn(false);
        given(weeklyBossRecordRepository.save(any(WeeklyBossRecord.class))).willReturn(bossRecord);
        given(weeklySettlementRepository.save(any(WeeklySettlement.class))).willReturn(existingSettlement);
        
        // when
        SettlementCompleteResponse response = settlementService.upsertSettlement(userId, weekStartDate, request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getSettlementId()).isEqualTo(existingSettlement.getId());
        verify(weeklyBossRecordRepository, times(1)).deleteBySettlementId(existingSettlement.getId());
        verify(weeklyBossRecordRepository, times(1)).save(any(WeeklyBossRecord.class));
        verify(weeklySettlementRepository, times(1)).save(any(WeeklySettlement.class));
    }

    @Test
    @DisplayName("정산 upsert - 월드별 독립적 정산")
    void upsertSettlement_IndependentWorlds() {
        // given
        String world1 = "크로아";
        String world2 = "베라";
        
        SettlementRequest request1 = createSettlementRequestForWorld(world1);
        SettlementRequest request2 = createSettlementRequestForWorld(world2);
        
        WeeklySettlement settlement1 = createWeeklySettlementForWorld(world1, 1L);
        WeeklySettlement settlement2 = createWeeklySettlementForWorld(world2, 2L);
        
        WeeklyBossRecord bossRecord1 = createWeeklyBossRecord(settlement1.getId());
        WeeklyBossRecord bossRecord2 = createWeeklyBossRecord(settlement2.getId());
        
        given(weeklySettlementRepository.findByUserIdAndWorldNameAndWeekStartDate(
                userId, world1, weekStartDate)).willReturn(Optional.empty());
        given(weeklySettlementRepository.findByUserIdAndWorldNameAndWeekStartDate(
                userId, world2, weekStartDate)).willReturn(Optional.empty());
        given(weeklySettlementRepository.save(any(WeeklySettlement.class)))
                .willReturn(settlement1).willReturn(settlement2);
        given(weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                any(), any(), any())).willReturn(false);
        given(weeklyBossRecordRepository.save(any(WeeklyBossRecord.class)))
                .willReturn(bossRecord1).willReturn(bossRecord2);
        
        // when
        SettlementCompleteResponse response1 = settlementService.upsertSettlement(userId, weekStartDate, request1);
        SettlementCompleteResponse response2 = settlementService.upsertSettlement(userId, weekStartDate, request2);
        
        // then
        assertThat(response1.getSettlementId()).isEqualTo(settlement1.getId());
        assertThat(response2.getSettlementId()).isEqualTo(settlement2.getId());
        assertThat(response1.getSettlementId()).isNotEqualTo(response2.getSettlementId());
        
        verify(weeklySettlementRepository, times(2)).save(any(WeeklySettlement.class));
        verify(weeklyBossRecordRepository, times(2)).save(any(WeeklyBossRecord.class));
    }

    @Test
    @DisplayName("정산 upsert - 중복 보스 기록 시 예외 발생")
    void upsertSettlement_DuplicateBossRecord_ThrowsException() {
        // given
        SettlementRequest request = createSettlementRequest();
        
        given(weeklySettlementRepository.findByUserIdAndWorldNameAndWeekStartDate(
                userId, worldName, weekStartDate)).willReturn(Optional.empty());
        given(weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                characterId, 1L, weekStartDate)).willReturn(true);
        
        // when & then
        assertThatThrownBy(() -> settlementService.upsertSettlement(userId, weekStartDate, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 이번 주에 완료된 기록이 있습니다");
        
        verify(weeklySettlementRepository, never()).save(any(WeeklySettlement.class));
        verify(weeklyBossRecordRepository, never()).save(any(WeeklyBossRecord.class));
    }

    @Test
    @DisplayName("정산 upsert - 캐릭터당 주간 제한 초과 시 예외 발생")
    void upsertSettlement_ExcessCrystalsPerCharacter_ThrowsException() {
        // given
        SettlementRequest request = createSettlementRequestWithExcessCrystals();
        
        given(weeklySettlementRepository.findByUserIdAndWorldNameAndWeekStartDate(
                userId, worldName, weekStartDate)).willReturn(Optional.empty());
        given(weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                any(), any(), any())).willReturn(false);
        
        // when & then
        assertThatThrownBy(() -> settlementService.upsertSettlement(userId, weekStartDate, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("캐릭터당 주간 결정석 판매 제한을 초과했습니다");
        
        verify(weeklySettlementRepository, never()).save(any(WeeklySettlement.class));
        verify(weeklyBossRecordRepository, never()).save(any(WeeklyBossRecord.class));
    }

    @Test
    @DisplayName("현재 주차 상태 조회 - 성공")
    void getCurrentWeekStatus_Success() {
        // given
        LocalDate currentWeekStart = getWeekStartDate(LocalDate.now());
        WeeklySettlement settlement = createWeeklySettlement();
        List<WeeklySettlement> settlements = List.of(settlement);
        
        given(weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId))
                .willReturn(settlements);
        
        // when
        CurrentWeekStatusResponse response = settlementService.getCurrentWeekStatus(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCurrentWeekStart()).isEqualTo(currentWeekStart);
        assertThat(response.getNextWeekStart()).isEqualTo(currentWeekStart.plusWeeks(1));
        assertThat(response.getIsCompleted()).isTrue(); // settlement의 weekStartDate가 currentWeekStart와 같고 isFinalized가 true이므로
        assertThat(response.getRemainingDays()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("정산 상태 조회 - 정산 존재하는 경우")
    void getSettlementStatus_Success() {
        // given
        WeeklySettlement settlement = createWeeklySettlement();
        List<WeeklySettlement> settlements = List.of(settlement);
        
        given(weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId))
                .willReturn(settlements);
        
        // when
        SettlementStatusResponse response = settlementService.getSettlementStatus(userId, weekStartDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSettlementId()).isEqualTo(settlement.getId());
        assertThat(response.getUserId()).isEqualTo(settlement.getUserId());
        assertThat(response.getWeekStartDate()).isEqualTo(settlement.getWeekStartDate());
        assertThat(response.getIsFinalized()).isTrue();
        assertThat(response.getTotalCrystalIncome()).isEqualTo(settlement.getTotalCrystalIncome());
        assertThat(response.getTotalDesireItemIncome()).isEqualTo(settlement.getTotalDesireItemIncome());
        assertThat(response.getTotalIncome()).isEqualTo(settlement.getTotalIncome());
    }

    @Test
    @DisplayName("정산 상태 조회 - 정산 존재하지 않는 경우")
    void getSettlementStatus_NotFound() {
        // given
        List<WeeklySettlement> settlements = List.of();
        
        given(weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId))
                .willReturn(settlements);
        
        // when
        SettlementStatusResponse response = settlementService.getSettlementStatus(userId, weekStartDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsFinalized()).isFalse();
        assertThat(response.getWeekStartDate()).isEqualTo(weekStartDate);
        assertThat(response.getSettlementId()).isNull();
    }

    @Test
    @DisplayName("정산 상세 조회 - 정산 존재하는 경우")
    void getSettlementDetail_Success() {
        // given
        WeeklySettlement settlement = createWeeklySettlement();
        List<WeeklySettlement> settlements = List.of(settlement);
        
        WeeklyBossRecord bossRecord = createWeeklyBossRecordWithDesireItems(settlement.getId());
        List<WeeklyBossRecord> bossRecords = List.of(bossRecord);
        
        given(weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId))
                .willReturn(settlements);
        given(weeklyBossRecordRepository.findBySettlementIdOrderByCreatedAtAsc(settlement.getId()))
                .willReturn(bossRecords);
        
        // when
        SettlementDetailResponse response = settlementService.getSettlementDetail(userId, weekStartDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSettlementId()).isEqualTo(settlement.getId());
        assertThat(response.getUserId()).isEqualTo(settlement.getUserId());
        assertThat(response.getWeekStartDate()).isEqualTo(settlement.getWeekStartDate());
        assertThat(response.getIsFinalized()).isTrue();
        assertThat(response.getBossRecords()).hasSize(1);
        
        // 보스 레코드 상세 정보 확인
        BossRecordDetailResponse bossRecordResponse = response.getBossRecords().get(0);
        assertThat(bossRecordResponse.getBossRecordId()).isEqualTo(bossRecord.getId());
        assertThat(bossRecordResponse.getCharacterId()).isEqualTo(bossRecord.getCharacterId());
        assertThat(bossRecordResponse.getBossId()).isEqualTo(bossRecord.getBossId());
        assertThat(bossRecordResponse.getPartySize()).isEqualTo(bossRecord.getPartySize());
        assertThat(bossRecordResponse.getCrystalIncome()).isEqualTo(bossRecord.getCrystalIncome());
        assertThat(bossRecordResponse.getDesireItems()).hasSize(1);
    }

    @Test
    @DisplayName("정산 상세 조회 - 정산 존재하지 않는 경우")
    void getSettlementDetail_NotFound() {
        // given
        List<WeeklySettlement> settlements = List.of();
        
        given(weeklySettlementRepository.findByUserIdOrderByWeekStartDateDesc(userId))
                .willReturn(settlements);
        
        // when
        SettlementDetailResponse response = settlementService.getSettlementDetail(userId, weekStartDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsFinalized()).isFalse();
        assertThat(response.getWeekStartDate()).isEqualTo(weekStartDate);
        assertThat(response.getSettlementId()).isNull();
        assertThat(response.getBossRecords()).isEmpty();
    }

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

    private SettlementRequest createSettlementRequestForWorld(String worldName) {
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

    private SettlementRequest createSettlementRequestWithExcessCrystals() {
        // 캐릭터당 주간 제한(12개)을 초과하는 13개의 보스 기록 생성
        List<BossRecordRequest> bossRecords = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            bossRecords.add(BossRecordRequest.builder()
                    .characterId(characterId)
                    .bossId((long) (i + 1))
                    .partySize(1)
                    .crystalIncome(BigInteger.valueOf(850))
                    .desireItems(List.of())
                    .build());
        }

        return SettlementRequest.builder()
                .worldName(worldName)
                .bossRecords(bossRecords)
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
                .isFinalized(true)
                .finalizedAt(LocalDateTime.now())
                .build();
        
        // 테스트를 위해 ID 설정 (리플렉션 사용)
        try {
            java.lang.reflect.Field idField = WeeklySettlement.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(settlement, 1L);
        } catch (Exception e) {
            // 리플렉션 실패 시 무시
        }
        
        return settlement;
    }

    private WeeklySettlement createWeeklySettlementForWorld(String worldName, Long id) {
        WeeklySettlement settlement = WeeklySettlement.builder()
                .userId(userId)
                .worldName(worldName)
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(BigInteger.valueOf(850))
                .totalDesireItemIncome(BigInteger.ZERO)
                .totalIncome(BigInteger.valueOf(850))
                .totalBossCount(1)
                .characterCount(1)
                .isFinalized(true)
                .finalizedAt(LocalDateTime.now())
                .build();
        
        // 테스트를 위해 ID 설정 (리플렉션 사용)
        try {
            java.lang.reflect.Field idField = WeeklySettlement.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(settlement, id);
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
                .partySize(1)
                .desireItemIncome(BigInteger.ZERO)
                .totalIncome(BigInteger.valueOf(850))
                .build();
        
        // 테스트를 위해 ID 설정 (리플렉션 사용)
        try {
            java.lang.reflect.Field idField = WeeklyBossRecord.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(record, 1L);
        } catch (Exception e) {
            // 리플렉션 실패 시 무시
        }
        
        return record;
    }

    private WeeklyBossRecord createWeeklyBossRecordWithDesireItems(Long settlementId) {
        // 물욕템 레코드 생성
        DesireItemRecord desireItemRecord = DesireItemRecord.builder()
                .weeklyBossRecordId(1L)
                .characterId(characterId)
                .desireItemId(1L)
                .salePrice(BigInteger.valueOf(500))
                .build();
        
        // 보스 레코드 생성
        WeeklyBossRecord record = WeeklyBossRecord.builder()
                .settlementId(settlementId)
                .userId(userId)
                .characterId(characterId)
                .bossId(1L)
                .weekStartDate(weekStartDate)
                .crystalIncome(BigInteger.valueOf(850))
                .partySize(2)
                .desireItemIncome(BigInteger.valueOf(500))
                .totalIncome(BigInteger.valueOf(1350))
                .build();
        
        // 테스트를 위해 ID 설정 (리플렉션 사용)
        try {
            java.lang.reflect.Field idField = WeeklyBossRecord.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(record, 1L);
            
            // 물욕템 레코드를 보스 레코드에 추가
            java.lang.reflect.Field desireItemsField = WeeklyBossRecord.class.getDeclaredField("desireItemRecords");
            desireItemsField.setAccessible(true);
            desireItemsField.set(record, List.of(desireItemRecord));
        } catch (Exception e) {
            // 리플렉션 실패 시 무시
        }
        
        return record;
    }

    private LocalDate getWeekStartDate(LocalDate date) {
        // 목요일을 기준으로 주차 시작일 계산 (구현체와 동일)
        int dayOfWeek = date.getDayOfWeek().getValue(); // 월=1, 화=2, 수=3, 목=4, 금=5, 토=6, 일=7
        int daysFromThursday = (dayOfWeek + 3) % 7; // 목요일을 0으로 만들기 위한 계산
        return date.minusDays(daysFromThursday);
    }
} 