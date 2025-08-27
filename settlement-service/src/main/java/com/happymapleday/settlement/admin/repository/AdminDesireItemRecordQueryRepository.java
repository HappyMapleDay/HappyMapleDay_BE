package com.happymapleday.settlement.admin.repository;

import com.happymapleday.settlement.admin.repository.projection.DateBigIntegerValue;
import com.happymapleday.settlement.admin.repository.projection.DateLongValue;
import com.happymapleday.settlement.admin.repository.projection.IdLongValue;
import com.happymapleday.settlement.entity.DesireItemRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdminDesireItemRecordQueryRepository extends JpaRepository<DesireItemRecord, Long> {

    @Query("select wbr.weekStartDate as date, count(dir.id) as value from DesireItemRecord dir " +
           "join WeeklyBossRecord wbr on wbr.id = dir.weeklyBossRecordId " +
           "where (:itemId is null or dir.desireItemId = :itemId) and (:from is null or wbr.weekStartDate >= :from) and (:to is null or wbr.weekStartDate <= :to) " +
           "group by wbr.weekStartDate order by wbr.weekStartDate")
    List<DateLongValue> findItemDropCountByWeek(@Param("itemId") Long itemId,
                                                @Param("from") LocalDate from,
                                                @Param("to") LocalDate to);

    @Query("select wbr.weekStartDate as date, avg(dir.salePrice) as value from DesireItemRecord dir " +
           "join WeeklyBossRecord wbr on wbr.id = dir.weeklyBossRecordId " +
           "where (:itemId is null or dir.desireItemId = :itemId) and (:from is null or wbr.weekStartDate >= :from) and (:to is null or wbr.weekStartDate <= :to) " +
           "group by wbr.weekStartDate order by wbr.weekStartDate")
    List<DateBigIntegerValue> findItemAveragePriceByWeek(@Param("itemId") Long itemId,
                                                         @Param("from") LocalDate from,
                                                         @Param("to") LocalDate to);

    // 보스별 아이템별 총 드랍 수 요약 (랜덤박스 출처 포함 집계: 결과 아이템 기준 카운트)
    @Query("select dir.desireItemId as id, count(dir.id) as value from DesireItemRecord dir " +
           "join WeeklyBossRecord wbr on wbr.id = dir.weeklyBossRecordId " +
           "where (:bossId is null or wbr.bossId = :bossId) and (:from is null or wbr.weekStartDate >= :from) and (:to is null or wbr.weekStartDate <= :to) " +
           "group by dir.desireItemId order by dir.desireItemId")
    List<IdLongValue> summarizeItemDropsByBoss(
            @Param("bossId") Long bossId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}


