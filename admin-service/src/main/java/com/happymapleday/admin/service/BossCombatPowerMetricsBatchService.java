package com.happymapleday.admin.service;

import com.happymapleday.admin.client.BossServiceClient;
import com.happymapleday.admin.client.SettlementServiceClient;
import com.happymapleday.admin.dto.external.BossInfoDto;
import com.happymapleday.admin.dto.external.SettlementCombatPowerDto;
import com.happymapleday.admin.entity.BossCombatPowerMetrics;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.enums.ExecutionType;
import com.happymapleday.admin.repository.BossCombatPowerMetricsRepository;
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
public class BossCombatPowerMetricsBatchService {

    private final SettlementServiceClient settlementServiceClient;
    private final BossServiceClient bossServiceClient;
    private final BossCombatPowerMetricsRepository bossCombatPowerMetricsRepository;
    private final BatchExecutor batchExecutor;

    @Transactional
    public BatchExecutor.BatchExecutionResult executeBatch(LocalDate from, LocalDate to, ExecutionType executionType) {
        return batchExecutor.execute(BatchType.COMBAT_POWER, from, to, executionType, () -> {
            ApiResponse<List<SettlementCombatPowerDto>> response = settlementServiceClient.getBossAvgCombatPower(
                from, to
            );

            if (response == null || response.getData() == null) {
                throw new RuntimeException("Settlement Service 응답 데이터가 없습니다");
            }

            List<SettlementCombatPowerDto> data = response.getData();
            int count = 0;

            for (SettlementCombatPowerDto dto : data) {
                BossInfoDto bossInfo = bossServiceClient.getBossInfo(dto.getBossId());
                
                if (bossInfo == null) {
                    log.warn("보스 정보를 찾을 수 없습니다: bossId={}", dto.getBossId());
                    continue;
                }

                BossCombatPowerMetrics metrics = bossCombatPowerMetricsRepository
                    .findByMetricDateAndBossIdAndCharacterClass(dto.getDate(), dto.getBossId(), dto.getCharacterClass())
                    .orElseGet(() -> BossCombatPowerMetrics.builder()
                        .metricDate(dto.getDate())
                        .bossId(dto.getBossId())
                        .characterClass(dto.getCharacterClass())
                        .build());

                BossCombatPowerMetrics newMetrics = BossCombatPowerMetrics.builder()
                    .id(metrics.getId())
                    .metricDate(dto.getDate())
                    .bossId(dto.getBossId())
                    .bossName(bossInfo.getBossName())
                    .bossNameEn(bossInfo.getBossNameEn())
                    .difficulty(bossInfo.getDifficulty())
                    .characterClass(dto.getCharacterClass())
                    .avgCombatPower(dto.getAvgCombatPower())
                    .build();

                bossCombatPowerMetricsRepository.save(newMetrics);
                count++;
            }

            return count;
        });
    }
}

