package com.happymapleday.boss.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminBossDropItemUpdateRequest {
    private Long bossId;
    private Long itemId;
} 