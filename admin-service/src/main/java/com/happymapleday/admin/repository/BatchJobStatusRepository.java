package com.happymapleday.admin.repository;

import com.happymapleday.admin.entity.BatchJobStatus;
import com.happymapleday.admin.enums.BatchType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BatchJobStatusRepository extends JpaRepository<BatchJobStatus, Long> {
    
    Optional<BatchJobStatus> findByBatchType(BatchType batchType);
}

