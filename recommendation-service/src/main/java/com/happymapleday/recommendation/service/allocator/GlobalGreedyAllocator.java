package com.happymapleday.recommendation.service.allocator;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.CharacterInput;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.SelectedBoss;
import com.happymapleday.recommendation.service.eligibility.BossEligibilityService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GlobalGreedyAllocator {

    private final BossEligibilityService eligibilityService;

    public GlobalGreedyAllocator(BossEligibilityService eligibilityService) {
        this.eligibilityService = eligibilityService;
    }

    public void allocate(List<CharacterInput> characters,
                         List<BossResponse> weeklyActive,
                         Map<Long, Long> characterMaxSoloPrice,
                         Map<Long, Set<String>> takenGroupByChar,
                         List<CharacterRecommendation> characterRecs,
                         int characterLimit,
                         int worldLimit,
                         int[] worldSelectedCount,
                         long[] worldCrystal) {

        Map<Long, BossResponse> nextCandidateByChar = new HashMap<>();
        for (int i = 0; i < characters.size(); i++) {
            CharacterInput c = characters.get(i);
            CharacterRecommendation rec = characterRecs.get(i);
            if (rec.getSelectedBossCount() >= characterLimit) continue;
            long maxSolo = characterMaxSoloPrice.getOrDefault(c.getCharacterId(), 0L);
            if (maxSolo == 0L) continue;
            Set<String> takenGroup = takenGroupByChar.get(c.getCharacterId());
            Optional<BossResponse> cand = weeklyActive.stream()
                    .filter(b -> !takenGroup.contains(b.getBossName()))
                    .filter(b -> eligibilityService.canChallenge(b, c))
                    .filter(b -> b.getCrystalPrice() <= maxSolo)
                    .findFirst();
            cand.ifPresent(b -> nextCandidateByChar.put(c.getCharacterId(), b));
        }

        while (worldSelectedCount[0] < worldLimit) {
            Optional<Map.Entry<Long, BossResponse>> best = nextCandidateByChar.entrySet().stream()
                    .max(Comparator.comparingLong(e -> Optional.ofNullable(e.getValue().getCrystalPrice()).orElse(0L)));
            if (best.isEmpty()) break;
            Long chosenCharId = best.get().getKey();
            BossResponse b = best.get().getValue();

            int idx = -1;
            for (int i = 0; i < characters.size(); i++) {
                if (Objects.equals(characters.get(i).getCharacterId(), chosenCharId)) { idx = i; break; }
            }
            if (idx < 0) { nextCandidateByChar.remove(chosenCharId); continue; }

            CharacterRecommendation rec = characterRecs.get(idx);
            if (rec.getSelectedBossCount() >= characterLimit) { nextCandidateByChar.remove(chosenCharId); continue; }

            Set<String> takenGroup = takenGroupByChar.get(chosenCharId);
            SelectedBoss sb = SelectedBoss.builder()
                    .bossId(b.getBossId())
                    .bossName(b.getBossName())
                    .difficulty(b.getDifficulty())
                    .crystalPrice(b.getCrystalPrice())
                    .partySize(1)
                    .forcedIncluded(false)
                    .build();
            List<SelectedBoss> newBosses = new ArrayList<>(rec.getBosses());
            newBosses.add(sb);
            characterRecs.set(idx, CharacterRecommendation.builder()
                    .characterId(rec.getCharacterId())
                    .selectedBossCount(rec.getSelectedBossCount() + 1)
                    .crystalIncome(rec.getCrystalIncome() + b.getCrystalPrice())
                    .bosses(newBosses)
                    .build());
            takenGroup.add(b.getBossName());
            takenGroupByChar.put(chosenCharId, takenGroup);
            worldSelectedCount[0]++;
            worldCrystal[0] += b.getCrystalPrice();

            long maxSolo = characterMaxSoloPrice.getOrDefault(chosenCharId, 0L);
            if (maxSolo == 0L || characterRecs.get(idx).getSelectedBossCount() >= characterLimit) {
                nextCandidateByChar.remove(chosenCharId);
            } else {
                Set<String> tk = takenGroupByChar.get(chosenCharId);
                CharacterInput c = characters.get(idx);
                Optional<BossResponse> cand = weeklyActive.stream()
                        .filter(x -> !tk.contains(x.getBossName()))
                        .filter(x -> eligibilityService.canChallenge(x, c))
                        .filter(x -> x.getCrystalPrice() <= maxSolo)
                        .findFirst();
                if (cand.isPresent()) nextCandidateByChar.put(chosenCharId, cand.get());
                else nextCandidateByChar.remove(chosenCharId);
            }
        }
    }
}


