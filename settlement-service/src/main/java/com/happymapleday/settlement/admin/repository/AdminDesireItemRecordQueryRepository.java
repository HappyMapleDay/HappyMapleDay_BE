package com.happymapleday.settlement.admin.repository;

import com.happymapleday.settlement.entity.DesireItemRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface AdminDesireItemRecordQueryRepository extends JpaRepository<DesireItemRecord, Long> {

    // 보스별 아이템별 총 드랍 수 요약 (결과 아이템 기준)
    @Query(value = "select dir.desire_item_id as itemId, count(dir.id) as count\n" +
            "from desire_item_records dir\n" +
            "join weekly_boss_records wbr on wbr.id = dir.weekly_boss_record_id\n" +
            "where (:bossId is null or wbr.boss_id = :bossId)\n" +
            "  and (:from is null or wbr.week_start_date >= :from)\n" +
            "  and (:to is null or wbr.week_start_date <= :to)\n" +
            "group by dir.desire_item_id\n" +
            "order by dir.desire_item_id",
            nativeQuery = true)
    List<Map<String, Object>> summarizeItemDropsByBoss(@Param("bossId") Long bossId,
                                                       @Param("from") LocalDate from,
                                                       @Param("to") LocalDate to);

    // 보스별 박스 내용물 요약: 특정 보스가 드랍한 상자(source_box_item_id)에서 나온 결과(desire_item_id) 카운트
    @Query(value = "select dir.desire_item_id as itemId, count(dir.id) as count\n" +
            "from desire_item_records dir\n" +
            "join weekly_boss_records wbr on wbr.id = dir.weekly_boss_record_id\n" +
            "where dir.source_box_item_id is not null\n" +
            "  and (:bossId is null or wbr.boss_id = :bossId)\n" +
            "  and (:boxItemId is null or dir.source_box_item_id = :boxItemId)\n" +
            "  and (:from is null or wbr.week_start_date >= :from)\n" +
            "  and (:to is null or wbr.week_start_date <= :to)\n" +
            "group by dir.desire_item_id\n" +
            "order by dir.desire_item_id",
            nativeQuery = true)
    List<Map<String, Object>> summarizeBoxContentsByBoss(@Param("bossId") Long bossId,
                                                         @Param("boxItemId") Long boxItemId,
                                                         @Param("from") LocalDate from,
                                                         @Param("to") LocalDate to);

    // 아이템별 평균 판매가 요약 (보스/기간 필터 선택)
    @Query(value = "select dir.desire_item_id as itemId, avg(dir.sale_price) as avgPrice\n" +
            "from desire_item_records dir\n" +
            "join weekly_boss_records wbr on wbr.id = dir.weekly_boss_record_id\n" +
            "where (:bossId is null or wbr.boss_id = :bossId)\n" +
            "  and (:itemId is null or dir.desire_item_id = :itemId)\n" +
            "  and (:from is null or wbr.week_start_date >= :from)\n" +
            "  and (:to is null or wbr.week_start_date <= :to)\n" +
            "group by dir.desire_item_id\n" +
            "order by dir.desire_item_id",
            nativeQuery = true)
    List<Map<String, Object>> summarizeItemAveragePrice(@Param("bossId") Long bossId,
                                                        @Param("itemId") Long itemId,
                                                        @Param("from") LocalDate from,
                                                        @Param("to") LocalDate to);
}


