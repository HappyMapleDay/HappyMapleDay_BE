package com.happymapleday.boss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.boss.dto.response.BossPresetResponse;
import com.happymapleday.boss.service.BossPresetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BossPresetController.class)
@DisplayName("BossPresetController 테스트")
class BossPresetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BossPresetService bossPresetService;

    @Autowired
    private ObjectMapper objectMapper;

    private BossPresetResponse bossPresetResponse;

    @BeforeEach
    void setUp() {
        bossPresetResponse = BossPresetResponse.builder()
                .id(1L)
                .presetName("일일 보스")
                .bossIds(Collections.emptyList())
                .bossCount(0)
                .bosses(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("보스 프리셋 목록 조회 API - 성공")
    void getAllPresets_Success() throws Exception {
        // given
        List<BossPresetResponse> presets = Arrays.asList(bossPresetResponse);
        given(bossPresetService.getAllPresetsWithBosses()).willReturn(presets);

        // when & then
        mockMvc.perform(get("/api/boss/preset/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].presetName").value("일일 보스"))
                .andExpect(jsonPath("$.data[0].bossCount").value(0))
                .andExpect(jsonPath("$.data[0].bossIds").isArray())
                .andExpect(jsonPath("$.data[0].bosses").isArray());
    }

    @Test
    @DisplayName("보스 프리셋 목록 조회 API - 빈 목록")
    void getAllPresets_EmptyList() throws Exception {
        // given
        given(bossPresetService.getAllPresetsWithBosses()).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/boss/preset/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("보스 프리셋 목록 조회 API - 서버 오류")
    void getAllPresets_ServerError() throws Exception {
        // given
        given(bossPresetService.getAllPresetsWithBosses()).willThrow(new RuntimeException("서버 오류"));

        // when & then
        mockMvc.perform(get("/api/boss/preset/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("프리셋 목록 조회 중 오류가 발생했습니다."));
    }
} 