package com.happymapleday.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsResponseDto {
    
    private String mainCharacterName;
    private Boolean dataCollectionAgreed;
    private Boolean weeklyResetEnabled;
    
    public static UserSettingsResponseDto from(String mainCharacterName,
                                               Boolean dataCollectionAgreed,
                                               Boolean weeklyResetEnabled) {
        return new UserSettingsResponseDto(
            mainCharacterName,
            dataCollectionAgreed,
            weeklyResetEnabled
        );
    }
} 