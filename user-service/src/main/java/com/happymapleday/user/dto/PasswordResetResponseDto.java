package com.happymapleday.user.dto;

public class PasswordResetResponseDto {
    
    private String message;
    private String temporaryPassword; // 테스트용 - 실제 운영에서는 이메일 발송
    
    // 기본 생성자
    public PasswordResetResponseDto() {}
    
    // 생성자
    public PasswordResetResponseDto(String message, String temporaryPassword) {
        this.message = message;
        this.temporaryPassword = temporaryPassword;
    }
    
    // 성공 응답 생성 메서드
    public static PasswordResetResponseDto success(String temporaryPassword) {
        return new PasswordResetResponseDto(
            "임시 비밀번호가 생성되었습니다. 로그인 후 비밀번호를 변경해주세요.", 
            temporaryPassword
        );
    }
    
    // Getter & Setter
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getTemporaryPassword() {
        return temporaryPassword;
    }
    
    public void setTemporaryPassword(String temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }
} 