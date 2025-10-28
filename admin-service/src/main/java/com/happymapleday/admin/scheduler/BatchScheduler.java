package com.happymapleday.admin.scheduler;

import com.happymapleday.admin.enums.ExecutionType;
import com.happymapleday.admin.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {

    private final UserMetricsBatchService userMetricsBatchService;
    private final BossKillMetricsBatchService bossKillMetricsBatchService;
    private final BossCombatPowerMetricsBatchService bossCombatPowerMetricsBatchService;
    private final ItemDropMetricsBatchService itemDropMetricsBatchService;
    private final ItemPriceMetricsBatchService itemPriceMetricsBatchService;

    // 매일 자정 1시 - 유저 통계 수집
    @Scheduled(cron = "0 0 1 * * ?")
    public void scheduleUserMetricsBatch() {
        log.info("=== 유저 통계 배치 스케줄 시작 ===");
        LocalDate yesterday = LocalDate.now().minusDays(1);
        userMetricsBatchService.executeBatch(yesterday, yesterday, ExecutionType.SCHEDULED);
        log.info("=== 유저 통계 배치 스케줄 완료 ===");
    }

    // 매주 목요일 0시 - 보스 격파 횟수 수집
    @Scheduled(cron = "0 0 0 ? * THU")
    public void scheduleBossKillMetricsBatch() {
        log.info("=== 보스 격파 횟수 배치 스케줄 시작 ===");
        LocalDate lastThursday = getLastThursday();
        LocalDate previousThursday = lastThursday.minusWeeks(1);
        bossKillMetricsBatchService.executeBatch(previousThursday, lastThursday.minusDays(1), ExecutionType.SCHEDULED);
        log.info("=== 보스 격파 횟수 배치 스케줄 완료 ===");
    }

    // 매주 목요일 0시 30분 - 전투력 평균 수집
    @Scheduled(cron = "0 30 0 ? * THU")
    public void scheduleBossCombatPowerMetricsBatch() {
        log.info("=== 전투력 평균 배치 스케줄 시작 ===");
        LocalDate lastThursday = getLastThursday();
        LocalDate previousThursday = lastThursday.minusWeeks(1);
        bossCombatPowerMetricsBatchService.executeBatch(previousThursday, lastThursday.minusDays(1), ExecutionType.SCHEDULED);
        log.info("=== 전투력 평균 배치 스케줄 완료 ===");
    }

    // 매주 목요일 1시 - 아이템 드롭 수집
    @Scheduled(cron = "0 0 1 ? * THU")
    public void scheduleItemDropMetricsBatch() {
        log.info("=== 아이템 드롭 배치 스케줄 시작 ===");
        LocalDate lastThursday = getLastThursday();
        LocalDate previousThursday = lastThursday.minusWeeks(1);
        itemDropMetricsBatchService.executeBatch(previousThursday, lastThursday.minusDays(1), ExecutionType.SCHEDULED);
        log.info("=== 아이템 드롭 배치 스케줄 완료 ===");
    }

    // 매주 목요일 1시 30분 - 아이템 판매가 수집
    @Scheduled(cron = "0 30 1 ? * THU")
    public void scheduleItemPriceMetricsBatch() {
        log.info("=== 아이템 판매가 배치 스케줄 시작 ===");
        LocalDate lastThursday = getLastThursday();
        LocalDate previousThursday = lastThursday.minusWeeks(1);
        itemPriceMetricsBatchService.executeBatch(previousThursday, lastThursday.minusDays(1), ExecutionType.SCHEDULED);
        log.info("=== 아이템 판매가 배치 스케줄 완료 ===");
    }

    // 직전 목요일 날짜 계산
    private LocalDate getLastThursday() {
        LocalDate today = LocalDate.now();
        int daysToSubtract = (today.getDayOfWeek().getValue() - DayOfWeek.THURSDAY.getValue() + 7) % 7;
        if (daysToSubtract == 0 && today.getDayOfWeek() == DayOfWeek.THURSDAY) {
            return today;
        }
        return today.minusDays(daysToSubtract);
    }
}

