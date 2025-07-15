package com.happymapleday.settlement.dto.response;

import com.happymapleday.settlement.entity.DesireItemRecord;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class DesireItemDetailResponse {
    
    private final Long desireItemId;
    private final BigInteger salePrice;
    
    public static DesireItemDetailResponse from(DesireItemRecord record) {
        return DesireItemDetailResponse.builder()
                .desireItemId(record.getDesireItemId())
                .salePrice(record.getSalePrice())
                .build();
    }
} 