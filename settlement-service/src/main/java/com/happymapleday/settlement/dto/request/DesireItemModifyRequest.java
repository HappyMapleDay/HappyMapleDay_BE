package com.happymapleday.settlement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class DesireItemModifyRequest {
    private final Long recordId;
    
    @NotNull(message = "물욕템 ID는 필수입니다.")
    @Positive(message = "물욕템 ID는 양수여야 합니다.")
    private final Long desireItemId;

    @NotNull(message = "캐릭터 ID는 필수입니다.")
    @Positive(message = "캐릭터 ID는 양수여야 합니다.")
    private final Long characterId;
    
    @NotNull(message = "판매 가격은 필수입니다.")
    @DecimalMin(value = "1", message = "판매 가격은 1 이상이어야 합니다.")
    private final BigInteger salePrice;
} 