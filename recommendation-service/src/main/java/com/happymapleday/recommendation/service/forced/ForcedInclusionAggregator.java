package com.happymapleday.recommendation.service.forced;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.CharacterInput;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.SelectedBoss;
import com.happymapleday.recommendation.service.model.WorldAccumulator;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ForcedInclusionAggregator {

    private final ForcedInclusionProcessor processor;

    public ForcedInclusionAggregator(ForcedInclusionProcessor processor) {
        this.processor = processor;
    }

    public void aggregateAndApply(List<CharacterInput> characters,
                                  Map<Long, BossResponse> bossById,
                                  int characterLimit,
                                  int worldLimit,
                                  List<CharacterRecommendation> characterRecs,
                                  WorldAccumulator acc,
                                  Map<Long, Set<String>> outTakenGroupByChar) {

        List<Map.Entry<Long, SelectedBoss>> forcedAll = new ArrayList<>();
        for (CharacterInput character : characters) {
            List<SelectedBoss> candidates = processor.collectCandidates(character, bossById);
            for (SelectedBoss sb : candidates) {
                forcedAll.add(Map.entry(character.getCharacterId(), sb));
            }
        }
        forcedAll.sort(Comparator.comparingLong((Map.Entry<Long, SelectedBoss> e) -> e.getValue().getCrystalPrice()).reversed());

        Map<Long, Integer> takenCountByChar = new HashMap<>();
        for (CharacterInput c : characters) takenCountByChar.put(c.getCharacterId(), 0);
        Map<Long, Set<String>> takenGroupTmp = new HashMap<>();
        for (CharacterInput c : characters) takenGroupTmp.put(c.getCharacterId(), new HashSet<>());

        for (Map.Entry<Long, SelectedBoss> e : forcedAll) {
            if (acc.getSelectedCount() >= worldLimit) break;
            Long cid = e.getKey();
            SelectedBoss sb = e.getValue();
            if (takenCountByChar.get(cid) >= characterLimit) continue;
            Set<String> tg = takenGroupTmp.get(cid);
            if (tg.contains(sb.getBossName())) continue;

            int idx = -1;
            for (int i = 0; i < characterRecs.size(); i++) {
                if (Objects.equals(characterRecs.get(i).getCharacterId(), cid)) { idx = i; break; }
            }
            if (idx < 0) {
                characterRecs.add(CharacterRecommendation.builder()
                        .characterId(cid)
                        .selectedBossCount(0)
                        .crystalIncome(0L)
                        .bosses(new ArrayList<>())
                        .build());
                idx = characterRecs.size() - 1;
            }

            CharacterRecommendation rec = characterRecs.get(idx);
            List<SelectedBoss> newBosses = new ArrayList<>(rec.getBosses());
            newBosses.add(sb);
            characterRecs.set(idx, CharacterRecommendation.builder()
                    .characterId(rec.getCharacterId())
                    .selectedBossCount(rec.getSelectedBossCount() + 1)
                    .crystalIncome(rec.getCrystalIncome() + sb.getCrystalPrice())
                    .bosses(newBosses)
                    .build());
            acc.incrementSelected();
            acc.addCrystal(sb.getCrystalPrice());
            takenCountByChar.put(cid, takenCountByChar.get(cid) + 1);
            tg.add(sb.getBossName());
        }

        outTakenGroupByChar.putAll(takenGroupTmp);
    }
}


