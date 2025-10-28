package com.happymapleday.admin.dto.request;

import com.happymapleday.admin.enums.BatchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchExecuteRequest {
    private BatchType batchType;
    private LocalDate from;
    private LocalDate to;
}

