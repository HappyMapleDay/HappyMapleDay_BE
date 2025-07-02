package com.happymapleday.boss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.boss.dto.response.BossResponse;
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
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].bossName").value("자쿰"));
    }


} 