package com.happymapleday.boss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.boss.dto.response.DesireItemResponse;
import com.happymapleday.boss.service.BossDropItemService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DesireItemController.class)
@DisplayName("DesireItemController 테스트")
class DesireItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BossDropItemService bossDropItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private DesireItemResponse desireItemResponse1;
    private DesireItemResponse desireItemResponse2;

    @BeforeEach
    void setUp() {
        desireItemResponse1 = DesireItemResponse.builder()
                .id(1L)
                .itemName("홍옥의 보스 반지 상자")
                .isRandomBox(true)
                .bossId(1L)
                .bossName("자쿰")
                .build();

        desireItemResponse2 = DesireItemResponse.builder()
                .id(2L)
                .itemName("손상된 블랙 하트")
                .isRandomBox(false)
                .bossId(1L)
                .bossName("자쿰")
                .build();
    }

    @Test
    @DisplayName("보스별 물욕템 목록 조회 API - 성공")
    void getDesireItemsByBossId_Success() throws Exception {
        // given
        Long bossId = 1L;
        List<DesireItemResponse> desireItems = Arrays.asList(desireItemResponse1, desireItemResponse2);
        given(bossDropItemService.getDropItemsByBossId(bossId)).willReturn(desireItems);

        // when & then
        mockMvc.perform(get("/api/boss/desire-items/boss/{bossId}", bossId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].itemName").value("홍옥의 보스 반지 상자"))
                .andExpect(jsonPath("$.data[0].isRandomBox").value(true))
                .andExpect(jsonPath("$.data[0].bossId").value(1L))
                .andExpect(jsonPath("$.data[0].bossName").value("자쿰"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].itemName").value("손상된 블랙 하트"))
                .andExpect(jsonPath("$.data[1].isRandomBox").value(false));
    }

    @Test
    @DisplayName("보스별 물욕템 목록 조회 API - 빈 목록")
    void getDesireItemsByBossId_EmptyList() throws Exception {
        // given
        Long bossId = 999L;
        given(bossDropItemService.getDropItemsByBossId(bossId)).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/boss/desire-items/boss/{bossId}", bossId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("보스별 물욕템 목록 조회 API - 서버 오류")
    void getDesireItemsByBossId_ServerError() throws Exception {
        // given
        Long bossId = 1L;
        given(bossDropItemService.getDropItemsByBossId(bossId)).willThrow(new RuntimeException("서버 오류"));

        // when & then
        mockMvc.perform(get("/api/boss/desire-items/boss/{bossId}", bossId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("물욕템 목록 조회 중 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("보스별 물욕템 목록 조회 API - 잘못된 bossId")
    void getDesireItemsByBossId_InvalidBossId() throws Exception {
        // given
        String invalidBossId = "invalid";

        // when & then
        mockMvc.perform(get("/api/boss/desire-items/boss/{bossId}", invalidBossId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
} 