package com.happymapleday.user.repository;

import com.happymapleday.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    // 토큰 해시로 조회
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    
    // 사용자 ID로 유효한 토큰들 조회
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId AND rt.isUsed = false AND rt.expiresAt > :now")
    Optional<RefreshToken> findValidTokenByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // 사용자의 모든 토큰 무효화 (로그아웃 시 사용)
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isUsed = true, rt.usedAt = :now WHERE rt.userId = :userId AND rt.isUsed = false")
    void invalidateAllUserTokens(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // 만료된 토큰들 정리 (배치 작업용)
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :cutoffDate")
    void deleteExpiredTokens(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // 사용자의 토큰 개수 확인 (기기 제한용)
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.userId = :userId AND rt.isUsed = false AND rt.expiresAt > :now")
    long countValidTokensByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
} 