package com.happymapleday.user.service;

import com.happymapleday.user.entity.UserRole;
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
    
    // application.yml에서 시크릿 키 주입
    @Value("${jwt.secret}")
    private String secretKey;
    
    private SecretKey getSigningKey() {
        // HS256 알고리즘을 위해 32바이트(256비트) 키 생성
        byte[] keyBytes = secretKey.getBytes();
        if (keyBytes.length < 32) {
            // 32바이트로 패딩
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            return Keys.hmacShaKeyFor(paddedKey);
        } else {
            // 32바이트로 자르기
            byte[] truncatedKey = new byte[32];
            System.arraycopy(keyBytes, 0, truncatedKey, 0, 32);
            return Keys.hmacShaKeyFor(truncatedKey);
        }
    }
    
    // 토큰 만료 시간 설정
    private final int ACCESS_TOKEN_HOURS = 1;    // Access Token: 1시간
    private final int REFRESH_TOKEN_DAYS = 30;   // Refresh Token: 30일
    
    // Access Token 생성 (role 정보 포함)
    public String generateAccessToken(Long userId, String mainCharacterName, UserRole role) {
        Instant now = Instant.now();
        Instant expiration = now.plus(ACCESS_TOKEN_HOURS, ChronoUnit.HOURS);
        
        return Jwts.builder()
                .subject(userId.toString())
                .claim("characterName", mainCharacterName)
                .claim("role", role.name()) // 권한 정보 포함
                .claim("tokenType", "ACCESS")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(getSigningKey())
                .compact();
    }
    
    // 기존 메서드 호환성 유지 (기본값: NORMAL)
    public String generateAccessToken(Long userId, String mainCharacterName) {
        return generateAccessToken(userId, mainCharacterName, UserRole.NORMAL);
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
                .signWith(getSigningKey())
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
        return claims.get("characterName", String.class);
    }
    
    // JWT 토큰에서 권한 정보 추출
    public UserRole getRoleFromToken(String token) {
        Claims claims = validateAndGetClaims(token);
        String roleString = claims.get("role", String.class);
        try {
            return roleString != null ? UserRole.valueOf(roleString) : UserRole.NORMAL;
        } catch (IllegalArgumentException e) {
            return UserRole.NORMAL; // 잘못된 값인 경우 기본값
        }
    }
    
    // JWT 토큰에서 어드민 권한 확인
    public boolean isAdminFromToken(String token) {
        return getRoleFromToken(token).isAdmin();
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
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
} 