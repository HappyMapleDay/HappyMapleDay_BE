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
                         com.happymapleday.recommendation.service.model.WorldAccumulator worldAcc) {

        // 초기 후보를 우선순위 큐로 구성
        record Candidate(long characterId, BossResponse boss, long price) {}
        PriorityQueue<Candidate> pq = new PriorityQueue<>(Comparator.comparingLong((Candidate c) -> c.price).reversed());

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
            cand.ifPresent(b -> pq.offer(new Candidate(c.getCharacterId(), b, Optional.ofNullable(b.getCrystalPrice()).orElse(0L))));
        }

        while (worldAcc.getSelectedCount() < worldLimit && !pq.isEmpty()) {
            Candidate best = pq.poll();
            Long chosenCharId = best.characterId;
            BossResponse b = best.boss;

            int idx = -1;
            for (int i = 0; i < characters.size(); i++) {
                if (Objects.equals(characters.get(i).getCharacterId(), chosenCharId)) { idx = i; break; }
            }
            if (idx < 0) continue;

            CharacterRecommendation rec = characterRecs.get(idx);
            if (rec.getSelectedBossCount() >= characterLimit) continue;

            Set<String> takenGroup = takenGroupByChar.get(chosenCharId);
            if (takenGroup.contains(b.getBossName())) continue;

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
                    .crystalIncome(rec.getCrystalIncome() + Optional.ofNullable(b.getCrystalPrice()).orElse(0L))
                    .bosses(newBosses)
                    .build());
            takenGroup.add(b.getBossName());
            takenGroupByChar.put(chosenCharId, takenGroup);
            worldAcc.incrementSelected();
            worldAcc.addCrystal(Optional.ofNullable(b.getCrystalPrice()).orElse(0L));

            long maxSolo = characterMaxSoloPrice.getOrDefault(chosenCharId, 0L);
            if (maxSolo == 0L || characterRecs.get(idx).getSelectedBossCount() >= characterLimit) {
                continue;
            }
            Set<String> tk = takenGroupByChar.get(chosenCharId);
            CharacterInput c = characters.get(idx);
            Optional<BossResponse> cand = weeklyActive.stream()
                    .filter(x -> !tk.contains(x.getBossName()))
                    .filter(x -> eligibilityService.canChallenge(x, c))
                    .filter(x -> x.getCrystalPrice() <= maxSolo)
                    .findFirst();
            cand.ifPresent(nb -> pq.offer(new Candidate(chosenCharId, nb, Optional.ofNullable(nb.getCrystalPrice()).orElse(0L))));
        }
    }
}


