package com.happymapleday.settlement.service.processor;

import com.happymapleday.settlement.dto.request.DesireItemRequest;
import com.happymapleday.settlement.entity.DesireItemRecord;
import com.happymapleday.settlement.repository.DesireItemRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesireItemProcessorTest {
    
    @InjectMocks
    private DesireItemProcessor processor;
    
    @Mock
    private DesireItemRecordRepository desireItemRecordRepository;
    
    private Long bossRecordId;
    private DesireItemRequest desireItemRequest;
    
    @BeforeEach
    void setUp() {
        bossRecordId = 1L;
        desireItemRequest = DesireItemRequest.builder()
                .desireItemId(1L)
                .salePrice(BigInteger.valueOf(500))
                .build();
    }
    
    @Test
    @DisplayName("물욕템 처리 - 단일 물욕템")
    void processDesireItems_SingleItem() {
        // given
        List<DesireItemRequest> desireItems = List.of(desireItemRequest);
        DesireItemRecord savedRecord = createDesireItemRecord(bossRecordId, desireItemRequest);
        
        given(desireItemRecordRepository.save(any(DesireItemRecord.class)))
                .willReturn(savedRecord);
        
        // when
        BigInteger result = processor.processDesireItems(bossRecordId, desireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.valueOf(500));
        
        ArgumentCaptor<DesireItemRecord> captor = ArgumentCaptor.forClass(DesireItemRecord.class);
        verify(desireItemRecordRepository).save(captor.capture());
        
        DesireItemRecord capturedRecord = captor.getValue();
        assertThat(capturedRecord.getWeeklyBossRecordId()).isEqualTo(bossRecordId);
        assertThat(capturedRecord.getDesireItemId()).isEqualTo(1L);
        assertThat(capturedRecord.getSalePrice()).isEqualTo(BigInteger.valueOf(500));
    }
    
    @Test
    @DisplayName("물욕템 처리 - 다중 물욕템")
    void processDesireItems_MultipleItems() {
        // given
        DesireItemRequest item1 = DesireItemRequest.builder()
                .desireItemId(1L)
                .salePrice(BigInteger.valueOf(500))
                .build();
        
        DesireItemRequest item2 = DesireItemRequest.builder()
                .desireItemId(2L)
                .salePrice(BigInteger.valueOf(800))
                .build();
        
        DesireItemRequest item3 = DesireItemRequest.builder()
                .desireItemId(3L)
                .salePrice(BigInteger.valueOf(1200))
                .build();
        
        List<DesireItemRequest> desireItems = List.of(item1, item2, item3);
        
        DesireItemRecord savedRecord1 = createDesireItemRecord(bossRecordId, item1);
        DesireItemRecord savedRecord2 = createDesireItemRecord(bossRecordId, item2);
        DesireItemRecord savedRecord3 = createDesireItemRecord(bossRecordId, item3);
        
        given(desireItemRecordRepository.save(any(DesireItemRecord.class)))
                .willReturn(savedRecord1, savedRecord2, savedRecord3);
        
        // when
        BigInteger result = processor.processDesireItems(bossRecordId, desireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.valueOf(2500)); // 500 + 800 + 1200
        
        verify(desireItemRecordRepository, times(3)).save(any(DesireItemRecord.class));
    }
    
    @Test
    @DisplayName("물욕템 처리 - 빈 리스트")
    void processDesireItems_EmptyList() {
        // given
        List<DesireItemRequest> desireItems = new ArrayList<>();
        
        // when
        BigInteger result = processor.processDesireItems(bossRecordId, desireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.ZERO);
        verify(desireItemRecordRepository, never()).save(any(DesireItemRecord.class));
    }
    
    @Test
    @DisplayName("물욕템 처리 - null 리스트")
    void processDesireItems_NullList() {
        // given
        List<DesireItemRequest> desireItems = null;
        
        // when
        BigInteger result = processor.processDesireItems(bossRecordId, desireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.ZERO);
        verify(desireItemRecordRepository, never()).save(any(DesireItemRecord.class));
    }
    
    @Test
    @DisplayName("물욕템 처리 - 0원 물욕템")
    void processDesireItems_ZeroPrice() {
        // given
        DesireItemRequest zeroItem = DesireItemRequest.builder()
                .desireItemId(1L)
                .salePrice(BigInteger.ZERO)
                .build();
        
        List<DesireItemRequest> desireItems = List.of(zeroItem);
        DesireItemRecord savedRecord = createDesireItemRecord(bossRecordId, zeroItem);
        
        given(desireItemRecordRepository.save(any(DesireItemRecord.class)))
                .willReturn(savedRecord);
        
        // when
        BigInteger result = processor.processDesireItems(bossRecordId, desireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.ZERO);
        verify(desireItemRecordRepository).save(any(DesireItemRecord.class));
    }
    
    @Test
    @DisplayName("물욕템 처리 - 대용량 가격")
    void processDesireItems_LargePrice() {
        // given
        DesireItemRequest expensiveItem = DesireItemRequest.builder()
                .desireItemId(1L)
                .salePrice(new BigInteger("999999999999"))
                .build();
        
        List<DesireItemRequest> desireItems = List.of(expensiveItem);
        DesireItemRecord savedRecord = createDesireItemRecord(bossRecordId, expensiveItem);
        
        given(desireItemRecordRepository.save(any(DesireItemRecord.class)))
                .willReturn(savedRecord);
        
        // when
        BigInteger result = processor.processDesireItems(bossRecordId, desireItems);
        
        // then
        assertThat(result).isEqualTo(new BigInteger("999999999999"));
        verify(desireItemRecordRepository).save(any(DesireItemRecord.class));
    }
    
    @Test
    @DisplayName("물욕템 기록 삭제 - 성공")
    void deleteDesireItemsByBossRecordId_Success() {
        // given
        Long bossRecordIdToDelete = 1L;
        
        // when
        processor.deleteDesireItemsByBossRecordId(bossRecordIdToDelete);
        
        // then
        verify(desireItemRecordRepository).deleteByWeeklyBossRecordId(bossRecordIdToDelete);
    }
    
    @Test
    @DisplayName("물욕템 처리 - 혼합 가격")
    void processDesireItems_MixedPrices() {
        // given
        DesireItemRequest cheapItem = DesireItemRequest.builder()
                .desireItemId(1L)
                .salePrice(BigInteger.valueOf(100))
                .build();
        
        DesireItemRequest expensiveItem = DesireItemRequest.builder()
                .desireItemId(2L)
                .salePrice(BigInteger.valueOf(50000))
                .build();
        
        DesireItemRequest mediumItem = DesireItemRequest.builder()
                .desireItemId(3L)
                .salePrice(BigInteger.valueOf(2500))
                .build();
        
        List<DesireItemRequest> desireItems = List.of(cheapItem, expensiveItem, mediumItem);
        
        DesireItemRecord savedRecord1 = createDesireItemRecord(bossRecordId, cheapItem);
        DesireItemRecord savedRecord2 = createDesireItemRecord(bossRecordId, expensiveItem);
        DesireItemRecord savedRecord3 = createDesireItemRecord(bossRecordId, mediumItem);
        
        given(desireItemRecordRepository.save(any(DesireItemRecord.class)))
                .willReturn(savedRecord1, savedRecord2, savedRecord3);
        
        // when
        BigInteger result = processor.processDesireItems(bossRecordId, desireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.valueOf(52600)); // 100 + 50000 + 2500
        verify(desireItemRecordRepository, times(3)).save(any(DesireItemRecord.class));
    }
    
    private DesireItemRecord createDesireItemRecord(Long bossRecordId, DesireItemRequest request) {
        return DesireItemRecord.builder()
                .weeklyBossRecordId(bossRecordId)
                .desireItemId(request.getDesireItemId())
                .salePrice(request.getSalePrice())
                .build();
    }
} 