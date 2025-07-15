package com.happymapleday.settlement.service.processor;

import com.happymapleday.settlement.dto.request.BossRecordRequest;
import com.happymapleday.settlement.dto.request.DesireItemRequest;
import com.happymapleday.settlement.dto.request.SettlementRequest;
import com.happymapleday.settlement.dto.response.SettlementCompleteResponse;
import com.happymapleday.settlement.entity.WeeklyBossRecord;
import com.happymapleday.settlement.entity.WeeklySettlement;
import com.happymapleday.settlement.repository.WeeklySettlementRepository;
import com.happymapleday.settlement.service.validator.CrystalLimitValidator;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeeklySettlementProcessorTest {
    
    @InjectMocks
    private WeeklySettlementProcessor processor;
    
    @Mock
    private WeeklySettlementRepository weeklySettlementRepository;
    
    @Mock
    private BossRecordProcessor bossRecordProcessor;
    
    @Mock
    private CrystalLimitValidator crystalLimitValidator;
    
    private Long userId;
    private LocalDate weekStartDate;
    private SettlementRequest request;
    private WeeklyBossRecord bossRecord;
    
    @BeforeEach
    void setUp() {
        userId = 1L;
        weekStartDate = LocalDate.of(2024, 1, 4);
        
        request = SettlementRequest.builder()
                .worldName("크로아")
                .bossRecords(List.of(createBossRecordRequest()))
                .build();
        
        bossRecord = createWeeklyBossRecord();
    }
    
    @Test
    @DisplayName("새로운 정산 생성 - 성공")
    void createSettlement_Success() {
        // given
        List<WeeklyBossRecord> bossRecords = List.of(bossRecord);
        WeeklySettlement settlement = createWeeklySettlement();
        WeeklySettlement savedSettlement = createWeeklySettlement();
        
        given(bossRecordProcessor.createBossRecords(userId, weekStartDate, request.getBossRecords()))
                .willReturn(bossRecords);
        given(weeklySettlementRepository.save(any(WeeklySettlement.class)))
                .willReturn(settlement).willReturn(savedSettlement);
        given(bossRecordProcessor.saveBossRecordsWithDesireItems(any(), any(), any()))
                .willReturn(bossRecords);
        
        // when
        SettlementCompleteResponse response = processor.createSettlement(userId, weekStartDate, request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getSettlementId()).isEqualTo(settlement.getId());
        assertThat(response.getWeekStartDate()).isEqualTo(weekStartDate);
        assertThat(response.getTotalCrystalIncome()).isEqualTo(BigInteger.valueOf(850));
        assertThat(response.getTotalBossCount()).isEqualTo(1);
        assertThat(response.getCharacterCount()).isEqualTo(1);
        
        verify(bossRecordProcessor).createBossRecords(userId, weekStartDate, request.getBossRecords());
        verify(crystalLimitValidator).validateCrystalLimits(bossRecords);
        verify(weeklySettlementRepository, times(2)).save(any(WeeklySettlement.class));
        verify(bossRecordProcessor).saveBossRecordsWithDesireItems(any(), any(), any());
    }
    
    @Test
    @DisplayName("기존 정산 수정 - 성공")
    void updateSettlement_Success() {
        // given
        Long settlementId = 1L;
        List<WeeklyBossRecord> bossRecords = List.of(bossRecord);
        WeeklySettlement settlement = createWeeklySettlement();
        
        given(bossRecordProcessor.createBossRecords(userId, weekStartDate, request.getBossRecords()))
                .willReturn(bossRecords);
        given(bossRecordProcessor.saveBossRecordsWithDesireItems(settlementId, bossRecords, request.getBossRecords()))
                .willReturn(bossRecords);
        given(weeklySettlementRepository.save(any(WeeklySettlement.class)))
                .willReturn(settlement);
        
        // when
        SettlementCompleteResponse response = processor.updateSettlement(settlementId, userId, weekStartDate, request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getSettlementId()).isEqualTo(settlement.getId());
        assertThat(response.getWeekStartDate()).isEqualTo(weekStartDate);
        assertThat(response.getTotalCrystalIncome()).isEqualTo(BigInteger.valueOf(850));
        
        verify(bossRecordProcessor).deleteExistingBossRecords(settlementId);
        verify(bossRecordProcessor).createBossRecords(userId, weekStartDate, request.getBossRecords());
        verify(crystalLimitValidator).validateCrystalLimits(bossRecords);
        verify(bossRecordProcessor).saveBossRecordsWithDesireItems(settlementId, bossRecords, request.getBossRecords());
        verify(weeklySettlementRepository).save(any(WeeklySettlement.class));
    }
    
    @Test
    @DisplayName("물욕템 포함 정산 생성 - 성공")
    void createSettlement_WithDesireItems() {
        // given
        DesireItemRequest desireItemRequest = DesireItemRequest.builder()
                .desireItemId(1L)
                .salePrice(BigInteger.valueOf(500))
                .build();
        
        BossRecordRequest bossRecordWithDesireItem = BossRecordRequest.builder()
                .characterId(1L)
                .bossId(1L)
                .partySize(2)
                .crystalIncome(BigInteger.valueOf(850))
                .desireItems(List.of(desireItemRequest))
                .build();
        
        SettlementRequest requestWithDesireItems = SettlementRequest.builder()
                .worldName("크로아")
                .bossRecords(List.of(bossRecordWithDesireItem))
                .build();
        
        // 물욕템 수입이 포함된 보스 기록
        WeeklyBossRecord bossRecordWithDesireIncome = WeeklyBossRecord.builder()
                .settlementId(1L)
                .userId(userId)
                .characterId(1L)
                .bossId(1L)
                .weekStartDate(weekStartDate)
                .crystalIncome(BigInteger.valueOf(850))
                .partySize(2)
                .desireItemIncome(BigInteger.valueOf(500))
                .totalIncome(BigInteger.valueOf(1350))
                .build();
        
        List<WeeklyBossRecord> bossRecords = List.of(bossRecordWithDesireIncome);
        WeeklySettlement settlement = createWeeklySettlementWithDesireItems();
        
        given(bossRecordProcessor.createBossRecords(userId, weekStartDate, requestWithDesireItems.getBossRecords()))
                .willReturn(bossRecords);
        given(weeklySettlementRepository.save(any(WeeklySettlement.class)))
                .willReturn(settlement);
        given(bossRecordProcessor.saveBossRecordsWithDesireItems(any(), any(), any()))
                .willReturn(bossRecords);
        
        // when
        SettlementCompleteResponse response = processor.createSettlement(userId, weekStartDate, requestWithDesireItems);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getTotalCrystalIncome()).isEqualTo(BigInteger.valueOf(850));
        assertThat(response.getTotalDesireItemIncome()).isEqualTo(BigInteger.valueOf(500));
        assertThat(response.getTotalIncome()).isEqualTo(BigInteger.valueOf(1350));
    }
    
    private BossRecordRequest createBossRecordRequest() {
        return BossRecordRequest.builder()
                .characterId(1L)
                .bossId(1L)
                .partySize(2)
                .crystalIncome(BigInteger.valueOf(850))
                .desireItems(List.of())
                .build();
    }
    
    private WeeklyBossRecord createWeeklyBossRecord() {
        return WeeklyBossRecord.builder()
                .settlementId(1L)
                .userId(userId)
                .characterId(1L)
                .bossId(1L)
                .weekStartDate(weekStartDate)
                .crystalIncome(BigInteger.valueOf(850))
                .partySize(2)
                .desireItemIncome(BigInteger.ZERO)
                .totalIncome(BigInteger.valueOf(850))
                .build();
    }
    
    private WeeklySettlement createWeeklySettlement() {
        WeeklySettlement settlement = WeeklySettlement.builder()
                .userId(userId)
                .worldName("크로아")
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
    
    private WeeklySettlement createWeeklySettlementWithDesireItems() {
        WeeklySettlement settlement = WeeklySettlement.builder()
                .userId(userId)
                .worldName("크로아")
                .weekStartDate(weekStartDate)
                .totalCrystalIncome(BigInteger.valueOf(850))
                .totalDesireItemIncome(BigInteger.valueOf(500))
                .totalIncome(BigInteger.valueOf(1350))
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
} 