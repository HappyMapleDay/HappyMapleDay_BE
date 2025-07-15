package com.happymapleday.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeRequestDto {
    
    @NotBlank(message = "메인 캐릭터명은 필수입니다.")
    @Size(min = 2, max = 50, message = "메인 캐릭터명은 2-50자 사이여야 합니다.")
    private String mainCharacterName;
    
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 8-100자 사이여야 합니다.")
    private String newPassword;
    
    // 기본 생성자
    public PasswordChangeRequestDto() {}
    
    // 생성자
    public PasswordChangeRequestDto(String mainCharacterName, String newPassword) {
        this.mainCharacterName = mainCharacterName;
        this.newPassword = newPassword;
    }
    
    // Getter & Setter
    public String getMainCharacterName() {
        return mainCharacterName;
    }
    
    public void setMainCharacterName(String mainCharacterName) {
        this.mainCharacterName = mainCharacterName;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
} 