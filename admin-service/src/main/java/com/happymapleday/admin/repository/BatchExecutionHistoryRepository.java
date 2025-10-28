package com.happymapleday.admin.repository;

import com.happymapleday.admin.entity.BatchExecutionHistory;
import com.happymapleday.admin.enums.BatchStatus;
import com.happymapleday.admin.enums.BatchType;
import com.happymapleday.admin.enums.ExecutionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BatchExecutionHistoryRepository extends JpaRepository<BatchExecutionHistory, Long> {
    
    Page<BatchExecutionHistory> findAllByOrderByExecutedAtDesc(Pageable pageable);
    
    Page<BatchExecutionHistory> findByBatchTypeOrderByExecutedAtDesc(BatchType batchType, Pageable pageable);
    
    @Query("SELECT h FROM BatchExecutionHistory h WHERE " +
           "(:batchType IS NULL OR h.batchType = :batchType) AND " +
           "(:executionType IS NULL OR h.executionType = :executionType) AND " +
           "(:status IS NULL OR h.status = :status) AND " +
           "(:from IS NULL OR h.executedAt >= :from) AND " +
           "(:to IS NULL OR h.executedAt <= :to) " +
           "ORDER BY h.executedAt DESC")
    Page<BatchExecutionHistory> findByFilters(
        @Param("batchType") BatchType batchType,
        @Param("executionType") ExecutionType executionType,
        @Param("status") BatchStatus status,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to,
        Pageable pageable
    );
}

