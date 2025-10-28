package com.happymapleday.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMetricsResponse {
    private List<UserCountData> userCounts;
    private Long totalActiveUsers;
    private String period;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserCountData {
        private LocalDate date;
        private Long cumulativeCount;
        private Integer dailyCount;
    }
}

