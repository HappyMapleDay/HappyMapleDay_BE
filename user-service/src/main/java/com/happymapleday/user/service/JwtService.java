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
    
    // 토큰 만료 시간 (24시간)
    private final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24시간을 밀리초로
    
    // JWT 토큰 생성
    public String generateToken(Long userId, String mainCharacterName) {
        Instant now = Instant.now();
        Instant expiration = now.plus(24, ChronoUnit.HOURS); // 24시간 후
        
        return Jwts.builder()
                .subject(userId.toString()) // 사용자 ID를 subject로 설정
                .claim("mainCharacterName", mainCharacterName)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
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