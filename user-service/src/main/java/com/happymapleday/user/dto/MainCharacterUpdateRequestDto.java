package com.happymapleday.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainCharacterUpdateRequestDto {
    
    @NotBlank(message = "새로운 본캐명은 필수입니다")
    @Size(min = 2, max = 12, message = "본캐명은 2자 이상 12자 이하여야 합니다")
    private String newMainCharacterName;
} 