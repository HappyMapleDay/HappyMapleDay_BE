package com.happymapleday.boss.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateLimitsResponse {
    private Boolean isValid;
    private Map<String, CharacterLimitStatus> characterLimitStatus;
    private ServerLimitStatus serverLimitStatus;
    private List<String> violations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CharacterLimitStatus {
        private Integer current;
        private Integer limit;
        private Integer remaining;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServerLimitStatus {
        private Integer current;
        private Integer limit;
        private Integer remaining;
    }
} 