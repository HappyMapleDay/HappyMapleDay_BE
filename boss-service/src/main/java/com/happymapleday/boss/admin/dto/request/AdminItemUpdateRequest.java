package com.happymapleday.boss.admin.dto.request;

import lombok.*;

@Getter
@Builder
public class AdminItemUpdateRequest {
    private String itemName;
    private String itemNameEn;
    private Boolean isRandomBox;
} 