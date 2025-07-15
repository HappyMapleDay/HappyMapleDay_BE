package com.happymapleday.settlement.service.validator;

import com.happymapleday.settlement.entity.WeeklyBossRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CrystalLimitValidator {
    
    // 결정석 제한 검증
    public void validateCrystalLimits(List<WeeklyBossRecord> bossRecords) {
        Set<Long> characterIds = bossRecords.stream()
                .map(WeeklyBossRecord::getCharacterId)
                .collect(Collectors.toSet());
        
        // 캐릭터별 결정석 제한 검증
        for (Long characterId : characterIds) {
            if (WeeklyBossRecord.isCharacterOverCrystalLimit(bossRecords, characterId)) {
                throw new IllegalStateException("캐릭터당 주간 결정석 판매 제한을 초과했습니다.");
            }
        }
        
        // 월드 전체 결정석 제한 검증
        if (WeeklyBossRecord.isWorldOverCrystalLimit(bossRecords)) {
            throw new IllegalStateException("월드 전체 주간 결정석 판매 제한을 초과했습니다.");
        }
    }
} 