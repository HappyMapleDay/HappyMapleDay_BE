package com.happymapleday.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SignupRequestDto {
    
    @NotBlank(message = "넥슨 API 키는 필수입니다.")
    private String nexonApiKey;
    
    @NotBlank(message = "메인 캐릭터명은 필수입니다.")
    @Size(max = 50, message = "메인 캐릭터명은 50자 이하여야 합니다.")
    private String mainCharacterName;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;
    
    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;
    
    @NotNull(message = "데이터 수집 동의 여부는 필수입니다.")
    private Boolean dataCollectionAgreed;
    
    // 기본 생성자
    public SignupRequestDto() {}
    
    // 생성자
    public SignupRequestDto(String nexonApiKey, String mainCharacterName, 
                          String password, String passwordConfirm, Boolean dataCollectionAgreed) {
        this.nexonApiKey = nexonApiKey;
        this.mainCharacterName = mainCharacterName;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.dataCollectionAgreed = dataCollectionAgreed;
    }
    
    // Getter 메서드들
    public String getNexonApiKey() {
        return nexonApiKey;
    }
    
    public String getMainCharacterName() {
        return mainCharacterName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getPasswordConfirm() {
        return passwordConfirm;
    }
    
    public Boolean getDataCollectionAgreed() {
        return dataCollectionAgreed;
    }
    
    // Setter 메서드들
    public void setNexonApiKey(String nexonApiKey) {
        this.nexonApiKey = nexonApiKey;
    }
    
    public void setMainCharacterName(String mainCharacterName) {
        this.mainCharacterName = mainCharacterName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
    
    public void setDataCollectionAgreed(Boolean dataCollectionAgreed) {
        this.dataCollectionAgreed = dataCollectionAgreed;
    }
} 