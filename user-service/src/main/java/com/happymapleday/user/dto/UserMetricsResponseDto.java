package com.happymapleday.user.dto;

import java.time.LocalDate;
import java.util.List;

public class UserMetricsResponseDto {
    
    private List<UserCountByDate> userCounts;
    private int totalActiveUsers;
    private String period;
    
    // 기본 생성자
    public UserMetricsResponseDto() {}
    
    // 생성자
    public UserMetricsResponseDto(List<UserCountByDate> userCounts, int totalActiveUsers, String period) {
        this.userCounts = userCounts;
        this.totalActiveUsers = totalActiveUsers;
        this.period = period;
    }
    
    // 정적 팩토리 메서드
    public static UserMetricsResponseDto of(List<UserCountByDate> userCounts, int totalActiveUsers, String period) {
        return new UserMetricsResponseDto(userCounts, totalActiveUsers, period);
    }
    
    // Getter 메서드들
    public List<UserCountByDate> getUserCounts() {
        return userCounts;
    }
    
    public int getTotalActiveUsers() {
        return totalActiveUsers;
    }
    
    public String getPeriod() {
        return period;
    }
    
    // Setter 메서드들
    public void setUserCounts(List<UserCountByDate> userCounts) {
        this.userCounts = userCounts;
    }
    
    public void setTotalActiveUsers(int totalActiveUsers) {
        this.totalActiveUsers = totalActiveUsers;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    // 내부 클래스
    public static class UserCountByDate {
        private LocalDate date;
        private long cumulativeCount; // 누적 가입자 수
        private long dailyCount; // 해당 일자 신규 가입자 수
        
        public UserCountByDate() {}
        
        public UserCountByDate(LocalDate date, long cumulativeCount, long dailyCount) {
            this.date = date;
            this.cumulativeCount = cumulativeCount;
            this.dailyCount = dailyCount;
        }
        
        // Getter 메서드들
        public LocalDate getDate() {
            return date;
        }
        
        public long getCumulativeCount() {
            return cumulativeCount;
        }
        
        public long getDailyCount() {
            return dailyCount;
        }
        
        // Setter 메서드들
        public void setDate(LocalDate date) {
            this.date = date;
        }
        
        public void setCumulativeCount(long cumulativeCount) {
            this.cumulativeCount = cumulativeCount;
        }
        
        public void setDailyCount(long dailyCount) {
            this.dailyCount = dailyCount;
        }
    }
}

