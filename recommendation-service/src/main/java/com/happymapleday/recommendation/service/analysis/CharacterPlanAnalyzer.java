package com.happymapleday.recommendation.service.analysis;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.CharacterInput;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CharacterPlanAnalyzer {

    public Map<Long, Long> computeMaxSoloPrice(List<CharacterInput> characters, Map<Long, BossResponse> bossById) {
        Map<Long, Long> characterMaxSoloPrice = new HashMap<>();
        for (CharacterInput character : characters) {
            long maxSolo = Optional.ofNullable(character.getPlannedBosses()).orElseGet(List::of).stream()
                    .filter(p -> p.getPartySize() != null && p.getPartySize() == 1)
                    .map(p -> bossById.getOrDefault(p.getBossId(), null))
                    .filter(Objects::nonNull)
                    .mapToLong(BossResponse::getCrystalPrice)
                    .max().orElse(0L);
            characterMaxSoloPrice.put(character.getCharacterId(), maxSolo);
        }
        return characterMaxSoloPrice;
    }
}


