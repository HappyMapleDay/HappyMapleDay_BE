package com.happymapleday.boss.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminBossDropItemCreateRequest {
    private Long bossId;
    
    // 기존 아이템을 사용하는 경우
    private Long itemId;
    
    // 새로운 아이템을 생성하는 경우
    private String itemName;
    private Boolean isRandomBox;
    
    // 기존 아이템 사용 여부 체크
    public boolean isUsingExistingItem() {
        return itemId != null;
    }
    
    // 새로운 아이템 생성 여부 체크
    public boolean isCreatingNewItem() {
        return itemName != null && !itemName.trim().isEmpty();
    }
} 