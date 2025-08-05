package com.happymapleday.recommendation.service;

import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.request.RecommendationRequest;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.OptimizationSummary;
import com.happymapleday.recommendation.dto.response.RecommendationResponse;
import com.happymapleday.recommendation.service.impl.RecommendationServiceImpl;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private OptimizationService optimizationService;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private RecommendationRequest request;
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

        request = RecommendationRequest.builder()
                .userId(1L)
                .characterBossSelections(List.of(characterBossSelection1, characterBossSelection2))
                .build();

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
    @DisplayName("추천 생성 성공")
    void generateRecommendation_Success() {
        // given
        given(optimizationService.optimizeRecommendations(request.getCharacterBossSelections()))
                .willReturn(characterRecommendations);
        given(optimizationService.createOptimizationSummary(characterRecommendations))
                .willReturn(optimizationSummary);

        // when
        RecommendationResponse result = recommendationService.generateRecommendation(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getTotalExpectedIncome()).isEqualTo(BigInteger.valueOf(55000000));
        assertThat(result.getTotalCrystalCount()).isEqualTo(4);
        assertThat(result.getTotalBossCount()).isEqualTo(2);
        assertThat(result.getCharacterRecommendations()).hasSize(2);
        assertThat(result.getOptimizationSummary()).isEqualTo(optimizationSummary);

        verify(optimizationService).optimizeRecommendations(request.getCharacterBossSelections());
        verify(optimizationService).createOptimizationSummary(characterRecommendations);
    }

    @Test
    @DisplayName("단일 캐릭터 추천 생성")
    void generateRecommendation_SingleCharacter() {
        // given
        CharacterBossSelection singleCharacter = CharacterBossSelection.builder()
                .characterId(1L)
                .characterName("메인캐릭터")
                .characterLevel(270)
                .bossSelections(List.of(BossSelection.builder()
                        .bossId(1L)
                        .partySize(1)
                        .build()))
                .build();

        RecommendationRequest singleRequest = RecommendationRequest.builder()
                .userId(1L)
                .characterBossSelections(List.of(singleCharacter))
                .build();

        List<CharacterRecommendation> singleRecommendations = List.of(characterRecommendations.get(0));

        given(optimizationService.optimizeRecommendations(singleRequest.getCharacterBossSelections()))
                .willReturn(singleRecommendations);
        given(optimizationService.createOptimizationSummary(singleRecommendations))
                .willReturn(optimizationSummary);

        // when
        RecommendationResponse result = recommendationService.generateRecommendation(singleRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getTotalExpectedIncome()).isEqualTo(BigInteger.valueOf(5000000));
        assertThat(result.getTotalCrystalCount()).isEqualTo(1);
        assertThat(result.getTotalBossCount()).isEqualTo(1);
        assertThat(result.getCharacterRecommendations()).hasSize(1);
    }

    @Test
    @DisplayName("수익이 0인 경우 처리")
    void generateRecommendation_ZeroIncome() {
        // given
        CharacterRecommendation zeroIncomeRecommendation = CharacterRecommendation.builder()
                .characterId(1L)
                .characterName("메인캐릭터")
                .characterLevel(270)
                .crystalCount(0)
                .expectedIncome(BigInteger.ZERO)
                .bossRecommendations(List.of())
                .highestDifficultySoloBossId(null)
                .partyBossIds(List.of())
                .build();

        List<CharacterRecommendation> zeroIncomeRecommendations = List.of(zeroIncomeRecommendation);

        given(optimizationService.optimizeRecommendations(request.getCharacterBossSelections()))
                .willReturn(zeroIncomeRecommendations);
        given(optimizationService.createOptimizationSummary(zeroIncomeRecommendations))
                .willReturn(optimizationSummary);

        // when
        RecommendationResponse result = recommendationService.generateRecommendation(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalExpectedIncome()).isEqualTo(BigInteger.ZERO);
        assertThat(result.getTotalCrystalCount()).isEqualTo(0);
        assertThat(result.getTotalBossCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("높은 수익 값 처리")
    void generateRecommendation_HighIncome() {
        // given
        CharacterRecommendation highIncomeRecommendation = CharacterRecommendation.builder()
                .characterId(1L)
                .characterName("메인캐릭터")
                .characterLevel(270)
                .crystalCount(12)
                .expectedIncome(new BigInteger("1000000000000"))
                .bossRecommendations(List.of())
                .highestDifficultySoloBossId(null)
                .partyBossIds(List.of())
                .build();

        List<CharacterRecommendation> highIncomeRecommendations = List.of(highIncomeRecommendation);

        given(optimizationService.optimizeRecommendations(request.getCharacterBossSelections()))
                .willReturn(highIncomeRecommendations);
        given(optimizationService.createOptimizationSummary(highIncomeRecommendations))
                .willReturn(optimizationSummary);

        // when
        RecommendationResponse result = recommendationService.generateRecommendation(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalExpectedIncome()).isEqualTo(new BigInteger("1000000000000"));
        assertThat(result.getTotalCrystalCount()).isEqualTo(12);
    }
} 