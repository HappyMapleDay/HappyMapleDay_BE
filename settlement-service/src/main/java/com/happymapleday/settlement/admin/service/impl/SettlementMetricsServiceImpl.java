package com.happymapleday.settlement.admin.service.impl;

import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesBossLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossKillCountSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.ItemDropSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BoxContentsSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.ItemAveragePriceResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.PartyRatioSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.AvgCombatPowerByBossJobResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossItemCountResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossItemAvgPriceResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossPartyRatioResponse;
import com.happymapleday.settlement.admin.repository.AdminDesireItemRecordQueryRepository;
import com.happymapleday.settlement.admin.repository.AdminWeeklyBossRecordQueryRepository;
import com.happymapleday.settlement.admin.repository.projection.DateLongValue;
import com.happymapleday.settlement.admin.service.SettlementMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SettlementMetricsServiceImpl implements SettlementMetricsService {

    private final AdminWeeklyBossRecordQueryRepository weeklyBossRecordRepository;
    private final AdminDesireItemRecordQueryRepository desireItemRecordRepository;

    // 주차별 보스 처치 횟수
    @Override
    public List<TimeSeriesLongResponse> getBossKillCountsByWeek(Long bossId, LocalDate from, LocalDate to) {
        List<DateLongValue> rows = weeklyBossRecordRepository.findBossKillCountsByWeek(bossId, from, to);
        return rows.stream()
                .map(r -> TimeSeriesLongResponse.builder().date(r.getDate()).value(r.getValue()).build())
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSeriesBossLongResponse> getBossKillCountsByWeekGroupByBoss(LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = weeklyBossRecordRepository.findBossKillCountsByWeekGroupByBoss(from, to);
        return rows.stream()
                .map(r -> {
                    Long bossIdVal = ((Number) r.get("bossId")).longValue();
                    Object dateObj = r.get("date");
                    LocalDate dateVal;
                    if (dateObj instanceof Date) {
                        dateVal = ((Date) dateObj).toLocalDate();
                    } else if (dateObj instanceof LocalDate) {
                        dateVal = (LocalDate) dateObj;
                    } else {
                        dateVal = LocalDate.parse(String.valueOf(dateObj));
                    }
                    Long valueVal = ((Number) r.get("value")).longValue();
                    return TimeSeriesBossLongResponse.builder()
                            .bossId(bossIdVal)
                            .date(dateVal)
                            .value(valueVal)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BossKillCountSummaryResponse> summarizeBossKillCounts(LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = weeklyBossRecordRepository.summarizeBossKillCounts(from, to);
        return rows.stream()
                .map(r -> BossKillCountSummaryResponse.builder()
                        .bossId(((Number) r.get("bossId")).longValue())
                        .count(((Number) r.get("count")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDropSummaryResponse> summarizeItemDropsByBoss(Long bossId, LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = desireItemRecordRepository.summarizeItemDropsByBoss(bossId, from, to);
        return rows.stream()
                .map(r -> ItemDropSummaryResponse.builder()
                        .itemId(((Number) r.get("itemId")).longValue())
                        .count(((Number) r.get("count")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<BossItemCountResponse> summarizeItemDropsGroupByBoss(LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = desireItemRecordRepository.summarizeItemDropsGroupByBoss(from, to);
        return rows.stream()
                .map(r -> BossItemCountResponse.builder()
                        .bossId(((Number) r.get("bossId")).longValue())
                        .itemId(((Number) r.get("itemId")).longValue())
                        .count(((Number) r.get("count")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<BoxContentsSummaryResponse> summarizeBoxContentsByBoss(Long bossId, Long boxItemId, LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = desireItemRecordRepository.summarizeBoxContentsByBoss(bossId, boxItemId, from, to);
        return rows.stream()
                .map(r -> BoxContentsSummaryResponse.builder()
                        .itemId(((Number) r.get("itemId")).longValue())
                        .count(((Number) r.get("count")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<BossItemCountResponse> summarizeBoxContentsGroupByBoss(Long boxItemId, LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = desireItemRecordRepository.summarizeBoxContentsGroupByBoss(boxItemId, from, to);
        return rows.stream()
                .map(r -> BossItemCountResponse.builder()
                        .bossId(((Number) r.get("bossId")).longValue())
                        .itemId(((Number) r.get("itemId")).longValue())
                        .count(((Number) r.get("count")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemAveragePriceResponse> summarizeItemAveragePrice(Long bossId, Long itemId, LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = desireItemRecordRepository.summarizeItemAveragePrice(bossId, itemId, from, to);
        return rows.stream()
                .map(r -> ItemAveragePriceResponse.builder()
                        .itemId(((Number) r.get("itemId")).longValue())
                        .avgPrice(new BigDecimal(r.get("avgPrice").toString()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<BossItemAvgPriceResponse> summarizeItemAveragePriceGroupByBoss(Long itemId, LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = desireItemRecordRepository.summarizeItemAveragePriceGroupByBoss(itemId, from, to);
        return rows.stream()
                .map(r -> BossItemAvgPriceResponse.builder()
                        .bossId(((Number) r.get("bossId")).longValue())
                        .itemId(((Number) r.get("itemId")).longValue())
                        .avgPrice(new BigDecimal(String.valueOf(r.get("avgPrice"))))
                        .build())
                .collect(Collectors.toList());
    }

    

    @Override
    public List<AvgCombatPowerByBossJobResponse> getTrimmedAvgCombatPowerGroupByBossAndJob(LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = weeklyBossRecordRepository.findTrimmedAvgCombatPowerByBossGroupByJob(from, to);
        return rows.stream()
                .map(r -> new AvgCombatPowerByBossJobResponse(
                        ((Number) r.get("bossId")).longValue(),
                        (String) r.get("job"),
                        r.get("avg_power") == null ? null : Double.valueOf(r.get("avg_power").toString())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public PartyRatioSummaryResponse summarizePartyRatio(Long bossId, LocalDate from, LocalDate to) {
        Map<String, Object> row = weeklyBossRecordRepository.summarizePartyRatio(bossId, from, to);
        Long solo = 0L;
        Long party = 0L;
        if (row != null) {
            if (row.get("soloCount") != null) solo = ((Number) row.get("soloCount")).longValue();
            if (row.get("partyCount") != null) party = ((Number) row.get("partyCount")).longValue();
        }
        return PartyRatioSummaryResponse.builder()
                .soloCount(solo)
                .partyCount(party)
                .build();
    }

    @Override
    public List<BossPartyRatioResponse> summarizePartyRatioGroupByBoss(LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = weeklyBossRecordRepository.summarizePartyRatioGroupByBoss(from, to);
        return rows.stream()
                .map(r -> BossPartyRatioResponse.builder()
                        .bossId(((Number) r.get("bossId")).longValue())
                        .soloCount(((Number) r.get("soloCount")).longValue())
                        .partyCount(((Number) r.get("partyCount")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    
}


