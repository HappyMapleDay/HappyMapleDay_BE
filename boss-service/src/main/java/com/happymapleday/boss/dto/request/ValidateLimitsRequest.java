package com.happymapleday.boss.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateLimitsRequest {
    private Long userId;
    private List<SelectedBoss> selectedBosses;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SelectedBoss {
        private Long characterId;
        private Long bossId;
    }
} 