package com.happymapleday.settlement.admin.service;

import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossKillCountSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.ItemDropSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BoxContentsSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.ItemAveragePriceResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossHardnessSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.PartyRatioSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.AvgCombatPowerByBossJobResponse;

import java.time.LocalDate;
import java.util.List;

public interface SettlementMetricsService {

	// 주차별 보스 처치 횟수(타임시리즈)
	List<TimeSeriesLongResponse> getBossKillCountsByWeek(Long bossId, LocalDate from, LocalDate to);

	// 요약 지표들
	List<BossKillCountSummaryResponse> summarizeBossKillCounts(LocalDate from, LocalDate to);
	List<ItemDropSummaryResponse> summarizeItemDropsByBoss(Long bossId, LocalDate from, LocalDate to);
	List<BoxContentsSummaryResponse> summarizeBoxContentsByBoss(Long bossId, Long boxItemId, LocalDate from, LocalDate to);
	List<ItemAveragePriceResponse> summarizeItemAveragePrice(Long bossId, Long itemId, LocalDate from, LocalDate to);
	BossHardnessSummaryResponse summarizeBossHardness(Long bossId, LocalDate from, LocalDate to);
	PartyRatioSummaryResponse summarizePartyRatio(Long bossId, LocalDate from, LocalDate to);

	// 전체 보스 대상: 직업별 트림 평균 투력 (솔플, 각 주 최난이도 보스 기준)
	List<AvgCombatPowerByBossJobResponse> getTrimmedAvgCombatPowerGroupByBossAndJob(LocalDate from, LocalDate to);
}


