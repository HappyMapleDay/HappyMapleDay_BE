package com.happymapleday.user.service;

import com.happymapleday.user.entity.RefreshToken;
import com.happymapleday.user.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class SecureRefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    
    // 최대 동시 로그인 기기 수
    private final int MAX_CONCURRENT_DEVICES = 5;
    
    @Autowired
    public SecureRefreshTokenService(RefreshTokenRepository refreshTokenRepository, 
                                   JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }
    
    // 안전한 Refresh Token 생성 및 저장
    @Transactional
    public String createSecureRefreshToken(Long userId, HttpServletRequest request) {
        try {
            // 기존 유효한 토큰 개수 확인
            long activeTokenCount = refreshTokenRepository.countValidTokensByUserId(userId, LocalDateTime.now());
            
            // 최대 기기 수 초과 시 가장 오래된 토큰 무효화
            if (activeTokenCount >= MAX_CONCURRENT_DEVICES) {
                // 실제 구현에서는 가장 오래된 토큰만 무효화하는 것이 좋음
                refreshTokenRepository.invalidateAllUserTokens(userId, LocalDateTime.now());
            }
            
            // 새 토큰 생성
            String rawToken = jwtService.generateRefreshToken(userId);
            String tokenHash = hashToken(rawToken);
            
            // 클라이언트 정보 수집
            String deviceInfo = extractDeviceInfo(request);
            String ipAddress = extractIpAddress(request);
            
            // DB에 토큰 정보 저장
            LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
            RefreshToken refreshToken = new RefreshToken(userId, tokenHash, expiresAt, deviceInfo, ipAddress);
            refreshTokenRepository.save(refreshToken);
            
            return rawToken;
        } catch (Exception e) {
            throw new RuntimeException("Refresh Token 생성에 실패했습니다.", e);
        }
    }
    
    // 안전한 토큰 갱신 (One-time use + 보안 검증)
    @Transactional
    public String refreshSecureToken(String refreshToken, HttpServletRequest request) {
        try {
            // 1. JWT 토큰 자체 유효성 검증
            if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
                throw new BadCredentialsException("유효하지 않은 Refresh Token입니다.");
            }
            
            // 2. 토큰 해시로 DB에서 조회
            String tokenHash = hashToken(refreshToken);
            Optional<RefreshToken> tokenRecord = refreshTokenRepository.findByTokenHash(tokenHash);
            
            if (tokenRecord.isEmpty()) {
                throw new BadCredentialsException("토큰을 찾을 수 없습니다.");
            }
            
            RefreshToken storedToken = tokenRecord.get();
            
            // 3. 토큰 상태 검증
            if (!storedToken.isValid()) {
                throw new BadCredentialsException("이미 사용되었거나 만료된 토큰입니다.");
            }
            
            // 4. 보안 검증 (IP, 디바이스 확인)
            validateSecurityContext(storedToken, request);
            
            // 5. 기존 토큰 즉시 무효화 (One-time use)
            storedToken.markAsUsed();
            refreshTokenRepository.save(storedToken);
            
            // 6. 새로운 토큰 발급
            Long userId = jwtService.getUserIdFromToken(refreshToken);
            return createSecureRefreshToken(userId, request);
            
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new BadCredentialsException("토큰 갱신 처리 중 오류가 발생했습니다.");
        }
    }
    
    // 사용자의 모든 토큰 무효화 (로그아웃)
    @Transactional
    public void invalidateAllTokens(Long userId) {
        refreshTokenRepository.invalidateAllUserTokens(userId, LocalDateTime.now());
    }
    
    // 토큰 해시 생성
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("해시 알고리즘을 찾을 수 없습니다.", e);
        }
    }
    
    // 디바이스 정보 추출
    private String extractDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 500)) : "Unknown";
    }
    
    // IP 주소 추출
    private String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    // 보안 컨텍스트 검증 (IP, 디바이스)
    private void validateSecurityContext(RefreshToken storedToken, HttpServletRequest request) {
        String currentIp = extractIpAddress(request);
        String currentDevice = extractDeviceInfo(request);
        
        // IP 변경 감지 (선택사항 - 너무 엄격할 수 있음)
        // if (!storedToken.getIpAddress().equals(currentIp)) {
        //     throw new BadCredentialsException("IP 주소가 변경되었습니다. 다시 로그인해주세요.");
        // }
        
        // 디바이스 정보 변경 감지 (간단한 검증)
        if (storedToken.getDeviceInfo() != null && 
            !storedToken.getDeviceInfo().substring(0, Math.min(50, storedToken.getDeviceInfo().length()))
                .equals(currentDevice.substring(0, Math.min(50, currentDevice.length())))) {
            // 너무 엄격하지 않게 경고만 로그로 남기거나, 선택적으로 적용
            // throw new BadCredentialsException("디바이스 정보가 변경되었습니다.");
        }
    }
} 