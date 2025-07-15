package com.happymapleday.settlement.dto.response;

import com.happymapleday.settlement.entity.WeeklyBossRecord;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class BossRecordDetailResponse {
    private final Long bossRecordId;
    private final Long characterId;
    private final String characterName;
    private final Long bossId;
    private final String bossName;
    private final String difficulty;
    private final Integer partySize;
    private final BigInteger crystalIncome;
    private final BigInteger desireItemIncome;
    private final BigInteger totalIncome;
    private final List<DesireItemDetailResponse> desireItems;
    
    public static BossRecordDetailResponse from(WeeklyBossRecord record) {
        List<DesireItemDetailResponse> desireItems = record.getDesireItemRecords() != null 
            ? record.getDesireItemRecords().stream()
                .map(DesireItemDetailResponse::from)
                .collect(Collectors.toList())
            : List.of();
            
        return BossRecordDetailResponse.builder()
                .bossRecordId(record.getId())
                .characterId(record.getCharacterId())
                .characterName("캐릭터" + record.getCharacterId()) // 실제로는 Character Service에서 가져와야 함
                .bossId(record.getBossId())
                .bossName("보스" + record.getBossId()) // 실제로는 Boss Service에서 가져와야 함
                .difficulty("하드") // 실제로는 Boss Service에서 가져와야 함
                .partySize(record.getPartySize())
                .crystalIncome(record.getCrystalIncome())
                .desireItemIncome(record.getDesireItemIncome())
                .totalIncome(record.getTotalIncome())
                .desireItems(desireItems)
                .build();
    }
} 