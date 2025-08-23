package com.happymapleday.settlement.admin.repository;

import com.happymapleday.settlement.admin.repository.projection.DateLongValue;
import com.happymapleday.settlement.entity.WeeklyBossRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface AdminWeeklyBossRecordQueryRepository extends JpaRepository<WeeklyBossRecord, Long> {

    @Query("select wbr.weekStartDate as date, count(wbr.id) as value from WeeklyBossRecord wbr " +
           "where (:bossId is null or wbr.bossId = :bossId) and (:from is null or wbr.weekStartDate >= :from) and (:to is null or wbr.weekStartDate <= :to) " +
           "group by wbr.weekStartDate order by wbr.weekStartDate")
    List<DateLongValue> findBossKillCountsByWeek(@Param("bossId") Long bossId,
                                                 @Param("from") LocalDate from,
                                                 @Param("to") LocalDate to);

    // (삭제됨) 단일 보스 트림 평균 투력 조회

    // 전체 보스에 대해: 솔플이고 그 주 최난이도 보스로 해당 보스를 선택한 캐릭터들의 직업별 트림 평균 투력
    @Query(value = "with ranked as (\n" +
            "  select wbr.*, \n" +
            "         row_number() over (partition by wbr.character_id, wbr.week_start_date order by wbr.difficulty_score desc) as rn\n" +
            "  from weekly_boss_records wbr\n" +
            "  where wbr.party_size = 1\n" +
            "    and (:from is null or wbr.week_start_date >= :from)\n" +
            "    and (:to is null or wbr.week_start_date <= :to)\n" +
            ")\n" +
            ", filtered as (\n" +
            "  select * from ranked where rn = 1\n" +
            ")\n" +
            ", job_ranked as (\n" +
            "  select boss_id, character_class, combat_power,\n" +
            "         ntile(10) over (partition by boss_id, character_class order by combat_power) as tile\n" +
            "  from filtered\n" +
            ")\n" +
            "select boss_id as bossId, character_class as job, avg(combat_power) as avg_power\n" +
            "from job_ranked\n" +
            "where tile between 2 and 9\n" +
            "group by boss_id, character_class\n" +
            "order by boss_id, character_class",
            nativeQuery = true)
    List<Map<String, Object>> findTrimmedAvgCombatPowerByBossGroupByJob(@Param("from") LocalDate from,
                                                                        @Param("to") LocalDate to);

    // 보스별 총 처치 수 요약 (bossId, count)
    @Query(value = "select wbr.boss_id as bossId, count(wbr.id) as count\n" +
            "from weekly_boss_records wbr\n" +
            "where (:from is null or wbr.week_start_date >= :from) and (:to is null or wbr.week_start_date <= :to)\n" +
            "group by wbr.boss_id order by wbr.boss_id",
            nativeQuery = true)
    List<Map<String, Object>> summarizeBossKillCounts(@Param("from") LocalDate from,
                                                      @Param("to") LocalDate to);

    // 보스 하드니스 요약: 총 대상 캐릭터 수
    @Query(value = "with ranked as (\n" +
            "  select wbr.*, \n" +
            "         row_number() over (partition by wbr.character_id, wbr.week_start_date order by wbr.difficulty_score desc) as rn\n" +
            "  from weekly_boss_records wbr\n" +
            "  where wbr.party_size = 1\n" +
            "    and (:from is null or wbr.week_start_date >= :from)\n" +
            "    and (:to is null or wbr.week_start_date <= :to)\n" +
            ")\n" +
            "select count(1) as totalCount\n" +
            "from ranked\n" +
            "where rn = 1 and (:bossId is null or boss_id = :bossId)",
            nativeQuery = true)
    Map<String, Object> summarizeBossHardnessTotal(@Param("bossId") Long bossId,
                                                   @Param("from") LocalDate from,
                                                   @Param("to") LocalDate to);

    // 보스 하드니스 요약: 직업별 카운트와 트림 평균 투력
    @Query(value = "with ranked as (\n" +
            "  select wbr.*, \n" +
            "         row_number() over (partition by wbr.character_id, wbr.week_start_date order by wbr.difficulty_score desc) as rn\n" +
            "  from weekly_boss_records wbr\n" +
            "  where wbr.party_size = 1\n" +
            "    and (:from is null or wbr.week_start_date >= :from)\n" +
            "    and (:to is null or wbr.week_start_date <= :to)\n" +
            ")\n" +
            ", filtered as (\n" +
            "  select * from ranked where rn = 1 and (:bossId is null or boss_id = :bossId)\n" +
            ")\n" +
            ", job_ranked as (\n" +
            "  select character_class, combat_power,\n" +
            "         ntile(10) over (partition by character_class order by combat_power) as tile\n" +
            "  from filtered\n" +
            ")\n" +
            "select character_class as job, count(1) as count, avg(case when tile between 2 and 9 then combat_power end) as avg_power\n" +
            "from job_ranked\n" +
            "group by character_class",
            nativeQuery = true)
    List<Map<String, Object>> summarizeBossHardnessByJob(@Param("bossId") Long bossId,
                                                         @Param("from") LocalDate from,
                                                         @Param("to") LocalDate to);

    // 보스 솔로/파티 비율 요약
    @Query(value = "select\n" +
            "  sum(case when wbr.party_size = 1 then 1 else 0 end) as soloCount,\n" +
            "  sum(case when wbr.party_size > 1 then 1 else 0 end) as partyCount\n" +
            "from weekly_boss_records wbr\n" +
            "where (:bossId is null or wbr.boss_id = :bossId)\n" +
            "  and (:from is null or wbr.week_start_date >= :from)\n" +
            "  and (:to is null or wbr.week_start_date <= :to)",
            nativeQuery = true)
    Map<String, Object> summarizePartyRatio(@Param("bossId") Long bossId,
                                            @Param("from") LocalDate from,
                                            @Param("to") LocalDate to);
}


