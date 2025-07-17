package com.happymapleday.recommendation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.request.RecommendationRequest;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.OptimizationSummary;
import com.happymapleday.recommendation.dto.response.RecommendationResponse;
import com.happymapleday.recommendation.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Autowired
    private ObjectMapper objectMapper;

    private RecommendationRequest request;
    private RecommendationResponse response;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        BossSelection bossSelection = BossSelection.builder()
                .bossId(1L)
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(10000L)
                .partySize(1)
                .maxPartySize(6)
                .build();

        CharacterBossSelection characterBossSelection = CharacterBossSelection.builder()
                .characterId(1L)
                .characterName("메인캐릭터")
                .characterLevel(270)
                .bossSelections(List.of(bossSelection))
                .build();

        request = RecommendationRequest.builder()
                .userId(1L)
                .characterBossSelections(List.of(characterBossSelection))
                .build();

        // 응답 데이터 준비
        BossRecommendation bossRecommendation = BossRecommendation.builder()
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

        CharacterRecommendation characterRecommendation = CharacterRecommendation.builder()
                .characterId(1L)
                .characterName("메인캐릭터")
                .characterLevel(270)
                .crystalCount(1)
                .expectedIncome(BigInteger.valueOf(5000000))
                .bossRecommendations(List.of(bossRecommendation))
                .highestDifficultySoloBossId(1L)
                .partyBossIds(List.of())
                .build();

        OptimizationSummary optimizationSummary = OptimizationSummary.builder()
                .isOptimized(true)
                .optimizationMessage("최적화 완료")
                .totalPartyBossCount(0)
                .totalSoloBossCount(1)
                .charactersWithMaxCrystal(0)
                .isWorldCrystalLimitReached(false)
                .build();

        response = RecommendationResponse.builder()
                .userId(1L)
                .totalExpectedIncome(BigInteger.valueOf(5000000))
                .totalCrystalCount(1)
                .totalBossCount(1)
                .characterRecommendations(List.of(characterRecommendation))
                .optimizationSummary(optimizationSummary)
                .build();
    }

    @Test
    @DisplayName("최적화 추천 생성 성공")
    void generateOptimizedRecommendation_Success() throws Exception {
        // given
        given(recommendationService.generateRecommendation(any(RecommendationRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/recommendation/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.totalExpectedIncome").value(5000000))
                .andExpect(jsonPath("$.data.totalCrystalCount").value(1))
                .andExpect(jsonPath("$.data.totalBossCount").value(1))
                .andExpect(jsonPath("$.data.characterRecommendations[0].characterId").value(1))
                .andExpect(jsonPath("$.data.optimizationSummary.optimized").value(true));
    }

    @Test
    @DisplayName("userId가 null인 경우 유효성 검사 실패")
    void generateOptimizedRecommendation_UserIdNull_ValidationFailed() throws Exception {
        // given
        RecommendationRequest invalidRequest = RecommendationRequest.builder()
                .userId(null)
                .characterBossSelections(List.of())
                .build();

        // when & then
        mockMvc.perform(post("/api/recommendation/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("characterBossSelections가 null인 경우 유효성 검사 실패")
    void generateOptimizedRecommendation_CharacterBossSelectionsNull_ValidationFailed() throws Exception {
        // given
        RecommendationRequest invalidRequest = RecommendationRequest.builder()
                .userId(1L)
                .characterBossSelections(null)
                .build();

        // when & then
        mockMvc.perform(post("/api/recommendation/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("헬스 체크 성공")
    void healthCheck_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/recommendation/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value("Recommendation service is running"));
    }

    @Test
    @DisplayName("잘못된 HTTP 메서드 사용")
    void generateOptimizedRecommendation_WrongHttpMethod() throws Exception {
        // when & then
        mockMvc.perform(get("/api/recommendation/optimize"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("잘못된 Content-Type")
    void generateOptimizedRecommendation_WrongContentType() throws Exception {
        // when & then
        mockMvc.perform(post("/api/recommendation/optimize")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }
} 