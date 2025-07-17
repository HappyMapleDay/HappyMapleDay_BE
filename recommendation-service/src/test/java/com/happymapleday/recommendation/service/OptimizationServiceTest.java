package com.happymapleday.recommendation.service;

import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.OptimizationSummary;
import com.happymapleday.recommendation.service.calculator.OptimizationSummaryCalculator;
import com.happymapleday.recommendation.service.impl.OptimizationServiceImpl;
import com.happymapleday.recommendation.service.limiter.CrystalLimitManager;
import com.happymapleday.recommendation.service.processor.CharacterRecommendationProcessor;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptimizationServiceTest {

    @Mock
    private CharacterRecommendationProcessor characterRecommendationProcessor;

    @Mock
    private CrystalLimitManager crystalLimitManager;

    @Mock
    private OptimizationSummaryCalculator optimizationSummaryCalculator;

    @InjectMocks
    private OptimizationServiceImpl optimizationService;

    private List<CharacterBossSelection> characterBossSelections;
    private List<CharacterRecommendation> characterRecommendations;
    private OptimizationSummary optimizationSummary;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        BossSelection bossSelection1 = BossSelection.builder()
                .bossId(1L)
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(10000L)
                .partySize(1)
                .maxPartySize(6)
                .build();

        BossSelection bossSelection2 = BossSelection.builder()
                .bossId(2L)
                .bossName("루타비스")
                .difficulty("하드")
                .crystalPrice(200000L)
                .partySize(3)
                .maxPartySize(6)
                .build();

        CharacterBossSelection characterBossSelection1 = CharacterBossSelection.builder()
                .characterId(1L)
                .characterName("메인캐릭터")
                .characterLevel(270)
                .bossSelections(List.of(bossSelection1))
                .build();

        CharacterBossSelection characterBossSelection2 = CharacterBossSelection.builder()
                .characterId(2L)
                .characterName("서브캐릭터")
                .characterLevel(260)
                .bossSelections(List.of(bossSelection2))
                .build();

        characterBossSelections = List.of(characterBossSelection1, characterBossSelection2);

        // 응답 데이터 준비
        BossRecommendation bossRecommendation1 = BossRecommendation.builder()
                .bossId(1L)
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(10000L)
                .partySize(1)
                .expectedIncome(BigInteger.valueOf(5000000))
                .isPartyBoss(false)
                .isHighestDifficultySolo(true)
                .isIncludedInOptimization(true)
                .build();

        BossRecommendation bossRecommendation2 = BossRecommendation.builder()
                .bossId(2L)
                .bossName("루타비스")
                .difficulty("하드")
                .crystalPrice(200000L)
                .partySize(3)
                .expectedIncome(BigInteger.valueOf(50000000))
                .isPartyBoss(true)
                .isHighestDifficultySolo(false)
                .isIncludedInOptimization(true)
                .build();

        CharacterRecommendation characterRecommendation1 = CharacterRecommendation.builder()
                .characterId(1L)
                .characterName("메인캐릭터")
                .characterLevel(270)
                .crystalCount(1)
                .expectedIncome(BigInteger.valueOf(5000000))
                .bossRecommendations(List.of(bossRecommendation1))
                .highestDifficultySoloBossId(1L)
                .partyBossIds(List.of())
                .build();

        CharacterRecommendation characterRecommendation2 = CharacterRecommendation.builder()
                .characterId(2L)
                .characterName("서브캐릭터")
                .characterLevel(260)
                .crystalCount(3)
                .expectedIncome(BigInteger.valueOf(50000000))
                .bossRecommendations(List.of(bossRecommendation2))
                .highestDifficultySoloBossId(null)
                .partyBossIds(List.of(2L))
                .build();

        characterRecommendations = List.of(characterRecommendation1, characterRecommendation2);

        optimizationSummary = OptimizationSummary.builder()
                .isOptimized(true)
                .optimizationMessage("최적화 완료")
                .totalPartyBossCount(1)
                .totalSoloBossCount(1)
                .charactersWithMaxCrystal(0)
                .isWorldCrystalLimitReached(false)
                .build();
    }

    @Test
    @DisplayName("최적화 추천 생성 성공")
    void optimizeRecommendations_Success() {
        // given
        given(characterRecommendationProcessor.createCharacterRecommendation(any(CharacterBossSelection.class), anyInt()))
                .willReturn(characterRecommendations.get(0))
                .willReturn(characterRecommendations.get(1));

        // when
        List<CharacterRecommendation> result = optimizationService.optimizeRecommendations(characterBossSelections);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(characterRecommendations);

        verify(characterRecommendationProcessor, times(2)).createCharacterRecommendation(any(CharacterBossSelection.class), anyInt());
        verify(crystalLimitManager).optimizeWithinGlobalLimit(any(List.class), anyInt());
    }

    @Test
    @DisplayName("단일 캐릭터 최적화")
    void optimizeRecommendations_SingleCharacter() {
        // given
        List<CharacterBossSelection> singleCharacterSelection = List.of(characterBossSelections.get(0));
        List<CharacterRecommendation> singleCharacterRecommendation = List.of(characterRecommendations.get(0));

        given(characterRecommendationProcessor.createCharacterRecommendation(any(CharacterBossSelection.class), anyInt()))
                .willReturn(singleCharacterRecommendation.get(0));

        // when
        List<CharacterRecommendation> result = optimizationService.optimizeRecommendations(singleCharacterSelection);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(singleCharacterRecommendation.get(0));

        verify(characterRecommendationProcessor, times(1)).createCharacterRecommendation(any(CharacterBossSelection.class), anyInt());
        verify(crystalLimitManager).optimizeWithinGlobalLimit(any(List.class), anyInt());
    }

    @Test
    @DisplayName("최적화 요약 생성 성공")
    void createOptimizationSummary_Success() {
        // given
        given(optimizationSummaryCalculator.createOptimizationSummary(characterRecommendations))
                .willReturn(optimizationSummary);

        // when
        OptimizationSummary result = optimizationService.createOptimizationSummary(characterRecommendations);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isOptimized()).isTrue();
        assertThat(result.getOptimizationMessage()).isEqualTo("최적화 완료");
        assertThat(result.getTotalPartyBossCount()).isEqualTo(1);
        assertThat(result.getTotalSoloBossCount()).isEqualTo(1);
        assertThat(result.getCharactersWithMaxCrystal()).isEqualTo(0);
        assertThat(result.isWorldCrystalLimitReached()).isFalse();

        verify(optimizationSummaryCalculator).createOptimizationSummary(characterRecommendations);
    }

    @Test
    @DisplayName("빈 캐릭터 선택 목록 처리")
    void optimizeRecommendations_EmptyCharacterSelections() {
        // given
        List<CharacterBossSelection> emptySelections = List.of();

        // when
        List<CharacterRecommendation> result = optimizationService.optimizeRecommendations(emptySelections);

        // then
        assertThat(result).isEmpty();
        verify(characterRecommendationProcessor, never()).createCharacterRecommendation(any(), anyInt());
        verify(crystalLimitManager).optimizeWithinGlobalLimit(any(List.class), eq(0));
    }

    @Test
    @DisplayName("결정석 개수 누적 테스트")
    void optimizeRecommendations_CrystalCountAccumulation() {
        // given
        CharacterRecommendation rec1 = CharacterRecommendation.builder()
                .characterId(1L)
                .crystalCount(5)
                .expectedIncome(BigInteger.valueOf(10000000))
                .bossRecommendations(List.of())
                .build();

        CharacterRecommendation rec2 = CharacterRecommendation.builder()
                .characterId(2L)
                .crystalCount(8)
                .expectedIncome(BigInteger.valueOf(20000000))
                .bossRecommendations(List.of())
                .build();

        given(characterRecommendationProcessor.createCharacterRecommendation(characterBossSelections.get(0), 0))
                .willReturn(rec1);
        given(characterRecommendationProcessor.createCharacterRecommendation(characterBossSelections.get(1), 5))
                .willReturn(rec2);

        // when
        List<CharacterRecommendation> result = optimizationService.optimizeRecommendations(characterBossSelections);

        // then
        assertThat(result).hasSize(2);
        verify(characterRecommendationProcessor).createCharacterRecommendation(characterBossSelections.get(0), 0);
        verify(characterRecommendationProcessor).createCharacterRecommendation(characterBossSelections.get(1), 5);
        verify(crystalLimitManager).optimizeWithinGlobalLimit(any(List.class), eq(13));
    }

    @Test
    @DisplayName("빈 추천 목록으로 요약 생성")
    void createOptimizationSummary_EmptyRecommendations() {
        // given
        List<CharacterRecommendation> emptyRecommendations = List.of();
        OptimizationSummary emptySummary = OptimizationSummary.builder()
                .isOptimized(false)
                .optimizationMessage("추천 없음")
                .totalPartyBossCount(0)
                .totalSoloBossCount(0)
                .charactersWithMaxCrystal(0)
                .isWorldCrystalLimitReached(false)
                .build();

        given(optimizationSummaryCalculator.createOptimizationSummary(emptyRecommendations))
                .willReturn(emptySummary);

        // when
        OptimizationSummary result = optimizationService.createOptimizationSummary(emptyRecommendations);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isOptimized()).isFalse();
        assertThat(result.getOptimizationMessage()).isEqualTo("추천 없음");
        assertThat(result.getTotalPartyBossCount()).isEqualTo(0);
        assertThat(result.getTotalSoloBossCount()).isEqualTo(0);
    }
} 