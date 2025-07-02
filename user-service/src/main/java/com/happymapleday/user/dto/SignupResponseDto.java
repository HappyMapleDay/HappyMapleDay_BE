package com.happymapleday.user.dto;

public class SignupResponseDto {
    
    private String message;
    
    // 기본 생성자
    public SignupResponseDto() {}
    
    // 생성자
    public SignupResponseDto(String message) {
        this.message = message;
    }
    
    // 성공 응답 생성 정적 메서드
    public static SignupResponseDto success() {
        return new SignupResponseDto("회원가입이 완료되었습니다.");
    }
    
    // Getter 메서드
    public String getMessage() {
        return message;
    }
    
    // Setter 메서드
    public void setMessage(String message) {
        this.message = message;
    }
} 