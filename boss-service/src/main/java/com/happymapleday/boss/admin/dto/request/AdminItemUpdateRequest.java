package com.happymapleday.boss.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminItemUpdateRequest {
    private String itemName;
    private Boolean isRandomBox;
} 