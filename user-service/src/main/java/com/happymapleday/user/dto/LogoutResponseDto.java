package com.happymapleday.user.dto;

public class LogoutResponseDto {
    
    private String message;
    
    // 기본 생성자
    public LogoutResponseDto() {}
    
    // 생성자
    public LogoutResponseDto(String message) {
        this.message = message;
    }
    
    // 성공 응답 생성 정적 메서드
    public static LogoutResponseDto success() {
        return new LogoutResponseDto("로그아웃이 완료되었습니다.");
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