package com.happymapleday.recommendation.service.impl;

import com.happymapleday.common.client.BossServiceClient;
import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.CharacterInput;
import com.happymapleday.recommendation.dto.request.OptimizeRecommendationRequest;
import com.happymapleday.recommendation.dto.response.*;
import com.happymapleday.recommendation.service.RecommendationService;
import com.happymapleday.recommendation.service.allocator.GlobalGreedyAllocator;
import com.happymapleday.recommendation.service.analysis.CharacterPlanAnalyzer;
import com.happymapleday.recommendation.service.forced.ForcedInclusionAggregator;
import com.happymapleday.recommendation.service.provider.WeeklyActiveProvider;
import com.happymapleday.recommendation.service.model.WorldAccumulator;
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
    private final ForcedInclusionAggregator forcedInclusionAggregator;
    private final GlobalGreedyAllocator globalGreedyAllocator;
    private final CharacterPlanAnalyzer characterPlanAnalyzer;
    private final WeeklyActiveProvider weeklyActiveProvider;

    @Override
    public OptimizedRecommendationResponse optimize(OptimizeRecommendationRequest request) {
        ApiResponse<List<BossResponse>> bossListResp = bossServiceClient.getBossList();
        List<BossResponse> allBosses = bossListResp.getData();
        Map<Long, BossResponse> bossById = allBosses.stream().collect(Collectors.toMap(BossResponse::getBossId, Function.identity()));
        // 캐시용 주간 보스 목록 사전 계산
        List<BossResponse> weeklyActiveOnce = weeklyActiveProvider.provideSortedWeekly(allBosses);

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

            Map<Long, Set<String>> characterTakenBossGroup = new HashMap<>();
            WorldAccumulator accInit = new WorldAccumulator();
            for (int i = 0; i < worldSelectedRef[0]; i++) accInit.incrementSelected();
            accInit.addCrystal(worldCrystalRef[0]);
            forcedInclusionAggregator.aggregateAndApply(
                    characters,
                    bossById,
                    CHARACTER_LIMIT,
                    WORLD_LIMIT,
                    characterRecs,
                    accInit,
                    characterTakenBossGroup
            );
            worldSelectedRef[0] = accInit.getSelectedCount();
            worldCrystalRef[0] = accInit.getCrystal();

            // 사용자가 입력한 1인 보스 중 가장 비싼 보스의 가격을 기준으로 동일/이하 가격은 잡을 수 있다고 가정
            Map<Long, Long> characterMaxSoloPrice = characterPlanAnalyzer.computeMaxSoloPrice(characters, bossById);

            // 나머지 슬롯은 월드/캐릭터 제한 안에서 높은 가격의 보스를 우선적으로 채움. (greedy)
            // 캐릭터 순회하며 가능한 보스 후보를 가격 내림차순으로 시도
            // 난이도 그룹 충돌 방지: 동일 보스명은 1회만 선택
            // 주간 보스 정렬은 미리 계산된 리스트 재사용
            List<BossResponse> weeklyActive = weeklyActiveOnce;

            WorldAccumulator acc = new WorldAccumulator();
            // 초기 강제 포함 결과를 acc에 반영
            for (int i = 0; i < worldSelectedRef[0]; i++) acc.incrementSelected();
            acc.addCrystal(worldCrystalRef[0]);

            globalGreedyAllocator.allocate(
                    characters,
                    weeklyActive,
                    characterMaxSoloPrice,
                    characterTakenBossGroup,
                    characterRecs,
                    CHARACTER_LIMIT,
                    WORLD_LIMIT,
                    acc
            );

            int worldSelectedCount = acc.getSelectedCount();
            long worldCrystal = acc.getCrystal();
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


