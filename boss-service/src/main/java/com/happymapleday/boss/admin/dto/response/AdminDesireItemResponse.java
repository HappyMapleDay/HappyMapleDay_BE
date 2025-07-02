package com.happymapleday.boss.admin.dto.response;

import com.happymapleday.boss.entity.DesireItem;
import lombok.Getter;

@Getter
public class AdminDesireItemResponse {
    
    private final Long id;
    private final Long bossId;
    private final String bossName;
    private final String itemName;
    private final Boolean isRandomBox;
    private final int randomBoxItemCount;
    
    public AdminDesireItemResponse(DesireItem desireItem) {
        this.id = desireItem.getId();
        this.bossId = desireItem.getBoss().getId();
        this.bossName = desireItem.getBoss().getFullName();
        this.itemName = desireItem.getItemName();
        this.isRandomBox = desireItem.getIsRandomBox();
        this.randomBoxItemCount = desireItem.getRandomBoxItems().size();
    }
} 