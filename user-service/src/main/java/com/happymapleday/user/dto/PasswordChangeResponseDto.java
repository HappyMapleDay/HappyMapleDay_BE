package com.happymapleday.user.dto;

public class PasswordChangeResponseDto {
    
    private String message;
    
    // 기본 생성자
    public PasswordChangeResponseDto() {}
    
    // 생성자
    public PasswordChangeResponseDto(String message) {
        this.message = message;
    }
    
    // 성공 응답 생성 메서드
    public static PasswordChangeResponseDto success() {
        return new PasswordChangeResponseDto("비밀번호가 성공적으로 변경되었습니다.");
    }
    
    // Getter & Setter
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
} 