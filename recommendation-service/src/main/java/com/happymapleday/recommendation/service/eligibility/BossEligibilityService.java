package com.happymapleday.recommendation.service.eligibility;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.CharacterInput;
import org.springframework.stereotype.Component;

@Component
public class BossEligibilityService {

    public boolean canChallenge(BossResponse boss, CharacterInput character) {
        if (boss == null) return false;
        if (Boolean.TRUE.equals(boss.getIsMonthly())) return false;
        if (boss.getIsActive() != null && !boss.getIsActive()) return false;
        if (boss.getMinEntryLevel() != null && character.getLevel() != null && character.getLevel() < boss.getMinEntryLevel()) return false;
        if (boss.getRequiredForceType() != null && !"NONE".equalsIgnoreCase(boss.getRequiredForceType())) {
            if ("ARCANE".equalsIgnoreCase(boss.getRequiredForceType())) {
                Integer af = character.getArcaneForce();
                Integer req = boss.getRequiredForceAmount();
                if (af == null) return false;
                if (req != null) {
                    int minAllowed = req / 2; // 절반 미만 제외
                    if (af < minAllowed) return false;
                }
            } else if ("AUTHENTIC".equalsIgnoreCase(boss.getRequiredForceType())) {
                Integer of = character.getAuthenticForce();
                Integer req = boss.getRequiredForceAmount();
                if (of == null) return false;
                if (req != null) {
                    int minAllowed = Math.max(0, req - 50); // 요구치-50 미만 제외
                    if (of < minAllowed) return false;
                }
            }
        }
        return true;
    }
}


