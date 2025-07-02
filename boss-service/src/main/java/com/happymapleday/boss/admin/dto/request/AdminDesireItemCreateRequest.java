package com.happymapleday.boss.admin.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminDesireItemCreateRequest {
    
    private Long bossId;
    private String itemName;
    private Boolean isRandomBox;
    
    public AdminDesireItemCreateRequest(Long bossId, String itemName, Boolean isRandomBox) {
        this.bossId = bossId;
        this.itemName = itemName;
        this.isRandomBox = isRandomBox;
    }
} 