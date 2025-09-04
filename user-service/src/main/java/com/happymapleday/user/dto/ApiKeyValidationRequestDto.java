package com.happymapleday.user.dto;

import jakarta.validation.constraints.NotBlank;

public class ApiKeyValidationRequestDto {
    
    @NotBlank(message = "넥슨 API 키는 필수입니다.")
    private String nexonApiKey;
    
    // 기본 생성자
    public ApiKeyValidationRequestDto() {}
    
    // 생성자
    public ApiKeyValidationRequestDto(String nexonApiKey) {
        this.nexonApiKey = nexonApiKey;
    }
    
    // Getter 메서드
    public String getNexonApiKey() {
        return nexonApiKey;
    }
    
    // Setter 메서드
    public void setNexonApiKey(String nexonApiKey) {
        this.nexonApiKey = nexonApiKey;
    }
} 