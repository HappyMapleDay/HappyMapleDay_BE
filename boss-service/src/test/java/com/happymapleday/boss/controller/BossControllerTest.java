package com.happymapleday.boss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.boss.dto.response.BossResponse;
import com.happymapleday.boss.dto.response.DesireItemResponse;
import com.happymapleday.boss.entity.ForceType;
import com.happymapleday.boss.service.BossService;
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
    private DesireItemService desireItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private BossResponse bossResponse;

    @BeforeEach
    void setUp() {
        bossResponse = BossResponse.builder()
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
    }

    @Test
    @DisplayName("보스 목록 조회 API")
    void getBossList() throws Exception {
        // given
        List<BossResponse> bosses = Arrays.asList(bossResponse);
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
        List<DesireItemResponse> desireItems = Arrays.asList(
                DesireItemResponse.builder()
                        .id(1L)
                        .itemName("홍옥의 보스 반지 상자")
                        .isRandomBox(true)
                        .bossId(1L)
                        .build(),
                DesireItemResponse.builder()
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
} 