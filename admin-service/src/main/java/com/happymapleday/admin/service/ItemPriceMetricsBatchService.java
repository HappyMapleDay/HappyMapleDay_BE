package com.happymapleday.admin.service;

import com.happymapleday.admin.client.BossServiceClient;
import com.happymapleday.admin.client.SettlementServiceClient;
import com.happymapleday.admin.dto.external.BossInfoDto;
import com.happymapleday.admin.dto.external.BossItemInfoDto;
import com.happymapleday.admin.dto.external.SettlementItemPriceDto;
import com.happymapleday.admin.entity.ItemPriceMetrics;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.enums.ExecutionType;
import com.happymapleday.admin.repository.ItemPriceMetricsRepository;
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
public class ItemPriceMetricsBatchService {

    private final SettlementServiceClient settlementServiceClient;
    private final BossServiceClient bossServiceClient;
    private final ItemPriceMetricsRepository itemPriceMetricsRepository;
    private final BatchExecutor batchExecutor;

    @Transactional
    public BatchExecutor.BatchExecutionResult executeBatch(LocalDate from, LocalDate to, ExecutionType executionType) {
        return batchExecutor.execute(BatchType.ITEM_SALES, from, to, executionType, () -> {
            ApiResponse<List<SettlementItemPriceDto>> response = settlementServiceClient.getItemAveragePriceSummary(
                null, null, from, to
            );

            if (response == null || response.getData() == null) {
                throw new RuntimeException("Settlement Service 응답 데이터가 없습니다");
            }

            List<SettlementItemPriceDto> data = response.getData();
            int count = 0;

            for (SettlementItemPriceDto dto : data) {
                BossInfoDto bossInfo = null;
                if (dto.getBossId() != null) {
                    bossInfo = bossServiceClient.getBossInfo(dto.getBossId());
                }

                BossItemInfoDto itemInfo = bossServiceClient.getItemInfo(dto.getItemId());
                if (itemInfo == null) {
                    log.warn("아이템 정보를 찾을 수 없습니다: itemId={}", dto.getItemId());
                    continue;
                }

                ItemPriceMetrics metrics = ItemPriceMetrics.builder()
                    .metricDate(dto.getDate())
                    .bossId(dto.getBossId())
                    .bossName(bossInfo != null ? bossInfo.getBossName() : null)
                    .bossNameEn(bossInfo != null ? bossInfo.getBossNameEn() : null)
                    .difficulty(bossInfo != null ? bossInfo.getDifficulty() : null)
                    .itemId(dto.getItemId())
                    .itemName(itemInfo.getItemName())
                    .itemNameEn(itemInfo.getItemNameEn())
                    .avgPrice(dto.getAvgPrice())
                    .build();

                itemPriceMetricsRepository.save(metrics);
                count++;
            }

            return count;
        });
    }
}

