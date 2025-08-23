package com.happymapleday.settlement.admin.repository;

import com.happymapleday.settlement.admin.repository.projection.DateBigDecimalValue;
import com.happymapleday.settlement.entity.WeeklySettlement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdminWeeklySettlementQueryRepository extends JpaRepository<WeeklySettlement, Long> {

    @Query("select ws.weekStartDate as date, avg(ws.totalIncome) as value from WeeklySettlement ws " +
           "where (:from is null or ws.weekStartDate >= :from) and (:to is null or ws.weekStartDate <= :to) " +
           "group by ws.weekStartDate order by ws.weekStartDate")
    List<DateBigDecimalValue> findAverageTotalIncomeByWeek(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select ws.weekStartDate as date, avg(ws.totalCrystalIncome) as value from WeeklySettlement ws " +
           "where (:from is null or ws.weekStartDate >= :from) and (:to is null or ws.weekStartDate <= :to) " +
           "group by ws.weekStartDate order by ws.weekStartDate")
    List<DateBigDecimalValue> findAverageCrystalIncomeByWeek(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select ws.weekStartDate as date, avg(ws.totalDesireItemIncome) as value from WeeklySettlement ws " +
           "where (:from is null or ws.weekStartDate >= :from) and (:to is null or ws.weekStartDate <= :to) " +
           "group by ws.weekStartDate order by ws.weekStartDate")
    List<DateBigDecimalValue> findAverageDesireItemIncomeByWeek(@Param("from") LocalDate from, @Param("to") LocalDate to);
}


