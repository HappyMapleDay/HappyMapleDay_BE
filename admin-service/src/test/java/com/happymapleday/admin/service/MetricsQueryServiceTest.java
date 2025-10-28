package com.happymapleday.admin.service;

import com.happymapleday.admin.dto.response.*;
import com.happymapleday.admin.entity.*;
import com.happymapleday.admin.enums.BatchStatus;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("MetricsQueryService 테스트")
class MetricsQueryServiceTest {

    @InjectMocks
    private MetricsQueryService metricsQueryService;

    @Mock
    private UserMetricsRepository userMetricsRepository;

    @Mock
    private BossKillMetricsRepository bossKillMetricsRepository;

    @Mock
    private BossCombatPowerMetricsRepository bossCombatPowerMetricsRepository;

    @Mock
    private ItemDropMetricsRepository itemDropMetricsRepository;

    @Mock
    private ItemPriceMetricsRepository itemPriceMetricsRepository;

    @Mock
    private BatchJobStatusRepository batchJobStatusRepository;

    @Mock
    private BatchExecutionHistoryRepository batchExecutionHistoryRepository;

    private LocalDate from;
    private LocalDate to;

    @BeforeEach
    void setUp() {
        from = LocalDate.of(2024, 1, 1);
        to = LocalDate.of(2024, 1, 7);
    }

    @Test
    @DisplayName("유저 통계 조회 - 날짜 범위 지정")
    void getUserMetrics_WithDateRange() {
        // given
        List<UserMetrics> metrics = List.of(
            createUserMetrics(LocalDate.of(2024, 1, 1), 1000L, 50),
            createUserMetrics(LocalDate.of(2024, 1, 2), 1050L, 50)
        );

        given(userMetricsRepository.findByMetricDateBetweenOrderByMetricDateAsc(from, to))
            .willReturn(metrics);

        // when
        UserMetricsResponse response = metricsQueryService.getUserMetrics(from, to, "daily");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserCounts()).hasSize(2);
        assertThat(response.getTotalActiveUsers()).isEqualTo(1000L);
        assertThat(response.getPeriod()).isEqualTo("daily");
        verify(userMetricsRepository).findByMetricDateBetweenOrderByMetricDateAsc(from, to);
    }

    @Test
    @DisplayName("유저 통계 조회 - 날짜 미지정 (전체)")
    void getUserMetrics_WithoutDateRange() {
        // given
        List<UserMetrics> metrics = List.of(
            createUserMetrics(LocalDate.of(2024, 1, 2), 1050L, 50),
            createUserMetrics(LocalDate.of(2024, 1, 1), 1000L, 50)
        );

        given(userMetricsRepository.findAllByOrderByMetricDateDesc())
            .willReturn(metrics);

        // when
        UserMetricsResponse response = metricsQueryService.getUserMetrics(null, null, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserCounts()).hasSize(2);
        assertThat(response.getTotalActiveUsers()).isEqualTo(1050L);
        verify(userMetricsRepository).findAllByOrderByMetricDateDesc();
    }

    @Test
    @DisplayName("보스 격파 횟수 조회 - 특정 보스, 날짜 범위")
    void getBossKillMetrics_WithBossIdAndDateRange() {
        // given
        Long bossId = 1L;
        List<BossKillMetrics> metrics = List.of(
            createBossKillMetrics(LocalDate.of(2024, 1, 4), bossId, "노말 힐라", 100L)
        );

        given(bossKillMetricsRepository.findByBossIdAndMetricDateBetweenOrderByMetricDateAsc(bossId, from, to))
            .willReturn(metrics);

        // when
        List<BossKillMetricsResponse> responses = metricsQueryService.getBossKillMetrics(bossId, from, to);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getBossId()).isEqualTo(bossId);
        assertThat(responses.get(0).getBossName()).isEqualTo("노말 힐라");
        assertThat(responses.get(0).getTotalKills()).isEqualTo(100L);
    }

    @Test
    @DisplayName("보스 전투력 조회 - 전체 보스, 날짜 범위")
    void getBossCombatPowerMetrics_AllBosses() {
        // given
        List<BossCombatPowerMetrics> metrics = List.of(
            createBossCombatPowerMetrics(LocalDate.of(2024, 1, 4), 1L, "히어로", new BigDecimal("85000.50"))
        );

        given(bossCombatPowerMetricsRepository.findByMetricDateBetweenOrderByMetricDateAsc(from, to))
            .willReturn(metrics);

        // when
        List<BossCombatPowerMetricsResponse> responses = metricsQueryService.getBossCombatPowerMetrics(null, from, to);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCharacterClass()).isEqualTo("히어로");
        assertThat(responses.get(0).getAvgCombatPower()).isEqualByComparingTo(new BigDecimal("85000.50"));
    }

    @Test
    @DisplayName("아이템 드롭 조회 - 보스ID와 아이템ID 모두 지정")
    void getItemDropMetrics_WithBothIds() {
        // given
        Long bossId = 1L;
        Long itemId = 101L;
        List<ItemDropMetrics> metrics = List.of(
            createItemDropMetrics(LocalDate.of(2024, 1, 4), bossId, itemId, 456L)
        );

        given(itemDropMetricsRepository.findByBossIdAndItemIdAndMetricDateBetweenOrderByMetricDateAsc(
            bossId, itemId, from, to))
            .willReturn(metrics);

        // when
        List<ItemDropMetricsResponse> responses = metricsQueryService.getItemDropMetrics(bossId, itemId, from, to);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getBossId()).isEqualTo(bossId);
        assertThat(responses.get(0).getItemId()).isEqualTo(itemId);
        assertThat(responses.get(0).getDropCount()).isEqualTo(456L);
    }

    @Test
    @DisplayName("아이템 가격 조회 - 아이템ID만 지정")
    void getItemPriceMetrics_WithItemIdOnly() {
        // given
        Long itemId = 101L;
        List<ItemPriceMetrics> metrics = List.of(
            createItemPriceMetrics(LocalDate.of(2024, 1, 4), null, itemId, new BigDecimal("5000000.50"))
        );

        given(itemPriceMetricsRepository.findByItemIdAndMetricDateBetweenOrderByMetricDateAsc(
            itemId, from, to))
            .willReturn(metrics);

        // when
        List<ItemPriceMetricsResponse> responses = metricsQueryService.getItemPriceMetrics(null, itemId, from, to);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getItemId()).isEqualTo(itemId);
        assertThat(responses.get(0).getAvgPrice()).isEqualByComparingTo(new BigDecimal("5000000.50"));
    }

    @Test
    @DisplayName("배치 상태 조회")
    void getBatchStatus() {
        // given
        List<BatchJobStatus> statuses = List.of(
            createBatchJobStatus(BatchType.USER_METRICS, BatchStatus.SUCCESS),
            createBatchJobStatus(BatchType.BOSS_KILLS, BatchStatus.PENDING)
        );

        given(batchJobStatusRepository.findAll()).willReturn(statuses);

        // when
        List<BatchStatusResponse> responses = metricsQueryService.getBatchStatus();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getBatchType()).isEqualTo(BatchType.USER_METRICS);
        assertThat(responses.get(0).getStatus()).isEqualTo(BatchStatus.SUCCESS);
        assertThat(responses.get(1).getBatchType()).isEqualTo(BatchType.BOSS_KILLS);
        assertThat(responses.get(1).getStatus()).isEqualTo(BatchStatus.PENDING);
    }

    // Helper methods
    private UserMetrics createUserMetrics(LocalDate date, Long cumulativeCount, Integer dailyCount) {
        return UserMetrics.builder()
            .metricDate(date)
            .cumulativeCount(cumulativeCount)
            .dailyCount(dailyCount)
            .build();
    }

    private BossKillMetrics createBossKillMetrics(LocalDate date, Long bossId, String bossName, Long totalKills) {
        return BossKillMetrics.builder()
            .metricDate(date)
            .bossId(bossId)
            .bossName(bossName)
            .bossNameEn("Normal Hilla")
            .difficulty("NORMAL")
            .totalKills(totalKills)
            .build();
    }

    private BossCombatPowerMetrics createBossCombatPowerMetrics(
        LocalDate date, Long bossId, String characterClass, BigDecimal avgCombatPower
    ) {
        return BossCombatPowerMetrics.builder()
            .metricDate(date)
            .bossId(bossId)
            .bossName("노말 힐라")
            .bossNameEn("Normal Hilla")
            .difficulty("NORMAL")
            .characterClass(characterClass)
            .avgCombatPower(avgCombatPower)
            .build();
    }

    private ItemDropMetrics createItemDropMetrics(LocalDate date, Long bossId, Long itemId, Long dropCount) {
        return ItemDropMetrics.builder()
            .metricDate(date)
            .bossId(bossId)
            .bossName(bossId != null ? "노말 힐라" : null)
            .bossNameEn(bossId != null ? "Normal Hilla" : null)
            .difficulty(bossId != null ? "NORMAL" : null)
            .itemId(itemId)
            .itemName("힐라의 분노")
            .itemNameEn("Hilla's Rage")
            .dropCount(dropCount)
            .build();
    }

    private ItemPriceMetrics createItemPriceMetrics(LocalDate date, Long bossId, Long itemId, BigDecimal avgPrice) {
        return ItemPriceMetrics.builder()
            .metricDate(date)
            .bossId(bossId)
            .bossName(bossId != null ? "노말 힐라" : null)
            .bossNameEn(bossId != null ? "Normal Hilla" : null)
            .difficulty(bossId != null ? "NORMAL" : null)
            .itemId(itemId)
            .itemName("힐라의 분노")
            .itemNameEn("Hilla's Rage")
            .avgPrice(avgPrice)
            .build();
    }

    private BatchJobStatus createBatchJobStatus(BatchType batchType, BatchStatus status) {
        return BatchJobStatus.builder()
            .batchType(batchType)
            .status(status)
            .recordCount(100)
            .message("테스트 메시지")
            .durationMs(1500L)
            .build();
    }
}

