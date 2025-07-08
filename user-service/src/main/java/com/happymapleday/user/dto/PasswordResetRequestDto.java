package com.happymapleday.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordResetRequestDto {
    
    @NotBlank(message = "메인 캐릭터명은 필수입니다.")
    @Size(min = 2, max = 50, message = "메인 캐릭터명은 2-50자 사이여야 합니다.")
    private String mainCharacterName;
    
    @NotBlank(message = "넥슨 API 키는 필수입니다.")
    private String nexonApiKey;
    
    // 기본 생성자
    public PasswordResetRequestDto() {}
    
    // 생성자
    public PasswordResetRequestDto(String mainCharacterName, String nexonApiKey) {
        this.mainCharacterName = mainCharacterName;
        this.nexonApiKey = nexonApiKey;
    }
    
    // Getter & Setter
    public String getMainCharacterName() {
        return mainCharacterName;
    }
    
    public void setMainCharacterName(String mainCharacterName) {
        this.mainCharacterName = mainCharacterName;
    }
    
    public String getNexonApiKey() {
        return nexonApiKey;
    }
    
    public void setNexonApiKey(String nexonApiKey) {
        this.nexonApiKey = nexonApiKey;
    }
} 