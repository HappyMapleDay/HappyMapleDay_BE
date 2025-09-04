package com.happymapleday.recommendation.service.analysis;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.CharacterInput;
import com.happymapleday.recommendation.service.weight.BossWeightCache;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CharacterPlanAnalyzer {

    private final BossWeightCache bossWeightCache;

    public CharacterPlanAnalyzer(BossWeightCache bossWeightCache) {
        this.bossWeightCache = bossWeightCache;
    }

    public Map<Long, Long> computeMaxSoloPrice(List<CharacterInput> characters, Map<Long, BossResponse> bossById) {
        Map<Long, Long> characterMaxSoloPrice = new HashMap<>();
        for (CharacterInput character : characters) {
            long maxSolo = Optional.ofNullable(character.getPlannedBosses()).orElseGet(List::of).stream()
                    .filter(p -> p.getPartySize() != null && p.getPartySize() == 1)
                    .map(p -> bossById.getOrDefault(p.getBossId(), null))
                    .filter(Objects::nonNull)
                    .mapToLong(b -> weightedPrice(b))
                    .max().orElse(0L);
            characterMaxSoloPrice.put(character.getCharacterId(), maxSolo);
        }
        return characterMaxSoloPrice;
    }

    private long weightedPrice(BossResponse boss) {
        long price = boss.getCrystalPrice() == null ? 0L : boss.getCrystalPrice();
        double w = bossWeightCache.getWeight(boss.getBossId());
        double v = price * w;
        if (v < 0) return 0L;
        if (v > Long.MAX_VALUE) return Long.MAX_VALUE;
        return (long) v;
    }
}


