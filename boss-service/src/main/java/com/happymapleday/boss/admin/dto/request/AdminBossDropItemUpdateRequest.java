package com.happymapleday.boss.admin.dto.request;

import lombok.*;

@Builder
@Getter
public class AdminBossDropItemUpdateRequest {
    private Long bossId;
    private Long itemId;
} 