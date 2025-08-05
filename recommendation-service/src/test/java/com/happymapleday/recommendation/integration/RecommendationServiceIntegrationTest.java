package com.happymapleday.recommendation.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.common.client.BossServiceClient;
import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.request.RecommendationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class RecommendationServiceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BossServiceClient bossServiceClient;

    private MockMvc mockMvc;
    private List<BossResponse> bossResponses;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 보스 응답 데이터 준비
        BossResponse soloBossResponse = BossResponse.builder()
                .id(1L)
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(10000L)
                .maxPartySize(6)
                .build();

        BossResponse partyBossResponse = BossResponse.builder()
                .id(2L)
                .bossName("루타비스")
                .difficulty("하드")
                .crystalPrice(200000L)
                .maxPartySize(6)
                .build();

        BossResponse soloBoss2Response = BossResponse.builder()
                .id(3L)
                .bossName("힐라")
                .difficulty("하드")
                .crystalPrice(50000L)
                .maxPartySize(6)
                .build();

        bossResponses = List.of(soloBossResponse, partyBossResponse, soloBoss2Response);

        // BossServiceClient 모킹
        given(bossServiceClient.getBossList())
                .willReturn(ApiResponse.success(bossResponses));
    }

    @Test
    @DisplayName("추천 생성 통합 테스트")
    void generateRecommendation_IntegrationTest() throws Exception {
        // given
        BossSelection soloBoss = BossSelection.builder()
                .bossId(1L)
                .partySize(1)
                .build();

        BossSelection partyBoss = BossSelection.builder()
                .bossId(2L)
                .partySize(3)
                .build();

        CharacterBossSelection characterBossSelection = CharacterBossSelection.builder()
                .characterId(1L)
                .characterName("메인캐릭터")
                .characterLevel(270)
                .bossSelections(List.of(soloBoss, partyBoss))
                .build();

        RecommendationRequest request = RecommendationRequest.builder()
                .userId(1L)
                .characterBossSelections(List.of(characterBossSelection))
                .build();

        // when & then
        mockMvc.perform(post("/api/recommendation/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.characterRecommendations").isArray())
                .andExpect(jsonPath("$.data.characterRecommendations[0].characterId").value(1))
                .andExpect(jsonPath("$.data.characterRecommendations[0].characterName").value("메인캐릭터"))
                .andExpect(jsonPath("$.data.characterRecommendations[0].characterLevel").value(270))
                .andExpect(jsonPath("$.data.optimizationSummary").exists())
                .andExpect(jsonPath("$.data.totalExpectedIncome").exists())
                .andExpect(jsonPath("$.data.totalCrystalCount").exists())
                .andExpect(jsonPath("$.data.totalBossCount").exists());
    }

    @Test
    @DisplayName("복수 캐릭터 추천 생성 통합 테스트")
    void generateRecommendation_MultipleCharacters_IntegrationTest() throws Exception {
        // given
        BossSelection soloBoss1 = BossSelection.builder()
                .bossId(1L)
                .partySize(1)
                .build();

        BossSelection soloBoss2 = BossSelection.builder()
                .bossId(3L)
                .partySize(1)
                .build();

        CharacterBossSelection character1 = CharacterBossSelection.builder()
                .characterId(1L)
                .characterName("메인캐릭터")
                .characterLevel(270)
                .bossSelections(List.of(soloBoss1))
                .build();

        CharacterBossSelection character2 = CharacterBossSelection.builder()
                .characterId(2L)
                .characterName("서브캐릭터")
                .characterLevel(260)
                .bossSelections(List.of(soloBoss2))
                .build();

        RecommendationRequest request = RecommendationRequest.builder()
                .userId(1L)
                .characterBossSelections(List.of(character1, character2))
                .build();

        // when & then
        mockMvc.perform(post("/api/recommendation/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.characterRecommendations").isArray())
                .andExpect(jsonPath("$.data.characterRecommendations").isNotEmpty())
                .andExpect(jsonPath("$.data.characterRecommendations[0].characterId").value(1))
                .andExpect(jsonPath("$.data.characterRecommendations[1].characterId").value(2));
    }

    @Test
    @DisplayName("헬스 체크 통합 테스트")
    void healthCheck_IntegrationTest() throws Exception {
        // when & then
        mockMvc.perform(get("/api/recommendation/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value("Recommendation service is running"));
    }

    @Test
    @DisplayName("유효하지 않은 요청 데이터 통합 테스트")
    void generateRecommendation_InvalidRequest_IntegrationTest() throws Exception {
        // given
        RecommendationRequest invalidRequest = RecommendationRequest.builder()
                .userId(null)
                .characterBossSelections(null)
                .build();

        // when & then
        mockMvc.perform(post("/api/recommendation/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("빈 캐릭터 선택 목록 통합 테스트")
    void generateRecommendation_EmptyCharacterSelections_IntegrationTest() throws Exception {
        // given
        RecommendationRequest request = RecommendationRequest.builder()
                .userId(1L)
                .characterBossSelections(List.of())
                .build();

        // when & then
        mockMvc.perform(post("/api/recommendation/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.characterRecommendations").isArray())
                .andExpect(jsonPath("$.data.characterRecommendations").isEmpty())
                .andExpect(jsonPath("$.data.totalExpectedIncome").value(0))
                .andExpect(jsonPath("$.data.totalCrystalCount").value(0))
                .andExpect(jsonPath("$.data.totalBossCount").value(0));
    }

    @Test
    @DisplayName("캐릭터에 빈 보스 선택 목록 통합 테스트")
    void generateRecommendation_EmptyBossSelections_IntegrationTest() throws Exception {
        // given
        CharacterBossSelection character = CharacterBossSelection.builder()
                .characterId(1L)
                .characterName("메인캐릭터")
                .characterLevel(270)
                .bossSelections(List.of())
                .build();

        RecommendationRequest request = RecommendationRequest.builder()
                .userId(1L)
                .characterBossSelections(List.of(character))
                .build();

        // when & then
        mockMvc.perform(post("/api/recommendation/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.characterRecommendations").isArray())
                .andExpect(jsonPath("$.data.characterRecommendations").isNotEmpty())
                .andExpect(jsonPath("$.data.characterRecommendations[0].characterId").value(1))
                .andExpect(jsonPath("$.data.characterRecommendations[0].crystalCount").value(0))
                .andExpect(jsonPath("$.data.characterRecommendations[0].bossRecommendations").isEmpty());
    }
} 