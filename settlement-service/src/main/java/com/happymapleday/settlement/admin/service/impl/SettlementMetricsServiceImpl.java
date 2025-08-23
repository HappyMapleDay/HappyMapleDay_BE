package com.happymapleday.settlement.admin.service.impl;

import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossKillCountSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.ItemDropSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BoxContentsSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.ItemAveragePriceResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossHardnessSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossHardnessByJobResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.PartyRatioSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.AvgCombatPowerByBossJobResponse;
import com.happymapleday.settlement.admin.repository.AdminDesireItemRecordQueryRepository;
import com.happymapleday.settlement.admin.repository.AdminWeeklyBossRecordQueryRepository;
import com.happymapleday.settlement.admin.repository.projection.DateLongValue;
import com.happymapleday.settlement.admin.service.SettlementMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SettlementMetricsServiceImpl implements SettlementMetricsService {

    private final AdminWeeklyBossRecordQueryRepository weeklyBossRecordRepository;
    private final AdminDesireItemRecordQueryRepository desireItemRecordRepository;

    // 평균 총/결정석/물욕템 수익 타임시리즈 제거

    // 주차별 보스 처치 횟수
    @Override
    public List<TimeSeriesLongResponse> getBossKillCountsByWeek(Long bossId, LocalDate from, LocalDate to) {
        List<DateLongValue> rows = weeklyBossRecordRepository.findBossKillCountsByWeek(bossId, from, to);
        return rows.stream()
                .map(r -> TimeSeriesLongResponse.builder().date(r.getDate()).value(r.getValue()).build())
                .collect(Collectors.toList());
    }

    // 아이템 드랍/평균가 타임시리즈 제거
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
    public List<ItemAveragePriceResponse> summarizeItemAveragePrice(Long bossId, Long itemId, LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = desireItemRecordRepository.summarizeItemAveragePrice(bossId, itemId, from, to);
        return rows.stream()
                .map(r -> ItemAveragePriceResponse.builder()
                        .itemId(((Number) r.get("itemId")).longValue())
                        .avgPrice(new java.math.BigDecimal(r.get("avgPrice").toString()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public BossHardnessSummaryResponse summarizeBossHardness(Long bossId, LocalDate from, LocalDate to) {
        Map<String, Object> total = weeklyBossRecordRepository.summarizeBossHardnessTotal(bossId, from, to);
        List<Map<String, Object>> byJob = weeklyBossRecordRepository.summarizeBossHardnessByJob(bossId, from, to);
        Long totalCount = 0L;
        if (total != null && total.get("totalCount") != null) {
            totalCount = ((Number) total.get("totalCount")).longValue();
        }
        List<BossHardnessByJobResponse> byJobDtos = byJob.stream()
                .map(r -> BossHardnessByJobResponse.builder()
                        .characterClass((String) r.get("job"))
                        .count(((Number) r.get("count")).longValue())
                        .avgCombatPower(r.get("avg_power") == null ? null : Double.valueOf(r.get("avg_power").toString()))
                        .build())
                .collect(Collectors.toList());
        return BossHardnessSummaryResponse.builder()
                .totalCount(totalCount)
                .byCharacterClass(byJobDtos)
                .build();
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
}


