package com.happymapleday.recommendation.service.processor;

import com.happymapleday.common.client.BossServiceClient;
import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.service.factory.BossRecommendationFactory;
import com.happymapleday.recommendation.service.limiter.CrystalLimitManager;
import com.happymapleday.recommendation.service.optimizer.BossSelectionOptimizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterRecommendationProcessorTest {

    @Mock
    private BossSelectionOptimizer bossSelectionOptimizer;

    @Mock
    private BossRecommendationFactory bossRecommendationFactory;

    @Mock
    private CrystalLimitManager crystalLimitManager;
    
    @Mock
    private BossServiceClient bossServiceClient;

    @InjectMocks
    private CharacterRecommendationProcessor characterRecommendationProcessor;

    private CharacterBossSelection characterBossSelection;
    private List<BossSelection> bossSelections;
    private List<BossSelection> partyBosses;
    private List<BossSelection> soloBosses;
    private List<BossSelection> filteredBossSelections;
    private List<BossResponse> bossResponses;
    private Map<Long, BossResponse> bossInfoMap;

    @BeforeEach
    void setUp() {
        // 보스 응답 데이터 준비
        BossResponse soloBossResponse = BossResponse.builder()
                .bossId(1L)
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(10000L)
                .maxPartySize(6)
                .build();

        BossResponse partyBossResponse = BossResponse.builder()
                .bossId(2L)
                .bossName("루타비스")
                .difficulty("하드")
                .crystalPrice(200000L)
                .maxPartySize(6)
                .build();
        
        bossResponses = List.of(soloBossResponse, partyBossResponse);
        bossInfoMap = Map.of(1L, soloBossResponse, 2L, partyBossResponse);

        // 테스트 데이터 준비
        BossSelection soloBoss = BossSelection.builder()
                .bossId(1L)
                .partySize(1)
                .build();

        BossSelection partyBoss = BossSelection.builder()
                .bossId(2L)
                .partySize(3)
                .build();

        bossSelections = List.of(soloBoss, partyBoss);
        partyBosses = List.of(partyBoss);
        soloBosses = List.of(soloBoss);
        filteredBossSelections = List.of(soloBoss, partyBoss);

        characterBossSelection = CharacterBossSelection.builder()
                .characterId(1L)
                .characterName("메인캐릭터")
                .characterLevel(270)
                .bossSelections(bossSelections)
                .build();
                
        // BossServiceClient 모킹
        given(bossServiceClient.getBossList())
                .willReturn(ApiResponse.success(bossResponses));
    }

    @Test
    @DisplayName("캐릭터 추천 생성 성공")
    void createCharacterRecommendation_Success() {
        // given
        int currentGlobalCrystalCount = 0;
        Long highestDifficultySoloBossId = 1L;
        List<Long> partyBossIds = List.of(2L);

        given(bossSelectionOptimizer.filterUniqueHighestProfitBosses(eq(bossSelections), eq(bossInfoMap)))
                .willReturn(filteredBossSelections);
        given(bossSelectionOptimizer.filterPartyBosses(filteredBossSelections))
                .willReturn(partyBosses);
        given(bossSelectionOptimizer.filterAndSortSoloBosses(eq(filteredBossSelections), eq(bossInfoMap)))
                .willReturn(soloBosses);
        given(bossSelectionOptimizer.findHighestDifficultySoloBossId(eq(soloBosses), eq(bossInfoMap)))
                .willReturn(highestDifficultySoloBossId);
        given(bossSelectionOptimizer.extractPartyBossIds(partyBosses))
                .willReturn(partyBossIds);

        given(crystalLimitManager.isCharacterLimitReached(anyInt())).willReturn(false);
        given(crystalLimitManager.isWorldLimitReached(anyInt())).willReturn(false);

        BossRecommendation partyBossRecommendation = BossRecommendation.builder()
                .bossId(2L)
                .bossName("루타비스")
                .difficulty("하드")
                .crystalPrice(200000L)
                .partySize(3)
                .expectedIncome(BigInteger.valueOf(200000))
                .isPartyBoss(true)
                .isHighestDifficultySolo(false)
                .isIncludedInOptimization(true)
                .build();

        BossRecommendation soloBossRecommendation = BossRecommendation.builder()
                .bossId(1L)
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(10000L)
                .partySize(1)
                .expectedIncome(BigInteger.valueOf(10000))
                .isPartyBoss(false)
                .isHighestDifficultySolo(true)
                .isIncludedInOptimization(true)
                .build();

        given(bossRecommendationFactory.createBossRecommendation(eq(partyBosses.get(0)), eq(bossInfoMap.get(2L)), eq(true), eq(false), eq(true)))
                .willReturn(partyBossRecommendation);
        given(bossRecommendationFactory.createBossRecommendation(eq(soloBosses.get(0)), eq(bossInfoMap.get(1L)), eq(false), eq(true), eq(true)))
                .willReturn(soloBossRecommendation);

        // when
        CharacterRecommendation result = characterRecommendationProcessor.createCharacterRecommendation(
                characterBossSelection, currentGlobalCrystalCount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCharacterId()).isEqualTo(1L);
        assertThat(result.getCharacterName()).isEqualTo("메인캐릭터");
        assertThat(result.getCharacterLevel()).isEqualTo(270);
        assertThat(result.getCrystalCount()).isEqualTo(2);
        assertThat(result.getExpectedIncome()).isEqualTo(BigInteger.valueOf(210000));
        assertThat(result.getBossRecommendations()).hasSize(2);
        assertThat(result.getHighestDifficultySoloBossId()).isEqualTo(1L);
        assertThat(result.getPartyBossIds()).isEqualTo(List.of(2L));

        verify(bossServiceClient).getBossList();
        verify(bossSelectionOptimizer).filterUniqueHighestProfitBosses(eq(bossSelections), eq(bossInfoMap));
        verify(bossSelectionOptimizer).filterPartyBosses(filteredBossSelections);
        verify(bossSelectionOptimizer).filterAndSortSoloBosses(eq(filteredBossSelections), eq(bossInfoMap));
        verify(bossSelectionOptimizer).findHighestDifficultySoloBossId(eq(soloBosses), eq(bossInfoMap));
        verify(bossSelectionOptimizer).extractPartyBossIds(partyBosses);
        verify(bossRecommendationFactory, times(2)).createBossRecommendation(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    }

    @Test
    @DisplayName("캐릭터 크리스탈 제한 도달 시 처리")
    void createCharacterRecommendation_CharacterLimitReached() {
        // given
        int currentGlobalCrystalCount = 0;
        Long highestDifficultySoloBossId = 1L;
        List<Long> partyBossIds = List.of(2L);

        given(bossSelectionOptimizer.filterUniqueHighestProfitBosses(eq(bossSelections), eq(bossInfoMap)))
                .willReturn(filteredBossSelections);
        given(bossSelectionOptimizer.filterPartyBosses(filteredBossSelections))
                .willReturn(partyBosses);
        given(bossSelectionOptimizer.filterAndSortSoloBosses(eq(filteredBossSelections), eq(bossInfoMap)))
                .willReturn(soloBosses);
        given(bossSelectionOptimizer.findHighestDifficultySoloBossId(eq(soloBosses), eq(bossInfoMap)))
                .willReturn(highestDifficultySoloBossId);
        given(bossSelectionOptimizer.extractPartyBossIds(partyBosses))
                .willReturn(partyBossIds);

        given(crystalLimitManager.isCharacterLimitReached(0)).willReturn(false);
        given(crystalLimitManager.isCharacterLimitReached(1)).willReturn(true);
        given(crystalLimitManager.isWorldLimitReached(anyInt())).willReturn(false);

        BossRecommendation partyBossRecommendation = BossRecommendation.builder()
                .bossId(2L)
                .bossName("루타비스")
                .difficulty("하드")
                .crystalPrice(200000L)
                .partySize(3)
                .expectedIncome(BigInteger.valueOf(200000))
                .isPartyBoss(true)
                .isHighestDifficultySolo(false)
                .isIncludedInOptimization(true)
                .build();

        given(bossRecommendationFactory.createBossRecommendation(eq(partyBosses.get(0)), eq(bossInfoMap.get(2L)), eq(true), eq(false), eq(true)))
                .willReturn(partyBossRecommendation);

        // when
        CharacterRecommendation result = characterRecommendationProcessor.createCharacterRecommendation(
                characterBossSelection, currentGlobalCrystalCount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCrystalCount()).isEqualTo(1);
        assertThat(result.getExpectedIncome()).isEqualTo(BigInteger.valueOf(200000));
        assertThat(result.getBossRecommendations()).hasSize(1);
        assertThat(result.getBossRecommendations().get(0).isPartyBoss()).isTrue();

        verify(bossServiceClient).getBossList();
        verify(bossRecommendationFactory, times(1)).createBossRecommendation(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    }

    @Test
    @DisplayName("빈 보스 선택 목록 처리")
    void createCharacterRecommendation_EmptyBossSelections() {
        // given
        int currentGlobalCrystalCount = 0;
        CharacterBossSelection emptySelection = CharacterBossSelection.builder()
                .characterId(1L)
                .characterName("메인캐릭터")
                .characterLevel(270)
                .bossSelections(List.of())
                .build();

        given(bossSelectionOptimizer.filterUniqueHighestProfitBosses(eq(List.of()), eq(bossInfoMap)))
                .willReturn(List.of());
        given(bossSelectionOptimizer.filterPartyBosses(List.of()))
                .willReturn(List.of());
        given(bossSelectionOptimizer.filterAndSortSoloBosses(eq(List.of()), eq(bossInfoMap)))
                .willReturn(List.of());
        given(bossSelectionOptimizer.findHighestDifficultySoloBossId(eq(List.of()), eq(bossInfoMap)))
                .willReturn(null);
        given(bossSelectionOptimizer.extractPartyBossIds(List.of()))
                .willReturn(List.of());

        // when
        CharacterRecommendation result = characterRecommendationProcessor.createCharacterRecommendation(
                emptySelection, currentGlobalCrystalCount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCrystalCount()).isEqualTo(0);
        assertThat(result.getExpectedIncome()).isEqualTo(BigInteger.ZERO);
        assertThat(result.getBossRecommendations()).isEmpty();
        assertThat(result.getHighestDifficultySoloBossId()).isNull();
        assertThat(result.getPartyBossIds()).isEmpty();

        verify(bossServiceClient).getBossList();
        verify(bossRecommendationFactory, never()).createBossRecommendation(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    }
} 