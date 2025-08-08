package com.happymapleday.recommendation.service;

import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.OptimizationSummary;
import com.happymapleday.recommendation.service.calculator.OptimizationSummaryCalculator;
import com.happymapleday.recommendation.service.impl.OptimizationServiceImpl;
import com.happymapleday.recommendation.service.limiter.CrystalLimitManager;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class OptimizationServiceTest {

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
                .partySize(1)
                .build();

        BossSelection bossSelection2 = BossSelection.builder()
                .bossId(2L)
                .partySize(3)
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
                .crystalCount(1)
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
        given(crystalLimitManager.optimizeGlobalRecommendations(eq(characterBossSelections)))
                .willReturn(characterRecommendations);

        // when
        List<CharacterRecommendation> result = optimizationService.optimizeRecommendations(characterBossSelections);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(characterRecommendations);

        verify(crystalLimitManager, times(1)).optimizeGlobalRecommendations(eq(characterBossSelections));
    }

    @Test
    @DisplayName("단일 캐릭터 최적화")
    void optimizeRecommendations_SingleCharacter() {
        // given
        List<CharacterBossSelection> singleCharacterSelection = List.of(characterBossSelections.get(0));
        List<CharacterRecommendation> singleCharacterRecommendation = List.of(characterRecommendations.get(0));

        given(crystalLimitManager.optimizeGlobalRecommendations(eq(singleCharacterSelection)))
                .willReturn(singleCharacterRecommendation);

        // when
        List<CharacterRecommendation> result = optimizationService.optimizeRecommendations(singleCharacterSelection);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(singleCharacterRecommendation.get(0));

        verify(crystalLimitManager, times(1)).optimizeGlobalRecommendations(eq(singleCharacterSelection));
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
        verify(crystalLimitManager, never()).optimizeGlobalRecommendations(any());
    }

    @Test
    @DisplayName("오케스트레이션 위임 호출")
    void optimizeRecommendations_DelegatesToCrystalLimitManager() {
        // given
        given(crystalLimitManager.optimizeGlobalRecommendations(eq(characterBossSelections)))
                .willReturn(characterRecommendations);

        // when
        List<CharacterRecommendation> result = optimizationService.optimizeRecommendations(characterBossSelections);

        // then
        assertThat(result).hasSize(2);
        verify(crystalLimitManager, times(1)).optimizeGlobalRecommendations(eq(characterBossSelections));
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