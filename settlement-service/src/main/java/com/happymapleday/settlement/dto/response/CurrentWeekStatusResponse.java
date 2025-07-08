package com.happymapleday.settlement.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CurrentWeekStatusResponse {
    
    private LocalDate currentWeekStart;
    private LocalDate nextWeekStart;
    private Boolean isCompleted;
    private Integer remainingDays;
    private LocalDateTime nextResetDate;
    
    // 기본 생성자
    public CurrentWeekStatusResponse() {}
    
    // 생성자
    public CurrentWeekStatusResponse(LocalDate currentWeekStart, LocalDate nextWeekStart,
                                   Boolean isCompleted, Integer remainingDays, 
                                   LocalDateTime nextResetDate) {
        this.currentWeekStart = currentWeekStart;
        this.nextWeekStart = nextWeekStart;
        this.isCompleted = isCompleted;
        this.remainingDays = remainingDays;
        this.nextResetDate = nextResetDate;
    }
    
    // Getter/Setter
    public LocalDate getCurrentWeekStart() {
        return currentWeekStart;
    }
    
    public void setCurrentWeekStart(LocalDate currentWeekStart) {
        this.currentWeekStart = currentWeekStart;
    }
    
    public LocalDate getNextWeekStart() {
        return nextWeekStart;
    }
    
    public void setNextWeekStart(LocalDate nextWeekStart) {
        this.nextWeekStart = nextWeekStart;
    }
    
    public Boolean getIsCompleted() {
        return isCompleted;
    }
    
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    
    public Integer getRemainingDays() {
        return remainingDays;
    }
    
    public void setRemainingDays(Integer remainingDays) {
        this.remainingDays = remainingDays;
    }
    
    public LocalDateTime getNextResetDate() {
        return nextResetDate;
    }
    
    public void setNextResetDate(LocalDateTime nextResetDate) {
        this.nextResetDate = nextResetDate;
    }
} 