package com.happymapleday.settlement.service.processor;

import com.happymapleday.settlement.dto.request.BossRecordRequest;
import com.happymapleday.settlement.dto.request.DesireItemRequest;
import com.happymapleday.settlement.entity.WeeklyBossRecord;
import com.happymapleday.settlement.repository.WeeklyBossRecordRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BossRecordProcessorTest {
    
    @InjectMocks
    private BossRecordProcessor processor;
    
    @Mock
    private WeeklyBossRecordRepository weeklyBossRecordRepository;
    
    @Mock
    private DesireItemProcessor desireItemProcessor;
    
    private Long userId;
    private LocalDate weekStartDate;
    private BossRecordRequest bossRecordRequest;
    
    @BeforeEach
    void setUp() {
        userId = 1L;
        weekStartDate = LocalDate.of(2024, 1, 4);
        
        bossRecordRequest = BossRecordRequest.builder()
                .characterId(1L)
                .bossId(1L)
                .partySize(2)
                .crystalIncome(BigInteger.valueOf(850))
                .desireItems(List.of())
                .build();
    }
    
    @Test
    @DisplayName("보스 기록 생성 - 성공")
    void createBossRecords_Success() {
        // given
        List<BossRecordRequest> bossRequests = List.of(bossRecordRequest);
        
        given(weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                1L, 1L, weekStartDate)).willReturn(false);
        
        // when
        List<WeeklyBossRecord> result = processor.createBossRecords(userId, weekStartDate, bossRequests);
        
        // then
        assertThat(result).hasSize(1);
        WeeklyBossRecord record = result.get(0);
        assertThat(record.getUserId()).isEqualTo(userId);
        assertThat(record.getCharacterId()).isEqualTo(1L);
        assertThat(record.getBossId()).isEqualTo(1L);
        assertThat(record.getWeekStartDate()).isEqualTo(weekStartDate);
        assertThat(record.getCrystalIncome()).isEqualTo(BigInteger.valueOf(850));
        assertThat(record.getPartySize()).isEqualTo(2);
        assertThat(record.getDesireItemIncome()).isEqualTo(BigInteger.ZERO);
        assertThat(record.getTotalIncome()).isEqualTo(BigInteger.valueOf(850));
        
        verify(weeklyBossRecordRepository).existsByCharacterIdAndBossIdAndWeekStartDate(
                1L, 1L, weekStartDate);
    }
    
    @Test
    @DisplayName("보스 기록 생성 - 중복 기록 예외")
    void createBossRecords_DuplicateRecord_ThrowsException() {
        // given
        List<BossRecordRequest> bossRequests = List.of(bossRecordRequest);
        
        given(weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                1L, 1L, weekStartDate)).willReturn(true);
        
        // when & then
        assertThatThrownBy(() -> processor.createBossRecords(userId, weekStartDate, bossRequests))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 이번 주에 완료된 기록이 있습니다");
        
        verify(weeklyBossRecordRepository).existsByCharacterIdAndBossIdAndWeekStartDate(
                1L, 1L, weekStartDate);
    }
    
    @Test
    @DisplayName("보스 기록 저장 및 물욕템 처리 - 성공")
    void saveBossRecordsWithDesireItems_Success() {
        // given
        Long settlementId = 1L;
        WeeklyBossRecord bossRecord = createWeeklyBossRecord();
        List<WeeklyBossRecord> bossRecords = List.of(bossRecord);
        List<BossRecordRequest> bossRequests = List.of(bossRecordRequest);
        
        WeeklyBossRecord savedRecord = createWeeklyBossRecordWithSettlement(settlementId);
        setRecordId(savedRecord, 1L);
        
        given(weeklyBossRecordRepository.save(any(WeeklyBossRecord.class)))
                .willReturn(savedRecord);
        
        // when
        List<WeeklyBossRecord> result = processor.saveBossRecordsWithDesireItems(
                settlementId, bossRecords, bossRequests);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getSettlementId()).isEqualTo(settlementId);
        
        verify(weeklyBossRecordRepository).save(any(WeeklyBossRecord.class));
    }
    
    @Test
    @DisplayName("보스 기록 저장 및 물욕템 처리 - 물욕템 포함")
    void saveBossRecordsWithDesireItems_WithDesireItems() {
        // given
        Long settlementId = 1L;
        
        DesireItemRequest desireItemRequest = DesireItemRequest.builder()
                .desireItemId(1L)
                .salePrice(BigInteger.valueOf(500))
                .build();
        
        BossRecordRequest bossRequestWithDesireItem = BossRecordRequest.builder()
                .characterId(1L)
                .bossId(1L)
                .partySize(2)
                .crystalIncome(BigInteger.valueOf(850))
                .desireItems(List.of(desireItemRequest))
                .build();
        
        WeeklyBossRecord bossRecord = createWeeklyBossRecord();
        List<WeeklyBossRecord> bossRecords = List.of(bossRecord);
        List<BossRecordRequest> bossRequests = List.of(bossRequestWithDesireItem);
        
        WeeklyBossRecord savedRecord = createWeeklyBossRecordWithSettlement(settlementId);
        setRecordId(savedRecord, 1L);
        
        given(weeklyBossRecordRepository.save(any(WeeklyBossRecord.class)))
                .willReturn(savedRecord);
        
        // when
        List<WeeklyBossRecord> result = processor.saveBossRecordsWithDesireItems(
                settlementId, bossRecords, bossRequests);
        
        // then
        assertThat(result).hasSize(1);
        
        verify(weeklyBossRecordRepository).save(any(WeeklyBossRecord.class));
        verify(desireItemProcessor).processDesireItems(1L, List.of(desireItemRequest));
    }
    
    @Test
    @DisplayName("기존 보스 기록 삭제 - 성공")
    void deleteExistingBossRecords_Success() {
        // given
        Long settlementId = 1L;
        WeeklyBossRecord existingRecord = createWeeklyBossRecord();
        setRecordId(existingRecord, 1L);
        
        given(weeklyBossRecordRepository.findBySettlementId(settlementId))
                .willReturn(List.of(existingRecord));
        
        // when
        processor.deleteExistingBossRecords(settlementId);
        
        // then
        verify(weeklyBossRecordRepository).findBySettlementId(settlementId);
        verify(desireItemProcessor).deleteDesireItemsByBossRecordId(1L);
        verify(weeklyBossRecordRepository).deleteBySettlementId(settlementId);
    }
    
    @Test
    @DisplayName("다중 보스 기록 생성 - 성공")
    void createMultipleBossRecords_Success() {
        // given
        BossRecordRequest request1 = BossRecordRequest.builder()
                .characterId(1L)
                .bossId(1L)
                .partySize(2)
                .crystalIncome(BigInteger.valueOf(850))
                .desireItems(List.of())
                .build();
        
        BossRecordRequest request2 = BossRecordRequest.builder()
                .characterId(2L)
                .bossId(2L)
                .partySize(3)
                .crystalIncome(BigInteger.valueOf(1020))
                .desireItems(List.of())
                .build();
        
        List<BossRecordRequest> bossRequests = List.of(request1, request2);
        
        given(weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                1L, 1L, weekStartDate)).willReturn(false);
        given(weeklyBossRecordRepository.existsByCharacterIdAndBossIdAndWeekStartDate(
                2L, 2L, weekStartDate)).willReturn(false);
        
        // when
        List<WeeklyBossRecord> result = processor.createBossRecords(userId, weekStartDate, bossRequests);
        
        // then
        assertThat(result).hasSize(2);
        
        WeeklyBossRecord record1 = result.get(0);
        assertThat(record1.getCharacterId()).isEqualTo(1L);
        assertThat(record1.getBossId()).isEqualTo(1L);
        assertThat(record1.getCrystalIncome()).isEqualTo(BigInteger.valueOf(850));
        
        WeeklyBossRecord record2 = result.get(1);
        assertThat(record2.getCharacterId()).isEqualTo(2L);
        assertThat(record2.getBossId()).isEqualTo(2L);
        assertThat(record2.getCrystalIncome()).isEqualTo(BigInteger.valueOf(1020));
        
        verify(weeklyBossRecordRepository).existsByCharacterIdAndBossIdAndWeekStartDate(
                1L, 1L, weekStartDate);
        verify(weeklyBossRecordRepository).existsByCharacterIdAndBossIdAndWeekStartDate(
                2L, 2L, weekStartDate);
    }
    
    private WeeklyBossRecord createWeeklyBossRecord() {
        return WeeklyBossRecord.builder()
                .settlementId(null)
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
    
    private WeeklyBossRecord createWeeklyBossRecordWithSettlement(Long settlementId) {
        return WeeklyBossRecord.builder()
                .settlementId(settlementId)
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
    
    private void setRecordId(WeeklyBossRecord record, Long id) {
        try {
            java.lang.reflect.Field idField = WeeklyBossRecord.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(record, id);
        } catch (Exception e) {
            // 리플렉션 실패 시 무시
        }
    }
} 