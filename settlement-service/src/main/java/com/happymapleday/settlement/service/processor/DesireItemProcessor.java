package com.happymapleday.settlement.service.processor;

import com.happymapleday.settlement.dto.request.DesireItemRequest;
import com.happymapleday.settlement.entity.DesireItemRecord;
import com.happymapleday.settlement.repository.DesireItemRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DesireItemProcessor {
    
    private final DesireItemRecordRepository desireItemRecordRepository;
    
    // 물욕템 기록 처리 및 총 수입 계산
    public BigInteger processDesireItems(Long bossRecordId, Long characterId, List<DesireItemRequest> desireItems) {
        if (desireItems == null || desireItems.isEmpty()) {
            return BigInteger.ZERO;
        }
        
        BigInteger totalDesireItemIncome = BigInteger.ZERO;
        
        for (DesireItemRequest desireRequest : desireItems) {
            DesireItemRecord desireRecord = DesireItemRecord.builder()
                    .weeklyBossRecordId(bossRecordId)
                    .characterId(characterId)
                    .desireItemId(desireRequest.getDesireItemId())
                    .sourceBoxItemId(desireRequest.getSourceBoxItemId())
                    .salePrice(desireRequest.getSalePrice())
                    .build();
            
            desireItemRecordRepository.save(desireRecord);
            totalDesireItemIncome = totalDesireItemIncome.add(desireRequest.getSalePrice());
        }
        
        return totalDesireItemIncome;
    }
    
    // 물욕템 기록 삭제
    public void deleteDesireItemsByBossRecordId(Long bossRecordId) {
        desireItemRecordRepository.deleteByWeeklyBossRecordId(bossRecordId);
    }
} 