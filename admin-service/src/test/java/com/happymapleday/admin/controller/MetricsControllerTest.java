package com.happymapleday.admin.controller;

import com.happymapleday.admin.dto.response.*;
import com.happymapleday.admin.service.MetricsQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MetricsController.class)
@DisplayName("MetricsController 통합 테스트")
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MetricsQueryService metricsQueryService;

    @Test
    @DisplayName("유저 통계 조회 - 인증 없이 접근 시 401")
    void getUserMetrics_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/metrics/users"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("유저 통계 조회 - 성공")
    void getUserMetrics_Success() throws Exception {
        // given
        UserMetricsResponse response = UserMetricsResponse.builder()
            .userCounts(List.of(
                UserMetricsResponse.UserCountData.builder()
                    .date(LocalDate.of(2024, 1, 1))
                    .cumulativeCount(1000L)
                    .dailyCount(50)
                    .build()
            ))
            .totalActiveUsers(1000L)
            .period("daily")
            .build();

        given(metricsQueryService.getUserMetrics(any(), any(), eq("daily")))
            .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/admin/metrics/users")
                .param("from", "2024-01-01")
                .param("to", "2024-01-07")
                .param("period", "daily")
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.userCounts").isArray())
            .andExpect(jsonPath("$.data.totalActiveUsers").value(1000))
            .andExpect(jsonPath("$.data.period").value("daily"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("보스 격파 횟수 조회 - 성공")
    void getBossKillMetrics_Success() throws Exception {
        // given
        List<BossKillMetricsResponse> responses = List.of(
            BossKillMetricsResponse.builder()
                .metricDate(LocalDate.of(2024, 1, 4))
                .bossId(1L)
                .bossName("노말 힐라")
                .bossNameEn("Normal Hilla")
                .difficulty("NORMAL")
                .totalKills(523L)
                .build()
        );

        given(metricsQueryService.getBossKillMetrics(eq(1L), any(), any()))
            .willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/admin/metrics/bosses/kills")
                .param("bossId", "1")
                .param("from", "2024-01-01")
                .param("to", "2024-01-07")
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].bossId").value(1))
            .andExpect(jsonPath("$.data[0].bossName").value("노말 힐라"))
            .andExpect(jsonPath("$.data[0].totalKills").value(523));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("보스 전투력 평균 조회 - 성공")
    void getBossCombatPowerMetrics_Success() throws Exception {
        // given
        List<BossCombatPowerMetricsResponse> responses = List.of(
            BossCombatPowerMetricsResponse.builder()
                .metricDate(LocalDate.of(2024, 1, 4))
                .bossId(1L)
                .bossName("노말 힐라")
                .bossNameEn("Normal Hilla")
                .difficulty("NORMAL")
                .characterClass("히어로")
                .avgCombatPower(new BigDecimal("85000.50"))
                .build()
        );

        given(metricsQueryService.getBossCombatPowerMetrics(any(), any(), any()))
            .willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/admin/metrics/bosses/combat-power")
                .param("from", "2024-01-01")
                .param("to", "2024-01-07")
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].characterClass").value("히어로"))
            .andExpect(jsonPath("$.data[0].avgCombatPower").value(85000.50));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("아이템 드롭 조회 - 성공")
    void getItemDropMetrics_Success() throws Exception {
        // given
        List<ItemDropMetricsResponse> responses = List.of(
            ItemDropMetricsResponse.builder()
                .metricDate(LocalDate.of(2024, 1, 4))
                .bossId(1L)
                .bossName("노말 힐라")
                .bossNameEn("Normal Hilla")
                .difficulty("NORMAL")
                .itemId(101L)
                .itemName("힐라의 분노")
                .itemNameEn("Hilla's Rage")
                .dropCount(456L)
                .build()
        );

        given(metricsQueryService.getItemDropMetrics(any(), any(), any(), any()))
            .willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/admin/metrics/bosses/items/drops")
                .param("bossId", "1")
                .param("itemId", "101")
                .param("from", "2024-01-01")
                .param("to", "2024-01-07")
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].itemName").value("힐라의 분노"))
            .andExpect(jsonPath("$.data[0].dropCount").value(456));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("아이템 가격 조회 - 성공")
    void getItemPriceMetrics_Success() throws Exception {
        // given
        List<ItemPriceMetricsResponse> responses = List.of(
            ItemPriceMetricsResponse.builder()
                .metricDate(LocalDate.of(2024, 1, 4))
                .bossId(1L)
                .bossName("노말 힐라")
                .bossNameEn("Normal Hilla")
                .difficulty("NORMAL")
                .itemId(101L)
                .itemName("힐라의 분노")
                .itemNameEn("Hilla's Rage")
                .avgPrice(new BigDecimal("5000000.50"))
                .build()
        );

        given(metricsQueryService.getItemPriceMetrics(any(), any(), any(), any()))
            .willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/admin/metrics/bosses/items/prices")
                .param("from", "2024-01-01")
                .param("to", "2024-01-07")
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].avgPrice").value(5000000.50));
    }
}

