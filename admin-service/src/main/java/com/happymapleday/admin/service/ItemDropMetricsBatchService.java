package com.happymapleday.admin.service;

import com.happymapleday.admin.client.BossServiceClient;
import com.happymapleday.admin.client.SettlementServiceClient;
import com.happymapleday.admin.dto.external.BossInfoDto;
import com.happymapleday.admin.dto.external.BossItemInfoDto;
import com.happymapleday.admin.dto.external.SettlementItemDropDto;
import com.happymapleday.admin.entity.ItemDropMetrics;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.enums.ExecutionType;
import com.happymapleday.admin.repository.ItemDropMetricsRepository;
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
public class ItemDropMetricsBatchService {

    private final SettlementServiceClient settlementServiceClient;
    private final BossServiceClient bossServiceClient;
    private final ItemDropMetricsRepository itemDropMetricsRepository;
    private final BatchExecutor batchExecutor;

    @Transactional
    public BatchExecutor.BatchExecutionResult executeBatch(LocalDate from, LocalDate to, ExecutionType executionType) {
        return batchExecutor.execute(BatchType.ITEM_DROPS, from, to, executionType, () -> {
            ApiResponse<List<SettlementItemDropDto>> response = settlementServiceClient.getItemDropsSummary(
                null, from, to
            );

            if (response == null || response.getData() == null) {
                throw new RuntimeException("Settlement Service 응답 데이터가 없습니다");
            }

            List<SettlementItemDropDto> data = response.getData();
            int count = 0;

            for (SettlementItemDropDto dto : data) {
                BossInfoDto bossInfo = null;
                if (dto.getBossId() != null) {
                    bossInfo = bossServiceClient.getBossInfo(dto.getBossId());
                }

                BossItemInfoDto itemInfo = bossServiceClient.getItemInfo(dto.getItemId());
                if (itemInfo == null) {
                    log.warn("아이템 정보를 찾을 수 없습니다: itemId={}", dto.getItemId());
                    continue;
                }

                ItemDropMetrics metrics = ItemDropMetrics.builder()
                    .metricDate(dto.getDate())
                    .bossId(dto.getBossId())
                    .bossName(bossInfo != null ? bossInfo.getBossName() : null)
                    .bossNameEn(bossInfo != null ? bossInfo.getBossNameEn() : null)
                    .difficulty(bossInfo != null ? bossInfo.getDifficulty() : null)
                    .itemId(dto.getItemId())
                    .itemName(itemInfo.getItemName())
                    .itemNameEn(itemInfo.getItemNameEn())
                    .dropCount(dto.getDropCount())
                    .build();

                itemDropMetricsRepository.save(metrics);
                count++;
            }

            return count;
        });
    }
}

