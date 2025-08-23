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

    // 보스별·직업별 트림 평균 투력: 솔플만 포함, 각 캐릭터/주에서 가장 어려운 보스가 해당 bossId인 경우
    @Query(value = "" +
            "with ranked as (\n" +
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
            "  where (:job is null or character_class = :job)\n" +
            ")\n" +
            "select character_class as job, avg(combat_power) as avg_power\n" +
            "from job_ranked\n" +
            "where tile between 2 and 9\n" +
            "group by character_class",
            nativeQuery = true)
    List<Map<String, Object>> findTrimmedAvgCombatPowerByBossAndJob(@Param("bossId") Long bossId,
                                                                                         @Param("job") String job,
                                                                                         @Param("from") LocalDate from,
                                                                                         @Param("to") LocalDate to);
}


