package com.happymapleday.admin.service;

import com.happymapleday.admin.client.BossServiceClient;
import com.happymapleday.admin.client.SettlementServiceClient;
import com.happymapleday.admin.dto.external.BossInfoDto;
import com.happymapleday.admin.dto.external.SettlementBossKillDto;
import com.happymapleday.admin.entity.BossKillMetrics;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.enums.ExecutionType;
import com.happymapleday.admin.repository.BossKillMetricsRepository;
import com.happymapleday.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BossKillMetricsBatchService {

    private final SettlementServiceClient settlementServiceClient;
    private final BossServiceClient bossServiceClient;
    private final BossKillMetricsRepository bossKillMetricsRepository;
    private final BatchExecutor batchExecutor;

    @Transactional
    public BatchExecutor.BatchExecutionResult executeBatch(LocalDate from, LocalDate to, ExecutionType executionType) {
        return batchExecutor.execute(BatchType.BOSS_KILLS, from, to, executionType, () -> {
            ApiResponse<List<SettlementBossKillDto>> response = settlementServiceClient.getBossKillsTimeSeries(
                null, from, to, null
            );

            if (response == null || response.getData() == null) {
                throw new RuntimeException("Settlement Service 응답 데이터가 없습니다");
            }

            List<SettlementBossKillDto> data = response.getData();
            int count = 0;

            for (SettlementBossKillDto dto : data) {
                BossInfoDto bossInfo = bossServiceClient.getBossInfo(dto.getBossId());
                
                if (bossInfo == null) {
                    log.warn("보스 정보를 찾을 수 없습니다: bossId={}", dto.getBossId());
                    continue;
                }

                BossKillMetrics metrics = bossKillMetricsRepository
                    .findByMetricDateAndBossId(dto.getDate(), dto.getBossId())
                    .orElseGet(() -> BossKillMetrics.builder()
                        .metricDate(dto.getDate())
                        .bossId(dto.getBossId())
                        .build());

                BossKillMetrics newMetrics = BossKillMetrics.builder()
                    .id(metrics.getId())
                    .metricDate(dto.getDate())
                    .bossId(dto.getBossId())
                    .bossName(bossInfo.getBossName())
                    .bossNameEn(bossInfo.getBossNameEn())
                    .difficulty(bossInfo.getDifficulty())
                    .totalKills(dto.getTotalKills())
                    .build();

                bossKillMetricsRepository.save(newMetrics);
                count++;
            }

            return count;
        });
    }
}

