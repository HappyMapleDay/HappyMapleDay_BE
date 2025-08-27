package com.happymapleday.settlement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Builder
public class BossRecordRequest {
    @NotNull(message = "캐릭터 ID는 필수입니다.")
    @Positive(message = "캐릭터 ID는 양수여야 합니다.")
    private final Long characterId;
    
    @NotNull(message = "보스 ID는 필수입니다.")
    @Positive(message = "보스 ID는 양수여야 합니다.")
    private final Long bossId;
    
    @NotNull(message = "클리어 인원은 필수값입니다.")
    @Min(value = 1, message = "클리어 인원은 최소 1명입니다.")
    @Max(value = 6, message = "클리어 인원은 최대 6명입니다.")
    private final Integer partySize;
    
    @NotNull(message = "결정석 수익은 필수입니다.")
    @DecimalMin(value = "0", message = "결정석 수익은 0 이상이어야 합니다.")
    private final BigInteger crystalIncome;
    
    @Valid
    private final List<DesireItemRequest> desireItems;

    // 정산 검증용: 프론트에서 전달
    @NotNull(message = "캐릭터 레벨은 필수입니다.")
    @Min(value = 1, message = "캐릭터 레벨은 1 이상이어야 합니다.")
    @Max(value = 300, message = "캐릭터 레벨은 300 이하여야 합니다.")
    private final Integer characterLevel;

    private final Integer arcaneForce;

    private final Integer authenticForce;

    // Nexon Open API 스냅샷: 직업명과 전투력
    private final String character_class;
    private final Long combat_power;
} 