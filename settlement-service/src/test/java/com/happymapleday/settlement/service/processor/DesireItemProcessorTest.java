package com.happymapleday.settlement.service.processor;

import com.happymapleday.settlement.dto.request.DesireItemRequest;
import com.happymapleday.settlement.entity.DesireItemRecord;
import com.happymapleday.settlement.repository.DesireItemRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DesireItemProcessor 테스트")
class DesireItemProcessorTest {

    @InjectMocks
    private DesireItemProcessor processor;

    @Mock
    private DesireItemRecordRepository desireItemRecordRepository;

    private Long bossRecordId;
    private Long characterId;
    private DesireItemRequest desireItemRequest;

    @BeforeEach
    void setUp() {
        bossRecordId = 1L;
        characterId = 1L;
        desireItemRequest = DesireItemRequest.builder()
                .desireItemId(1L)
                .salePrice(BigInteger.valueOf(500))
                .build();
    }

    @Test
    @DisplayName("물욕템 처리 - 단일 아이템 성공")
    void processDesireItems_SingleItem_Success() {
        // given
        List<DesireItemRequest> desireItems = List.of(desireItemRequest);
        DesireItemRecord savedRecord = createDesireItemRecord(bossRecordId, desireItemRequest);
        
        given(desireItemRecordRepository.save(any(DesireItemRecord.class)))
                .willReturn(savedRecord);
        
        // when
        BigInteger result = processor.processDesireItems(bossRecordId, characterId, desireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.valueOf(500));
        
        verify(desireItemRecordRepository).save(any(DesireItemRecord.class));
    }

    @Test
    @DisplayName("물욕템 처리 - 여러 아이템 성공")
    void processDesireItems_MultipleItems_Success() {
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
        BigInteger result = processor.processDesireItems(bossRecordId, characterId, desireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.valueOf(2500)); // 500 + 800 + 1200
        
        verify(desireItemRecordRepository, times(3)).save(any(DesireItemRecord.class));
    }

    @Test
    @DisplayName("물욕템 처리 - 빈 리스트")
    void processDesireItems_EmptyList_ReturnsZero() {
        // given
        List<DesireItemRequest> emptyDesireItems = List.of();
        
        // when
        BigInteger result = processor.processDesireItems(bossRecordId, characterId, emptyDesireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.ZERO);
        
        verify(desireItemRecordRepository, never()).save(any(DesireItemRecord.class));
    }

    @Test
    @DisplayName("물욕템 처리 - null 리스트")
    void processDesireItems_NullList_ReturnsZero() {
        // given
        List<DesireItemRequest> nullDesireItems = null;
        
        // when
        BigInteger result = processor.processDesireItems(bossRecordId, characterId, nullDesireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.ZERO);
        
        verify(desireItemRecordRepository, never()).save(any(DesireItemRecord.class));
    }

    @Test
    @DisplayName("물욕템 처리 - 0원 가격 아이템")
    void processDesireItems_ZeroPriceItem_Success() {
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
        BigInteger result = processor.processDesireItems(bossRecordId, characterId, desireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.ZERO);
        
        verify(desireItemRecordRepository).save(any(DesireItemRecord.class));
    }

    @Test
    @DisplayName("물욕템 기록 삭제 - 성공")
    void deleteDesireItemsByBossRecordId_Success() {
        // given
        Long targetBossRecordId = 1L;
        
        // when
        processor.deleteDesireItemsByBossRecordId(targetBossRecordId);
        
        // then
        verify(desireItemRecordRepository).deleteByWeeklyBossRecordId(targetBossRecordId);
    }

    @Test
    @DisplayName("물욕템 처리 - 높은 가격 아이템")
    void processDesireItems_HighPriceItem_Success() {
        // given
        DesireItemRequest highPriceItem = DesireItemRequest.builder()
                .desireItemId(1L)
                .salePrice(BigInteger.valueOf(999999999L))
                .build();
        
        List<DesireItemRequest> desireItems = List.of(highPriceItem);
        DesireItemRecord savedRecord = createDesireItemRecord(bossRecordId, highPriceItem);
        
        given(desireItemRecordRepository.save(any(DesireItemRecord.class)))
                .willReturn(savedRecord);
        
        // when
        BigInteger result = processor.processDesireItems(bossRecordId, characterId, desireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.valueOf(999999999L));
        
        verify(desireItemRecordRepository).save(any(DesireItemRecord.class));
    }

    @Test
    @DisplayName("물욕템 처리 - 혼합 가격 아이템들")
    void processDesireItems_MixedPriceItems_Success() {
        // given
        DesireItemRequest lowPriceItem = DesireItemRequest.builder()
                .desireItemId(1L)
                .salePrice(BigInteger.valueOf(100))
                .build();
        DesireItemRequest highPriceItem = DesireItemRequest.builder()
                .desireItemId(2L)
                .salePrice(BigInteger.valueOf(50000))
                .build();
        DesireItemRequest mediumPriceItem = DesireItemRequest.builder()
                .desireItemId(3L)
                .salePrice(BigInteger.valueOf(5000))
                .build();
        
        List<DesireItemRequest> desireItems = List.of(lowPriceItem, highPriceItem, mediumPriceItem);
        
        DesireItemRecord savedRecord1 = createDesireItemRecord(bossRecordId, lowPriceItem);
        DesireItemRecord savedRecord2 = createDesireItemRecord(bossRecordId, highPriceItem);
        DesireItemRecord savedRecord3 = createDesireItemRecord(bossRecordId, mediumPriceItem);
        
        given(desireItemRecordRepository.save(any(DesireItemRecord.class)))
                .willReturn(savedRecord1, savedRecord2, savedRecord3);
        
        // when
        BigInteger result = processor.processDesireItems(bossRecordId, characterId, desireItems);
        
        // then
        assertThat(result).isEqualTo(BigInteger.valueOf(55100)); // 100 + 50000 + 5000
        
        verify(desireItemRecordRepository, times(3)).save(any(DesireItemRecord.class));
    }

    private DesireItemRecord createDesireItemRecord(Long bossRecordId, DesireItemRequest request) {
        return DesireItemRecord.builder()
                .id(1L)
                .weeklyBossRecordId(bossRecordId)
                .characterId(characterId)
                .desireItemId(request.getDesireItemId())
                .salePrice(request.getSalePrice())
                .build();
    }
} 