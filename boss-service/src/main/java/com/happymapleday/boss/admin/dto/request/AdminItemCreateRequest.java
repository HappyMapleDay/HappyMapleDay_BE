package com.happymapleday.boss.admin.dto.request;

import lombok.*;

@Getter
@Builder
public class AdminItemCreateRequest {
    private String itemName;
    private String itemNameEn;
    private Boolean isRandomBox;
} 