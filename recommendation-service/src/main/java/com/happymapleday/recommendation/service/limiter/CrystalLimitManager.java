package com.happymapleday.recommendation.service.limiter;

import com.happymapleday.recommendation.service.constants.OptimizationConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrystalLimitManager {
    // 제한 체크 유틸 클래스로 역할 축소 (필요 시 확장)

    // 캐릭터별 결정석 제한 체크
    public boolean isCharacterLimitReached(int currentCrystalCount) {
        return currentCrystalCount >= OptimizationConstants.CHARACTER_CRYSTAL_LIMIT;
    }

    // 전체 결정석 제한 체크
    public boolean isWorldLimitReached(int currentGlobalCrystalCount) {
        return currentGlobalCrystalCount >= OptimizationConstants.WORLD_CRYSTAL_LIMIT;
    }
}