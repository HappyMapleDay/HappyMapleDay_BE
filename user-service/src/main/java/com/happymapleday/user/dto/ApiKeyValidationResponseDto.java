package com.happymapleday.user.dto;

public class ApiKeyValidationResponseDto {
    
    private boolean isValid;
    private Integer characterCount;
    private String message;
    
    // 기본 생성자
    public ApiKeyValidationResponseDto() {}
    
    // 생성자
    public ApiKeyValidationResponseDto(boolean isValid, Integer characterCount, String message) {
        this.isValid = isValid;
        this.characterCount = characterCount;
        this.message = message;
    }
    
    // 성공 응답 생성 정적 메서드
    public static ApiKeyValidationResponseDto success(int characterCount) {
        return new ApiKeyValidationResponseDto(true, characterCount, null);
    }
    
    // 실패 응답 생성 정적 메서드
    public static ApiKeyValidationResponseDto failure(String message) {
        return new ApiKeyValidationResponseDto(false, null, message);
    }
    
    // Getter 메서드들
    public boolean isValid() {
        return isValid;
    }
    
    public Integer getCharacterCount() {
        return characterCount;
    }
    
    public String getMessage() {
        return message;
    }
    
    // Setter 메서드들
    public void setValid(boolean valid) {
        isValid = valid;
    }
    
    public void setCharacterCount(Integer characterCount) {
        this.characterCount = characterCount;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
} 