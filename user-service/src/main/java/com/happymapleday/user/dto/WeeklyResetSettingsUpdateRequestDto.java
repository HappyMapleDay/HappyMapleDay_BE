package com.happymapleday.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyResetSettingsUpdateRequestDto {
    
    @NotNull(message = "주간 초기화 설정 여부는 필수입니다")
    private Boolean weeklyResetEnabled;
} 