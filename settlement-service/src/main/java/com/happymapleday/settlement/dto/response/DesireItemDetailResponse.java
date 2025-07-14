package com.happymapleday.settlement.dto.response;

import com.happymapleday.settlement.entity.DesireItemRecord;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Builder
public class DesireItemDetailResponse {
    private final Long desireItemId;
    private final String itemName;
    private final BigInteger salePrice;
    private final LocalDateTime acquiredAt;
    
    public static DesireItemDetailResponse from(DesireItemRecord record) {
        return DesireItemDetailResponse.builder()
                .desireItemId(record.getDesireItemId())
                .itemName("물욕템" + record.getDesireItemId()) // 실제로는 Boss Service에서 가져와야 함
                .salePrice(record.getSalePrice())
                .acquiredAt(record.getAcquiredAt())
                .build();
    }
} 