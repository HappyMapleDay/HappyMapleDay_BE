package com.happymapleday.user.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
    
    // 기본 시크릿 키 (실제 운영에서는 외부 설정으로 관리해야 함)
    private final String SECRET_KEY = "mySecretKeyForJwtTokenGenerationThatShouldBeLongEnoughForHS256Algorithm";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    
    // 토큰 만료 시간 설정
    private final int ACCESS_TOKEN_HOURS = 1;    // Access Token: 1시간
    private final int REFRESH_TOKEN_DAYS = 30;   // Refresh Token: 30일
    
    // Access Token 생성
    public String generateAccessToken(Long userId, String mainCharacterName) {
        Instant now = Instant.now();
        Instant expiration = now.plus(ACCESS_TOKEN_HOURS, ChronoUnit.HOURS);
        
        return Jwts.builder()
                .subject(userId.toString())
                .claim("mainCharacterName", mainCharacterName)
                .claim("tokenType", "ACCESS")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }
    
    // Refresh Token 생성
    public String generateRefreshToken(Long userId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS);
        
        return Jwts.builder()
                .subject(userId.toString())
                .claim("tokenType", "REFRESH")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }
    
    // 기존 메서드 유지 (호환성)
    public String generateToken(Long userId, String mainCharacterName) {
        return generateAccessToken(userId, mainCharacterName);
    }
    
    // JWT 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        Claims claims = validateAndGetClaims(token);
        return Long.parseLong(claims.getSubject());
    }
    
    // JWT 토큰에서 메인 캐릭터명 추출
    public String getMainCharacterNameFromToken(String token) {
        Claims claims = validateAndGetClaims(token);
        return claims.get("mainCharacterName", String.class);
    }
    
    // 토큰 타입 확인 (ACCESS/REFRESH)
    public String getTokenType(String token) {
        Claims claims = validateAndGetClaims(token);
        return claims.get("tokenType", String.class);
    }
    
    // Access Token인지 확인
    public boolean isAccessToken(String token) {
        return "ACCESS".equals(getTokenType(token));
    }
    
    // Refresh Token인지 확인
    public boolean isRefreshToken(String token) {
        return "REFRESH".equals(getTokenType(token));
    }
    
    // JWT 토큰 유효성 검증
    public boolean isTokenValid(String token) {
        try {
            validateAndGetClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    // JWT 토큰 검증 및 Claims 반환
    private Claims validateAndGetClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
} 