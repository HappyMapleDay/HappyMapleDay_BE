package com.happymapleday.boss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.boss.dto.BossDto;
import com.happymapleday.boss.entity.ForceType;
import com.happymapleday.boss.service.BossService;
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

import static org.mockito.ArgumentMatchers.any;
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

    @Autowired
    private ObjectMapper objectMapper;

    private BossDto.Response bossResponse;
    private BossDto.CreateRequest createRequest;

    @BeforeEach
    void setUp() {
        bossResponse = BossDto.Response.builder()
                .id(1L)
                .bossName("Zakum Chaos")
                .difficulty("Hard")
                .crystalPrice(12960000L)
                .maxPartySize(6)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(50)
                .requiredForceType(ForceType.NONE)
                .requiredForceAmount(0)
                .build();

        createRequest = BossDto.CreateRequest.builder()
                .bossName("Lucid Hard")
                .difficulty("Hard")
                .crystalPrice(81000000L)
                .isMonthly(false)
                .minEntryLevel(200)
                .requiredForceType(ForceType.ARCANE)
                .requiredForceAmount(320)
                .build();
    }

    @Test
    @DisplayName("Get all active bosses API")
    void getAllActiveBosses() throws Exception {
        // given
        List<BossDto.Response> bosses = Arrays.asList(bossResponse);
        given(bossService.getAllActiveBosses()).willReturn(bosses);

        // when & then
        mockMvc.perform(get("/api/v1/bosses"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("보스 목록 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].bossName").value("Zakum Chaos"));
    }

    @Test
    @DisplayName("Get boss by ID API")
    void getBossById() throws Exception {
        // given
        given(bossService.getBossById(1L)).willReturn(bossResponse);

        // when & then
        mockMvc.perform(get("/api/v1/bosses/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.bossName").value("Zakum Chaos"));
    }

    @Test
    @DisplayName("Create boss API")
    void createBoss() throws Exception {
        // given
        given(bossService.createBoss(any(BossDto.CreateRequest.class))).willReturn(bossResponse);

        // when & then
        mockMvc.perform(post("/api/v1/bosses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.bossName").value("Zakum Chaos"));
    }

    @Test
    @DisplayName("Search bosses by name API")
    void searchBossesByName() throws Exception {
        // given
        List<BossDto.Response> bosses = Arrays.asList(bossResponse);
        given(bossService.searchBossesByName("Zakum")).willReturn(bosses);

        // when & then
        mockMvc.perform(get("/api/v1/bosses/search")
                        .param("bossName", "Zakum"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].bossName").value("Zakum Chaos"));
    }
} 