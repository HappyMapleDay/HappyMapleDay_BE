package com.happymapleday.recommendation.service.impl;

import com.happymapleday.common.client.BossServiceClient;
import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.CharacterInput;
import com.happymapleday.recommendation.dto.request.OptimizeRecommendationRequest;
import com.happymapleday.recommendation.dto.request.PlannedBoss;
import com.happymapleday.recommendation.dto.response.*;
import com.happymapleday.recommendation.service.RecommendationService;
import com.happymapleday.recommendation.service.allocator.GlobalGreedyAllocator;
import com.happymapleday.recommendation.service.forced.ForcedInclusionProcessor;
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
    private final ForcedInclusionProcessor forcedInclusionProcessor;
    private final GlobalGreedyAllocator globalGreedyAllocator;

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
            int[] worldSelectedRef = new int[]{0};
            long[] worldCrystalRef = new long[]{0L};

            // 각 캐릭터별 우선적으로 포함 되어야 할 보스 선택
            Map<Long, Set<String>> characterTakenBossGroup = new HashMap<>(); // bossName -> taken difficulty group key per character

            for (CharacterInput character : characters) {
                List<SelectedBoss> selected = new ArrayList<>();
                Set<String> takenGroup = new HashSet<>();

                List<PlannedBoss> planned = Optional.ofNullable(character.getPlannedBosses()).orElseGet(List::of);
                // 2인 이상 클리어, 이미 클리어 한 보스 포함
                planned.stream()
                        .filter(p -> Boolean.TRUE.equals(p.getAlreadyCleared()) || (p.getPartySize() != null && p.getPartySize() >= 2))
                        .toList();

                List<SelectedBoss> forcedSelected =
                        forcedInclusionProcessor.process(
                                character,
                                bossById,
                                takenGroup,
                                CHARACTER_LIMIT,
                                WORLD_LIMIT,
                                worldSelectedRef,
                                worldCrystalRef
                        );
                selected.addAll(forcedSelected);
                characterTakenBossGroup.put(character.getCharacterId(), takenGroup);
                characterRecs.add(CharacterRecommendation.builder()
                        .characterId(character.getCharacterId())
                        .selectedBossCount(selected.size())
                        .crystalIncome(selected.stream().mapToLong(SelectedBoss::getCrystalPrice).sum())
                        .bosses(selected)
                        .build());
            }

            // 사용자가 입력한 1인 보스 중 가장 비싼 보스의 가격을 기준으로 동일/이하 가격은 잡을 수 있다고 가정
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

            // 나머지 슬롯은 월드/캐릭터 제한 안에서 높은 가격의 보스를 우선적으로 채움. (greedy)
            // 캐릭터 순회하며 가능한 보스 후보를 가격 내림차순으로 시도
            // 난이도 그룹 충돌 방지: 동일 보스명은 1회만 선택
            List<BossResponse> weeklyActive = allBosses.stream()
                    .filter(b -> !Boolean.TRUE.equals(b.getIsMonthly()))
                    .sorted(Comparator.comparingLong(BossResponse::getCrystalPrice).reversed())
                    .toList();

            globalGreedyAllocator.allocate(
                    characters,
                    weeklyActive,
                    characterMaxSoloPrice,
                    characterTakenBossGroup,
                    characterRecs,
                    CHARACTER_LIMIT,
                    WORLD_LIMIT,
                    worldSelectedRef,
                    worldCrystalRef
            );

            int worldSelectedCount = worldSelectedRef[0];
            long worldCrystal = worldCrystalRef[0];
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
}


