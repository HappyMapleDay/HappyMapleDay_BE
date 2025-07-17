package com.happymapleday.recommendation.service.processor;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyBoolean;
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

    @InjectMocks
    private CharacterRecommendationProcessor characterRecommendationProcessor;

    private CharacterBossSelection characterBossSelection;
    private List<BossSelection> bossSelections;
    private List<BossSelection> partyBosses;
    private List<BossSelection> soloBosses;
    private List<BossSelection> filteredBossSelections;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        BossSelection soloBoss = BossSelection.builder()
                .bossId(1L)
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(10000L)
                .partySize(1)
                .maxPartySize(6)
                .build();

        BossSelection partyBoss = BossSelection.builder()
                .bossId(2L)
                .bossName("루타비스")
                .difficulty("하드")
                .crystalPrice(200000L)
                .partySize(3)
                .maxPartySize(6)
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
    }

    @Test
    @DisplayName("캐릭터 추천 생성 성공")
    void createCharacterRecommendation_Success() {
        // given
        int currentGlobalCrystalCount = 0;
        Long highestDifficultySoloBossId = 1L;
        List<Long> partyBossIds = List.of(2L);

        given(bossSelectionOptimizer.filterUniqueHighestProfitBosses(bossSelections))
                .willReturn(filteredBossSelections);
        given(bossSelectionOptimizer.filterPartyBosses(filteredBossSelections))
                .willReturn(partyBosses);
        given(bossSelectionOptimizer.filterAndSortSoloBosses(filteredBossSelections))
                .willReturn(soloBosses);
        given(bossSelectionOptimizer.findHighestDifficultySoloBossId(soloBosses))
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

        given(bossRecommendationFactory.createBossRecommendation(partyBosses.get(0), true, false, true))
                .willReturn(partyBossRecommendation);
        given(bossRecommendationFactory.createBossRecommendation(soloBosses.get(0), false, true, true))
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

        verify(bossSelectionOptimizer).filterUniqueHighestProfitBosses(bossSelections);
        verify(bossSelectionOptimizer).filterPartyBosses(filteredBossSelections);
        verify(bossSelectionOptimizer).filterAndSortSoloBosses(filteredBossSelections);
        verify(bossSelectionOptimizer).findHighestDifficultySoloBossId(soloBosses);
        verify(bossSelectionOptimizer).extractPartyBossIds(partyBosses);
        verify(bossRecommendationFactory, times(2)).createBossRecommendation(any(), anyBoolean(), anyBoolean(), anyBoolean());
    }

    @Test
    @DisplayName("캐릭터 크리스탈 제한 도달 시 처리")
    void createCharacterRecommendation_CharacterLimitReached() {
        // given
        int currentGlobalCrystalCount = 0;
        Long highestDifficultySoloBossId = 1L;
        List<Long> partyBossIds = List.of(2L);

        given(bossSelectionOptimizer.filterUniqueHighestProfitBosses(bossSelections))
                .willReturn(filteredBossSelections);
        given(bossSelectionOptimizer.filterPartyBosses(filteredBossSelections))
                .willReturn(partyBosses);
        given(bossSelectionOptimizer.filterAndSortSoloBosses(filteredBossSelections))
                .willReturn(soloBosses);
        given(bossSelectionOptimizer.findHighestDifficultySoloBossId(soloBosses))
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

        given(bossRecommendationFactory.createBossRecommendation(partyBosses.get(0), true, false, true))
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

        verify(bossRecommendationFactory, times(1)).createBossRecommendation(any(), anyBoolean(), anyBoolean(), anyBoolean());
    }

    @Test
    @DisplayName("월드 크리스탈 제한 도달 시 처리")
    void createCharacterRecommendation_WorldLimitReached() {
        // given
        int currentGlobalCrystalCount = 89;
        Long highestDifficultySoloBossId = 1L;
        List<Long> partyBossIds = List.of(2L);

        given(bossSelectionOptimizer.filterUniqueHighestProfitBosses(bossSelections))
                .willReturn(filteredBossSelections);
        given(bossSelectionOptimizer.filterPartyBosses(filteredBossSelections))
                .willReturn(partyBosses);
        given(bossSelectionOptimizer.filterAndSortSoloBosses(filteredBossSelections))
                .willReturn(soloBosses);
        given(bossSelectionOptimizer.findHighestDifficultySoloBossId(soloBosses))
                .willReturn(highestDifficultySoloBossId);
        given(bossSelectionOptimizer.extractPartyBossIds(partyBosses))
                .willReturn(partyBossIds);

        given(crystalLimitManager.isCharacterLimitReached(anyInt())).willReturn(false);
        given(crystalLimitManager.isWorldLimitReached(89)).willReturn(false);
        given(crystalLimitManager.isWorldLimitReached(90)).willReturn(true);

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

        given(bossRecommendationFactory.createBossRecommendation(partyBosses.get(0), true, false, true))
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

        verify(bossRecommendationFactory, times(1)).createBossRecommendation(any(), anyBoolean(), anyBoolean(), anyBoolean());
    }

    @Test
    @DisplayName("솔로 보스만 있는 경우 처리")
    void createCharacterRecommendation_OnlySoloBosses() {
        // given
        int currentGlobalCrystalCount = 0;
        Long highestDifficultySoloBossId = 1L;
        List<Long> emptyPartyBossIds = List.of();

        given(bossSelectionOptimizer.filterUniqueHighestProfitBosses(bossSelections))
                .willReturn(soloBosses);
        given(bossSelectionOptimizer.filterPartyBosses(soloBosses))
                .willReturn(List.of());
        given(bossSelectionOptimizer.filterAndSortSoloBosses(soloBosses))
                .willReturn(soloBosses);
        given(bossSelectionOptimizer.findHighestDifficultySoloBossId(soloBosses))
                .willReturn(highestDifficultySoloBossId);
        given(bossSelectionOptimizer.extractPartyBossIds(List.of()))
                .willReturn(emptyPartyBossIds);

        given(crystalLimitManager.isCharacterLimitReached(anyInt())).willReturn(false);
        given(crystalLimitManager.isWorldLimitReached(anyInt())).willReturn(false);

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

        given(bossRecommendationFactory.createBossRecommendation(soloBosses.get(0), false, true, true))
                .willReturn(soloBossRecommendation);

        // when
        CharacterRecommendation result = characterRecommendationProcessor.createCharacterRecommendation(
                characterBossSelection, currentGlobalCrystalCount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCrystalCount()).isEqualTo(1);
        assertThat(result.getExpectedIncome()).isEqualTo(BigInteger.valueOf(10000));
        assertThat(result.getBossRecommendations()).hasSize(1);
        assertThat(result.getBossRecommendations().get(0).isPartyBoss()).isFalse();
        assertThat(result.getBossRecommendations().get(0).isHighestDifficultySolo()).isTrue();
        assertThat(result.getPartyBossIds()).isEmpty();

        verify(bossRecommendationFactory, times(1)).createBossRecommendation(any(), anyBoolean(), anyBoolean(), anyBoolean());
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

        given(bossSelectionOptimizer.filterUniqueHighestProfitBosses(List.of()))
                .willReturn(List.of());
        given(bossSelectionOptimizer.filterPartyBosses(List.of()))
                .willReturn(List.of());
        given(bossSelectionOptimizer.filterAndSortSoloBosses(List.of()))
                .willReturn(List.of());
        given(bossSelectionOptimizer.findHighestDifficultySoloBossId(List.of()))
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

        verify(bossRecommendationFactory, never()).createBossRecommendation(any(), anyBoolean(), anyBoolean(), anyBoolean());
    }
} 