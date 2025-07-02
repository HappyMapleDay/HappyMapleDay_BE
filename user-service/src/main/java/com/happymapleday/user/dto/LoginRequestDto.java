package com.happymapleday.user.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDto {
    
    @NotBlank(message = "메인 캐릭터명은 필수입니다.")
    private String mainCharacterName;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
    
    // 기본 생성자
    public LoginRequestDto() {}
    
    // 생성자
    public LoginRequestDto(String mainCharacterName, String password) {
        this.mainCharacterName = mainCharacterName;
        this.password = password;
    }
    
    // Getter 메서드들
    public String getMainCharacterName() {
        return mainCharacterName;
    }
    
    public String getPassword() {
        return password;
    }
    
    // Setter 메서드들
    public void setMainCharacterName(String mainCharacterName) {
        this.mainCharacterName = mainCharacterName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
} 