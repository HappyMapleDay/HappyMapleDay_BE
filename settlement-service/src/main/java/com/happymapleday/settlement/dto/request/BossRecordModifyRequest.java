package com.happymapleday.settlement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Builder
public class BossRecordModifyRequest {
    
    @NotNull(message = "수정할 보스 기록의 ID는 필수입니다.")
    @Positive(message = "보스 기록 ID는 양수여야 합니다.")
    private final Long weeklyBossRecordId;
    
    @NotNull(message = "캐릭터 ID는 필수입니다.")
    @Positive(message = "캐릭터 ID는 양수여야 합니다.")
    private final Long characterId;
    
    @NotNull(message = "보스 ID는 필수입니다.")
    @Positive(message = "보스 ID는 양수여야 합니다.")
    private final Long bossId;
    
    @NotNull(message = "파티 인원수는 필수입니다.")
    @Positive(message = "파티 인원수는 양수여야 합니다.")
    private final Integer partySize;
    
    @NotNull(message = "결정석 수익은 필수입니다.")
    @DecimalMin(value = "0", message = "크리스탈 수익은 0 이상이어야 합니다.")
    private final BigInteger crystalIncome;
    
    @Valid
    private final List<DesireItemModifyRequest> desireItems;
} 