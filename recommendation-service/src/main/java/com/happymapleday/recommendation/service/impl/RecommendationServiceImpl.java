package com.happymapleday.recommendation.service.impl;

import com.happymapleday.common.client.BossServiceClient;
import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.CharacterInput;
import com.happymapleday.recommendation.dto.request.OptimizeRecommendationRequest;
import com.happymapleday.recommendation.dto.request.PlannedBoss;
import com.happymapleday.recommendation.dto.response.*;
import com.happymapleday.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private static final int CHARACTER_LIMIT = 12;
    private static final int WORLD_LIMIT = 90;

    private final BossServiceClient bossServiceClient;

    @Override
    public OptimizedRecommendationResponse optimize(OptimizeRecommendationRequest request) {
        ApiResponse<List<BossResponse>> bossListResp = bossServiceClient.getBossList();
        List<BossResponse> allBosses = bossListResp.getData();
        Map<Long, BossResponse> bossById = allBosses.stream().collect(Collectors.toMap(BossResponse::getBossId, Function.identity()));

        Map<String, List<CharacterInput>> worldToCharacters = request.getCharacters().stream()
                .collect(Collectors.groupingBy(CharacterInput::getWorldName));

        List<WorldRecommendation> worldRecommendations = new ArrayList<>();
        long grandTotalCrystal = 0L;
        int grandTotalBossCount = 0;

        for (Map.Entry<String, List<CharacterInput>> entry : worldToCharacters.entrySet()) {
            String worldName = entry.getKey();
            List<CharacterInput> characters = entry.getValue();

            // 캐릭터별 선택 결과
            List<CharacterRecommendation> characterRecs = new ArrayList<>();
            int worldSelectedCount = 0;
            long worldCrystal = 0L;

            // 각 캐릭터별 강제 포함 보스 우선 선택 (규칙 11, 12)
            Map<Long, Set<String>> characterTakenBossGroup = new HashMap<>(); // bossName -> taken difficulty group key per character

            // 먼저 모든 캐릭터의 강제 보스를 담음
            for (CharacterInput character : characters) {
                List<SelectedBoss> selected = new ArrayList<>();
                Set<String> takenGroup = new HashSet<>();

                List<PlannedBoss> planned = Optional.ofNullable(character.getPlannedBosses()).orElseGet(List::of);
                // 파티 2 이상 또는 이미 클리어 강제 포함
                List<PlannedBoss> forced = planned.stream()
                        .filter(p -> Boolean.TRUE.equals(p.getAlreadyCleared()) || (p.getPartySize() != null && p.getPartySize() >= 2))
                        .toList();

                // 같은 보스 다난이도 강제 포함 시 가장 어려운 난이도만 선택
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
                            int existingRank = difficultyRank(existingBoss.getDifficulty());
                            int candidateRank = difficultyRank(boss.getDifficulty());
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
                    if (worldSelectedCount >= WORLD_LIMIT) break;
                    if (selected.size() >= CHARACTER_LIMIT) break;
                    BossResponse boss = bossById.get(pb.getBossId());
                    if (!canChallenge(boss, character)) continue;
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
                    worldSelectedCount++;
                    worldCrystal += perPlayerCrystal;
                }
                characterTakenBossGroup.put(character.getCharacterId(), takenGroup);
                characterRecs.add(CharacterRecommendation.builder()
                        .characterId(character.getCharacterId())
                        .selectedBossCount(selected.size())
                        .crystalIncome(selected.stream().mapToLong(SelectedBoss::getCrystalPrice).sum())
                        .bosses(selected)
                        .build());
            }

            // 규칙 13-14: 사용자가 입력한 1인 보스 중 가장 비싼 보스의 가격을 기준으로 동일/이하 가격은 잡을 수 있다고 가정
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

            // 나머지 슬롯은 월드/캐릭터 제한 하에서 높은 가격 우선 그리디로 채움 (규칙 1,2,3,6,7,8,9,13,14,15,16)
            // 캐릭터 순회하며 가능한 보스 후보를 가격 내림차순으로 시도
            // 난이도 그룹 충돌 방지: 동일 보스명은 1회만 선택
            List<BossResponse> weeklyActive = allBosses.stream()
                    .filter(b -> !Boolean.TRUE.equals(b.getIsMonthly()))
                    .sorted(Comparator.comparingLong(BossResponse::getCrystalPrice).reversed())
                    .toList();

            // 전역 그리디: 각 캐릭터의 다음 최고가 후보를 계산하고, 그 중 최댓값을 가진 캐릭터/보스를 선택해 배정
            boolean filledWorld = false;
            // 사전 계산: 캐릭터별 다음 후보 보스
            Map<Long, BossResponse> nextCandidateByChar = new HashMap<>();
            for (int i = 0; i < characters.size(); i++) {
                CharacterInput c = characters.get(i);
                CharacterRecommendation rec = characterRecs.get(i);
                if (rec.getSelectedBossCount() >= CHARACTER_LIMIT) continue;
                long maxSolo = characterMaxSoloPrice.getOrDefault(c.getCharacterId(), 0L);
                if (maxSolo == 0L) continue; // 1인 계획 없으면 추가 채우지 않음
                Set<String> takenGroup = characterTakenBossGroup.get(c.getCharacterId());
                Optional<BossResponse> cand = weeklyActive.stream()
                        .filter(b -> !takenGroup.contains(b.getBossName()))
                        .filter(b -> canChallenge(b, c))
                        .filter(b -> b.getCrystalPrice() <= maxSolo)
                        .findFirst();
                cand.ifPresent(b -> nextCandidateByChar.put(c.getCharacterId(), b));
            }

            while (!filledWorld) {
                if (worldSelectedCount >= WORLD_LIMIT) { filledWorld = true; break; }
                // 최고가 후보 찾기
                Optional<Map.Entry<Long, BossResponse>> best = nextCandidateByChar.entrySet().stream()
                        .max(Comparator.comparingLong(e -> Optional.ofNullable(e.getValue().getCrystalPrice()).orElse(0L)));
                if (best.isEmpty()) {
                    break; // 더 이상 배정할 후보 없음
                }
                Long chosenCharId = best.get().getKey();
                BossResponse b = best.get().getValue();
                // 해당 캐릭터 인덱스 찾기
                int idx = -1;
                for (int i = 0; i < characters.size(); i++) {
                    if (Objects.equals(characters.get(i).getCharacterId(), chosenCharId)) { idx = i; break; }
                }
                if (idx < 0) {
                    nextCandidateByChar.remove(chosenCharId);
                    continue;
                }
                CharacterInput character = characters.get(idx);
                CharacterRecommendation rec = characterRecs.get(idx);
                if (rec.getSelectedBossCount() >= CHARACTER_LIMIT) {
                    nextCandidateByChar.remove(chosenCharId);
                    continue;
                }
                Set<String> takenGroup = characterTakenBossGroup.get(chosenCharId);
                // 배정
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
                characterTakenBossGroup.put(chosenCharId, takenGroup);
                worldSelectedCount++;
                worldCrystal += b.getCrystalPrice();
                if (worldSelectedCount >= WORLD_LIMIT) { filledWorld = true; break; }

                // 해당 캐릭터의 다음 후보 업데이트
                long maxSolo = characterMaxSoloPrice.getOrDefault(chosenCharId, 0L);
                if (maxSolo == 0L || characterRecs.get(idx).getSelectedBossCount() >= CHARACTER_LIMIT) {
                    nextCandidateByChar.remove(chosenCharId);
                } else {
                    Set<String> tk = characterTakenBossGroup.get(chosenCharId);
                    Optional<BossResponse> cand = weeklyActive.stream()
                            .filter(x -> !tk.contains(x.getBossName()))
                            .filter(x -> canChallenge(x, character))
                            .filter(x -> x.getCrystalPrice() <= maxSolo)
                            .findFirst();
                    if (cand.isPresent()) {
                        nextCandidateByChar.put(chosenCharId, cand.get());
                    } else {
                        nextCandidateByChar.remove(chosenCharId);
                    }
                }
            }

            worldRecommendations.add(WorldRecommendation.builder()
                    .worldName(worldName)
                    .worldBossCount(worldSelectedCount)
                    .worldCrystalIncome(worldCrystal)
                    .characters(characterRecs)
                    .build());
            grandTotalCrystal += worldCrystal;
            grandTotalBossCount += worldSelectedCount;
        }

        return OptimizedRecommendationResponse.builder()
                .worlds(worldRecommendations)
                .totalCrystalIncome(grandTotalCrystal)
                .totalBossCount(grandTotalBossCount)
                .build();
    }

    private boolean canChallenge(BossResponse boss, CharacterInput character) {
        if (boss == null) return false;
        if (Boolean.TRUE.equals(boss.getIsMonthly())) return false;
        if (boss.getIsActive() != null && !boss.getIsActive()) return false;
        if (boss.getMinEntryLevel() != null && character.getLevel() != null && character.getLevel() < boss.getMinEntryLevel()) return false;
        // 포스 체크
        if (boss.getRequiredForceType() != null && !"NONE".equalsIgnoreCase(boss.getRequiredForceType())) {
            if ("ARCANE".equalsIgnoreCase(boss.getRequiredForceType())) {
                Integer af = character.getArcaneForce();
                Integer req = boss.getRequiredForceAmount();
                if (af == null) return false;
                if (req != null) {
                    int minAllowed = req / 2; // 절반 미만 제외 => af < floor(req/2) 면 제외
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

    private static int difficultyRank(String difficulty) {
        if (difficulty == null) return 0;
        String d = difficulty.trim().toLowerCase(Locale.ROOT);
        return switch (d) {
            case "easy", "이지" -> 1;
            case "normal", "노말" -> 2;
            case "hard", "하드" -> 3;
            case "chaos", "카오스" -> 4;
            case "extreme", "익스트림" -> 5;
            default -> 0;
        };
    }
}


