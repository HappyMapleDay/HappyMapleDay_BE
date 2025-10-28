package com.happymapleday.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.admin.dto.request.BatchExecuteRequest;
import com.happymapleday.admin.dto.response.BatchStatusResponse;
import com.happymapleday.admin.enums.BatchStatus;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BatchController.class)
@DisplayName("BatchController 통합 테스트")
class BatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserMetricsBatchService userMetricsBatchService;

    @MockBean
    private BossKillMetricsBatchService bossKillMetricsBatchService;

    @MockBean
    private BossCombatPowerMetricsBatchService bossCombatPowerMetricsBatchService;

    @MockBean
    private ItemDropMetricsBatchService itemDropMetricsBatchService;

    @MockBean
    private ItemPriceMetricsBatchService itemPriceMetricsBatchService;

    @MockBean
    private BatchCoordinatorService batchCoordinatorService;

    @MockBean
    private MetricsQueryService metricsQueryService;

    @Test
    @DisplayName("배치 상태 조회 - 인증 없이 접근 시 401")
    void getBatchStatus_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/batch/status"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("배치 상태 조회 - 성공")
    void getBatchStatus_Success() throws Exception {
        // given
        List<BatchStatusResponse> responses = List.of(
            BatchStatusResponse.builder()
                .batchType(BatchType.USER_METRICS)
                .lastExecutedAt(LocalDateTime.now())
                .status(BatchStatus.SUCCESS)
                .recordCount(100)
                .message("가입 유저 수 집계 완료")
                .durationMs(1500L)
                .build()
        );

        given(metricsQueryService.getBatchStatus()).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/admin/batch/status")
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].batchType").value("USER_METRICS"))
            .andExpect(jsonPath("$.data[0].status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("개별 배치 수동 실행 - 성공")
    void executeBatch_Success() throws Exception {
        // given
        BatchExecuteRequest request = new BatchExecuteRequest();
        request.setBatchType(BatchType.USER_METRICS);
        request.setFrom(LocalDate.of(2024, 1, 1));
        request.setTo(LocalDate.of(2024, 1, 7));

        BatchExecutor.BatchExecutionResult result = new BatchExecutor.BatchExecutionResult(
            BatchType.USER_METRICS,
            BatchStatus.SUCCESS,
            LocalDateTime.now(),
            100,
            "가입 유저 수 집계 완료",
            1500L
        );

        given(userMetricsBatchService.executeBatch(any(), any(), any()))
            .willReturn(result);

        // when & then
        mockMvc.perform(post("/api/admin/batch/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.batchType").value("USER_METRICS"))
            .andExpect(jsonPath("$.data.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data.recordCount").value(100));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("전체 배치 일괄 실행 - 성공")
    void executeAllBatches_Success() throws Exception {
        // given
        List<BatchExecutor.BatchExecutionResult> results = List.of(
            new BatchExecutor.BatchExecutionResult(
                BatchType.USER_METRICS,
                BatchStatus.SUCCESS,
                LocalDateTime.now(),
                100,
                "가입 유저 수 집계 완료",
                1500L
            ),
            new BatchExecutor.BatchExecutionResult(
                BatchType.BOSS_KILLS,
                BatchStatus.SUCCESS,
                LocalDateTime.now(),
                50,
                "보스 격파 횟수 집계 완료",
                2000L
            )
        );

        given(batchCoordinatorService.executeAllBatches(any(), any()))
            .willReturn(results);

        // when & then
        mockMvc.perform(post("/api/admin/batch/execute-all")
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.results").isArray())
            .andExpect(jsonPath("$.data.results[0].batchType").value("USER_METRICS"))
            .andExpect(jsonPath("$.data.results[1].batchType").value("BOSS_KILLS"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("배치 이력 조회 - 성공")
    void getBatchHistory_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/admin/batch/history")
                .param("page", "0")
                .param("size", "20")
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"));
    }
}

