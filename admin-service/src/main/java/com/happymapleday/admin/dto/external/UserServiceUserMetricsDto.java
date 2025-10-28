package com.happymapleday.admin.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceUserMetricsDto {
    private List<UserCountData> userCounts;
    private Long totalActiveUsers;
    private String period;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCountData {
        private LocalDate date;
        private Long cumulativeCount;
        private Integer dailyCount;
    }
}

