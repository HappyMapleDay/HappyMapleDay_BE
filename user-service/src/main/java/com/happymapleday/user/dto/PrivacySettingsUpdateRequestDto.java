package com.happymapleday.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivacySettingsUpdateRequestDto {
    
    @NotNull(message = "개인정보 수집 동의 여부는 필수입니다")
    private Boolean dataCollectionAgreed;
} 