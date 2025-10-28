package com.happymapleday.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchExecuteAllRequest {
    private LocalDate from;
    private LocalDate to;
}

