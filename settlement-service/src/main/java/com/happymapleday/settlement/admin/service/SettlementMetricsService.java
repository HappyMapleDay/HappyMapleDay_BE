package com.happymapleday.settlement.admin.service;

import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesBossLongResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossKillCountSummaryResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.AvgCombatPowerByBossJobResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossItemCountResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossItemAvgPriceResponse;
import com.happymapleday.settlement.admin.dto.response.metrics.BossPartyRatioResponse;

import java.time.LocalDate;
import java.util.List;

public interface SettlementMetricsService {

	// 주차별 보스 처치 횟수(타임시리즈) - bossId 없으면 전체 보스별 반환
	List<TimeSeriesBossLongResponse> getBossKillCountsByWeek(Long bossId, LocalDate from, LocalDate to);

	// 요약 지표들 (bossId 없으면 전체 보스별 반환)
	List<BossKillCountSummaryResponse> summarizeBossKillCounts(LocalDate from, LocalDate to);
	List<BossItemCountResponse> summarizeItemDropsByBoss(Long bossId, LocalDate from, LocalDate to);
	List<BossItemCountResponse> summarizeBoxContentsByBoss(Long bossId, Long boxItemId, LocalDate from, LocalDate to);
	List<BossItemAvgPriceResponse> summarizeItemAveragePrice(Long bossId, Long itemId, LocalDate from, LocalDate to);
	List<BossPartyRatioResponse> summarizePartyRatio(Long bossId, LocalDate from, LocalDate to);

	// 전체 보스 대상: 직업별 트림 평균 투력 (솔플, 각 주 최난이도 보스 기준)
	List<AvgCombatPowerByBossJobResponse> getTrimmedAvgCombatPowerGroupByBossAndJob(LocalDate from, LocalDate to);
}


