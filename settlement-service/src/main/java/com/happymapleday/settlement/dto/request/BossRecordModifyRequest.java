package com.happymapleday.settlement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigInteger;
import java.util.List;

public class BossRecordModifyRequest {
    
    @NotNull(message = "보스 기록 ID는 필수입니다.")
    @Positive(message = "보스 기록 ID는 양수여야 합니다.")
    private Long weeklyBossRecordId;
    
    @NotNull(message = "캐릭터 ID는 필수입니다.")
    @Positive(message = "캐릭터 ID는 양수여야 합니다.")
    private Long characterId;
    
    @NotNull(message = "보스 ID는 필수입니다.")
    @Positive(message = "보스 ID는 양수여야 합니다.")
    private Long bossId;
    
    @NotNull(message = "파티 크기는 필수입니다.")
    @Min(value = 1, message = "파티 크기는 최소 1명입니다.")
    @Max(value = 6, message = "파티 크기는 최대 6명입니다.")
    private Integer partySize;
    
    @NotNull(message = "크리스탈 수익은 필수입니다.")
    @DecimalMin(value = "0", message = "크리스탈 수익은 0 이상이어야 합니다.")
    private BigInteger crystalIncome;
    
    @Valid
    private List<DesireItemModifyRequest> desireItems;
    
    // 기본 생성자
    public BossRecordModifyRequest() {}
    
    // 생성자
    public BossRecordModifyRequest(Long weeklyBossRecordId, Long characterId, Long bossId, 
                                 Integer partySize, BigInteger crystalIncome, 
                                 List<DesireItemModifyRequest> desireItems) {
        this.weeklyBossRecordId = weeklyBossRecordId;
        this.characterId = characterId;
        this.bossId = bossId;
        this.partySize = partySize;
        this.crystalIncome = crystalIncome;
        this.desireItems = desireItems;
    }
    
    // Getter/Setter
    public Long getWeeklyBossRecordId() {
        return weeklyBossRecordId;
    }
    
    public void setWeeklyBossRecordId(Long weeklyBossRecordId) {
        this.weeklyBossRecordId = weeklyBossRecordId;
    }
    
    public Long getCharacterId() {
        return characterId;
    }
    
    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }
    
    public Long getBossId() {
        return bossId;
    }
    
    public void setBossId(Long bossId) {
        this.bossId = bossId;
    }
    
    public Integer getPartySize() {
        return partySize;
    }
    
    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }
    
    public BigInteger getCrystalIncome() {
        return crystalIncome;
    }
    
    public void setCrystalIncome(BigInteger crystalIncome) {
        this.crystalIncome = crystalIncome;
    }
    
    public List<DesireItemModifyRequest> getDesireItems() {
        return desireItems;
    }
    
    public void setDesireItems(List<DesireItemModifyRequest> desireItems) {
        this.desireItems = desireItems;
    }
} 