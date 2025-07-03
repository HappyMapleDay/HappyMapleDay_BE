package com.happymapleday.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainCharacterUpdateResponseDto {
    
    private String previousMainCharacterName;
    private String newMainCharacterName;
    private String message;
    
    public static MainCharacterUpdateResponseDto of(String previousName, String newName) {
        return new MainCharacterUpdateResponseDto(
            previousName,
            newName,
            "본캐명이 성공적으로 변경되었습니다."
        );
    }
} 