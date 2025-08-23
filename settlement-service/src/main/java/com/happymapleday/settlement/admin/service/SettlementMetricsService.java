package com.happymapleday.settlement.admin.service;

import com.happymapleday.settlement.admin.dto.response.metrics.TimeSeriesLongResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SettlementMetricsService {

	// 주차별 보스 처치 횟수(타임시리즈)
	List<TimeSeriesLongResponse> getBossKillCountsByWeek(Long bossId, LocalDate from, LocalDate to);

	// 요약 지표들
	List<Map<String, Object>> summarizeBossKillCounts(LocalDate from, LocalDate to);
	List<Map<String, Object>> summarizeItemDropsByBoss(Long bossId, LocalDate from, LocalDate to);
	List<Map<String, Object>> summarizeBoxContentsByBoss(Long bossId, Long boxItemId, LocalDate from, LocalDate to);
	List<Map<String, Object>> summarizeItemAveragePrice(Long bossId, Long itemId, LocalDate from, LocalDate to);
	Map<String, Object> summarizeBossHardness(Long bossId, LocalDate from, LocalDate to);
	Map<String, Object> summarizePartyRatio(Long bossId, LocalDate from, LocalDate to);

	// 전체 보스 대상: 직업별 트림 평균 투력 (솔플, 각 주 최난이도 보스 기준)
	List<Map<String, Object>> getTrimmedAvgCombatPowerGroupByBossAndJob(LocalDate from, LocalDate to);
}


