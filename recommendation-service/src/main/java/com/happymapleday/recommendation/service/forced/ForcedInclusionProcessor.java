package com.happymapleday.recommendation.service.forced;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.CharacterInput;
import com.happymapleday.recommendation.dto.request.PlannedBoss;
import com.happymapleday.recommendation.dto.response.SelectedBoss;
import com.happymapleday.recommendation.service.eligibility.BossEligibilityService;
import com.happymapleday.recommendation.util.DifficultyRanker;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ForcedInclusionProcessor {

    private final BossEligibilityService eligibilityService;

    public ForcedInclusionProcessor(BossEligibilityService eligibilityService) {
        this.eligibilityService = eligibilityService;
    }

    public List<SelectedBoss> process(CharacterInput character,
                                      Map<Long, BossResponse> bossById,
                                      Set<String> takenGroup,
                                      int characterLimit,
                                      int worldLimit,
                                      int[] worldSelectedCount,
                                      long[] worldCrystal) {
        List<SelectedBoss> selected = new ArrayList<>();
        List<PlannedBoss> planned = Optional.ofNullable(character.getPlannedBosses()).orElseGet(List::of);
        List<PlannedBoss> forced = planned.stream()
                .filter(p -> Boolean.TRUE.equals(p.getAlreadyCleared()) || (p.getPartySize() != null && p.getPartySize() >= 2))
                .toList();

        Map<String, PlannedBoss> hardestForcedByName = new HashMap<>();
        for (PlannedBoss pb : forced) {
            BossResponse boss = bossById.get(pb.getBossId());
            if (boss == null) continue;
            String name = boss.getBossName();
            PlannedBoss existing = hardestForcedByName.get(name);
            if (existing == null) {
                hardestForcedByName.put(name, pb);
            } else {
                BossResponse existingBoss = bossById.get(existing.getBossId());
                if (existingBoss == null) {
                    hardestForcedByName.put(name, pb);
                } else {
                    int existingRank = DifficultyRanker.rank(existingBoss.getDifficulty());
                    int candidateRank = DifficultyRanker.rank(boss.getDifficulty());
                    if (candidateRank > existingRank) {
                        hardestForcedByName.put(name, pb);
                    } else if (candidateRank == existingRank) {
                        long existingPrice = Optional.ofNullable(existingBoss.getCrystalPrice()).orElse(0L);
                        long candidatePrice = Optional.ofNullable(boss.getCrystalPrice()).orElse(0L);
                        if (candidatePrice > existingPrice) {
                            hardestForcedByName.put(name, pb);
                        }
                    }
                }
            }
        }

        for (PlannedBoss pb : hardestForcedByName.values()) {
            if (worldSelectedCount[0] >= worldLimit) break;
            if (selected.size() >= characterLimit) break;
            BossResponse boss = bossById.get(pb.getBossId());
            if (!eligibilityService.canChallenge(boss, character)) continue;
            String groupKey = boss.getBossName();
            if (takenGroup.contains(groupKey)) continue;
            int partySize = Optional.ofNullable(pb.getPartySize()).orElse(1);
            if (partySize <= 0) partySize = 1;
            long perPlayerCrystal = Optional.ofNullable(boss.getCrystalPrice()).orElse(0L) / partySize;
            selected.add(SelectedBoss.builder()
                    .bossId(boss.getBossId())
                    .bossName(boss.getBossName())
                    .difficulty(boss.getDifficulty())
                    .crystalPrice(perPlayerCrystal)
                    .partySize(partySize)
                    .forcedIncluded(true)
                    .build());
            takenGroup.add(groupKey);
            worldSelectedCount[0]++;
            worldCrystal[0] += perPlayerCrystal;
        }

        return selected;
    }
}


