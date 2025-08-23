package com.happymapleday.recommendation.service.allocator;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.CharacterInput;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.SelectedBoss;
import com.happymapleday.recommendation.service.eligibility.BossEligibilityService;
import com.happymapleday.recommendation.service.weight.BossWeightCache;
import com.happymapleday.recommendation.service.model.WorldAccumulator;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GlobalGreedyAllocator {

    private final BossEligibilityService eligibilityService;
    private final BossWeightCache bossWeightCache;

    public GlobalGreedyAllocator(BossEligibilityService eligibilityService, BossWeightCache bossWeightCache) {
        this.eligibilityService = eligibilityService;
        this.bossWeightCache = bossWeightCache;
    }

    public void allocate(List<CharacterInput> characters,
                         List<BossResponse> weeklyActive,
                         Map<Long, Long> characterMaxSoloPrice,
                         Map<Long, Set<String>> takenGroupByChar,
                         List<CharacterRecommendation> characterRecs,
                         int characterLimit,
                         int worldLimit,
                         WorldAccumulator worldAcc) {

        // 캐릭터별 추천 객체 맵 구성 (없으면 생성)
        Map<Long, Integer> positionByCharId = new HashMap<>();
        for (int j = 0; j < characterRecs.size(); j++) {
            positionByCharId.put(characterRecs.get(j).getCharacterId(), j);
        }
        for (CharacterInput c : characters) {
            long cid = c.getCharacterId();
            if (!positionByCharId.containsKey(cid)) {
                characterRecs.add(CharacterRecommendation.builder()
                        .characterId(cid)
                        .selectedBossCount(0)
                        .crystalIncome(0L)
                        .bosses(new ArrayList<>())
                        .build());
                positionByCharId.put(cid, characterRecs.size() - 1);
            }
        }

        // 초기 후보를 우선순위 큐로 구성
        record Candidate(long characterId, BossResponse boss, double score) {}
        PriorityQueue<Candidate> pq = new PriorityQueue<>(Comparator.comparingDouble((Candidate c) -> c.score).reversed());

        for (int i = 0; i < characters.size(); i++) {
            CharacterInput c = characters.get(i);
            CharacterRecommendation rec = characterRecs.get(positionByCharId.get(c.getCharacterId()));
            if (rec.getSelectedBossCount() >= characterLimit) continue;
            long maxSolo = characterMaxSoloPrice.getOrDefault(c.getCharacterId(), 0L);
            if (maxSolo == 0L) continue;
            Set<String> takenGroup = takenGroupByChar.computeIfAbsent(c.getCharacterId(), k -> new HashSet<>());
            Optional<BossResponse> cand = weeklyActive.stream()
                    .filter(b -> !takenGroup.contains(b.getBossName()))
                    .filter(b -> eligibilityService.canChallenge(b, c))
                    .filter(b -> weightedPrice(b) <= maxSolo)
                    .findFirst();
            cand.ifPresent(b -> pq.offer(new Candidate(c.getCharacterId(), b, weightedPrice(b))));
        }

        while (worldAcc.getSelectedCount() < worldLimit && !pq.isEmpty()) {
            Candidate best = pq.poll();
            Long chosenCharId = best.characterId;
            BossResponse b = best.boss;

            Integer idxObj = positionByCharId.get(chosenCharId);
            if (idxObj == null) {
                characterRecs.add(CharacterRecommendation.builder()
                        .characterId(chosenCharId)
                        .selectedBossCount(0)
                        .crystalIncome(0L)
                        .bosses(new ArrayList<>())
                        .build());
                idxObj = characterRecs.size() - 1;
                positionByCharId.put(chosenCharId, idxObj);
            }
            int idx = idxObj;

            CharacterRecommendation rec = characterRecs.get(idx);
            if (rec.getSelectedBossCount() >= characterLimit) continue;

            Set<String> takenGroup = takenGroupByChar.computeIfAbsent(chosenCharId, k -> new HashSet<>());
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
            Set<String> tk = takenGroupByChar.computeIfAbsent(chosenCharId, k -> new HashSet<>());
            CharacterInput found = null;
            for (CharacterInput ci : characters) {
                if (Objects.equals(ci.getCharacterId(), chosenCharId)) { found = ci; break; }
            }
            if (found == null) continue;
            final CharacterInput finalFound = found;
            Optional<BossResponse> cand = weeklyActive.stream()
                    .filter(x -> !tk.contains(x.getBossName()))
                    .filter(x -> eligibilityService.canChallenge(x, finalFound))
                    .filter(x -> weightedPrice(x) <= maxSolo)
                    .findFirst();
            cand.ifPresent(nb -> pq.offer(new Candidate(chosenCharId, nb, weightedPrice(nb))));
        }
    }

    private double weightedPrice(BossResponse boss) {
        long price = boss.getCrystalPrice() == null ? 0L : boss.getCrystalPrice();
        double w = bossWeightCache.getWeight(boss.getBossId());
        return price * w;
    }
}


