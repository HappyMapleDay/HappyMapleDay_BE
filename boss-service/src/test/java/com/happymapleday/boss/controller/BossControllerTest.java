package com.happymapleday.boss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.boss.dto.BossDto;
import com.happymapleday.boss.dto.BossPresetDto;
import com.happymapleday.boss.dto.DesireItemDto;
import com.happymapleday.boss.entity.ForceType;
import com.happymapleday.boss.service.BossService;
import com.happymapleday.boss.service.BossPresetService;
import com.happymapleday.boss.service.DesireItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BossController.class)
@DisplayName("BossController Test")
class BossControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BossService bossService;

    @MockBean
    private BossPresetService bossPresetService;

    @MockBean
    private DesireItemService desireItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private BossDto.Response bossResponse;
    private BossPresetDto.ValidateLimitsRequest validateRequest;

    @BeforeEach
    void setUp() {
        bossResponse = BossDto.Response.builder()
                .id(1L)
                .bossName("자쿰")
                .difficulty("카오스")
                .crystalPrice(8080000L)
                .maxPartySize(6)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(90)
                .requiredForceType(ForceType.NONE)
                .requiredForceAmount(0)
                .build();

        List<BossPresetDto.ValidateLimitsRequest.SelectedBoss> selectedBosses = Arrays.asList(
                BossPresetDto.ValidateLimitsRequest.SelectedBoss.builder()
                        .characterId(1L)
                        .bossId(1L)
                        .build(),
                BossPresetDto.ValidateLimitsRequest.SelectedBoss.builder()
                        .characterId(1L)
                        .bossId(2L)
                        .build()
        );

        validateRequest = BossPresetDto.ValidateLimitsRequest.builder()
                .userId(1L)
                .selectedBosses(selectedBosses)
                .build();
    }

    @Test
    @DisplayName("보스 목록 조회 API")
    void getBossList() throws Exception {
        // given
        List<BossDto.Response> bosses = Arrays.asList(bossResponse);
        given(bossService.getAllActiveBosses()).willReturn(bosses);

        // when & then
        mockMvc.perform(get("/api/boss/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("보스 목록 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].bossName").value("자쿰"));
    }

    @Test
    @DisplayName("보스 물욕템 조회 API")
    void getBossDesireItems() throws Exception {
        // given
        List<DesireItemDto.Response> desireItems = Arrays.asList(
                DesireItemDto.Response.builder()
                        .id(1L)
                        .itemName("홍옥의 보스 반지 상자")
                        .isRandomBox(true)
                        .bossId(1L)
                        .build(),
                DesireItemDto.Response.builder()
                        .id(2L)
                        .itemName("손상된 블랙 하트")
                        .isRandomBox(false)
                        .bossId(1L)
                        .build()
        );
        given(desireItemService.getDesireItemsByBossId(anyLong())).willReturn(desireItems);

        // when & then
        mockMvc.perform(get("/api/boss/{bossId}/desire-items", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("보스 물욕템 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].itemName").value("홍옥의 보스 반지 상자"));
    }

    @Test
    @DisplayName("보스 선택 제한 검증 API")
    void validateBossLimits() throws Exception {
        // given
        BossPresetDto.ValidateLimitsResponse response = BossPresetDto.ValidateLimitsResponse.builder()
                .isValid(true)
                .characterLimitStatus(Map.of())
                .serverLimitStatus(BossPresetDto.ValidateLimitsResponse.ServerLimitStatus.builder()
                        .current(2)
                        .limit(90)
                        .remaining(88)
                        .build())
                .violations(Arrays.asList())
                .build();
        given(bossPresetService.validateLimits(any(BossPresetDto.ValidateLimitsRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/boss/validate-limits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("보스 선택 제한 검증이 완료되었습니다."))
                .andExpect(jsonPath("$.data.isValid").value(true));
    }
} 