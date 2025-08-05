package com.happymapleday.boss.admin.dto.request;

import lombok.*;

@Getter
@Builder
public class AdminBossDropItemCreateRequest {
    private Long bossId;
    private Long itemId;
} 