package com.happymapleday.settlement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigInteger;

public class DesireItemRequest {
    
    @NotNull(message = "물욕템 ID는 필수입니다.")
    @Positive(message = "물욕템 ID는 양수여야 합니다.")
    private Long desireItemId;
    
    @NotNull(message = "판매 가격은 필수입니다.")
    @DecimalMin(value = "1", message = "판매 가격은 1 이상이어야 합니다.")
    private BigInteger salePrice;
    
    // 기본 생성자
    public DesireItemRequest() {}
    
    // 생성자
    public DesireItemRequest(Long desireItemId, BigInteger salePrice) {
        this.desireItemId = desireItemId;
        this.salePrice = salePrice;
    }
    
    // Getter/Setter
    public Long getDesireItemId() {
        return desireItemId;
    }
    
    public void setDesireItemId(Long desireItemId) {
        this.desireItemId = desireItemId;
    }
    
    public BigInteger getSalePrice() {
        return salePrice;
    }
    
    public void setSalePrice(BigInteger salePrice) {
        this.salePrice = salePrice;
    }
} 