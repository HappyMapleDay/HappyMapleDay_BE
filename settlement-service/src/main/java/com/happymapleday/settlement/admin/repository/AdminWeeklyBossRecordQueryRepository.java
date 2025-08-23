package com.happymapleday.settlement.admin.repository;

import com.happymapleday.settlement.admin.repository.projection.DateLongValue;
import com.happymapleday.settlement.entity.WeeklyBossRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdminWeeklyBossRecordQueryRepository extends JpaRepository<WeeklyBossRecord, Long> {

    @Query("select wbr.weekStartDate as date, count(wbr.id) as value from WeeklyBossRecord wbr " +
           "where (:bossId is null or wbr.bossId = :bossId) and (:from is null or wbr.weekStartDate >= :from) and (:to is null or wbr.weekStartDate <= :to) " +
           "group by wbr.weekStartDate order by wbr.weekStartDate")
    List<DateLongValue> findBossKillCountsByWeek(@Param("bossId") Long bossId,
                                                 @Param("from") LocalDate from,
                                                 @Param("to") LocalDate to);
}


