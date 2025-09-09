package com.happymapleday.user.service;

import com.happymapleday.user.entity.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String testSecretKey = "mySecretKeyForTestingPurposes123456789";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecretKey);
    }

    @Test
    void testGenerateAccessTokenWithRole() {
        // given
        Long userId = 123L;
        String mainCharacterName = "testUser";
        UserRole role = UserRole.ADMIN;

        // when
        String token = jwtService.generateAccessToken(userId, mainCharacterName, role);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        // JWT 구조 확인 (header.payload.signature)
        String[] tokenParts = token.split("\\.");
        assertThat(tokenParts).hasSize(3);
    }

    @Test
    void testGenerateAccessTokenWithDefaultRole() {
        // given
        Long userId = 123L;
        String mainCharacterName = "testUser";

        // when
        String token = jwtService.generateAccessToken(userId, mainCharacterName);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        // 기본 권한이 NORMAL인지 확인
        UserRole extractedRole = jwtService.getRoleFromToken(token);
        assertThat(extractedRole).isEqualTo(UserRole.NORMAL);
    }

    @Test
    void testExtractUserIdFromToken() {
        // given
        Long userId = 123L;
        String mainCharacterName = "testUser";
        UserRole role = UserRole.ADMIN;
        String token = jwtService.generateAccessToken(userId, mainCharacterName, role);

        // when
        Long extractedUserId = jwtService.getUserIdFromToken(token);

        // then
        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    void testExtractMainCharacterNameFromToken() {
        // given
        Long userId = 123L;
        String mainCharacterName = "testUser";
        UserRole role = UserRole.ADMIN;
        String token = jwtService.generateAccessToken(userId, mainCharacterName, role);

        // when
        String extractedName = jwtService.getMainCharacterNameFromToken(token);

        // then
        assertThat(extractedName).isEqualTo(mainCharacterName);
    }

    @Test
    void testExtractRoleFromToken() {
        // given
        Long userId = 123L;
        String mainCharacterName = "testUser";
        UserRole role = UserRole.ADMIN;
        String token = jwtService.generateAccessToken(userId, mainCharacterName, role);

        // when
        UserRole extractedRole = jwtService.getRoleFromToken(token);

        // then
        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    void testExtractRoleFromTokenWithNormalRole() {
        // given
        Long userId = 123L;
        String mainCharacterName = "testUser";
        UserRole role = UserRole.NORMAL;
        String token = jwtService.generateAccessToken(userId, mainCharacterName, role);

        // when
        UserRole extractedRole = jwtService.getRoleFromToken(token);

        // then
        assertThat(extractedRole).isEqualTo(UserRole.NORMAL);
    }

    @Test
    void testIsAdminFromToken() {
        // given
        Long userId = 123L;
        String mainCharacterName = "testUser";
        
        String adminToken = jwtService.generateAccessToken(userId, mainCharacterName, UserRole.ADMIN);
        String normalToken = jwtService.generateAccessToken(userId, mainCharacterName, UserRole.NORMAL);

        // when & then
        assertThat(jwtService.isAdminFromToken(adminToken)).isTrue();
        assertThat(jwtService.isAdminFromToken(normalToken)).isFalse();
    }

    @Test
    void testTokenValidation() {
        // given
        Long userId = 123L;
        String mainCharacterName = "testUser";
        UserRole role = UserRole.ADMIN;
        String token = jwtService.generateAccessToken(userId, mainCharacterName, role);

        // when & then
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void testInvalidTokenValidation() {
        // given
        String invalidToken = "invalid.token.here";

        // when & then
        assertThat(jwtService.isTokenValid(invalidToken)).isFalse();
    }

    @Test
    void testGenerateRefreshToken() {
        // given
        Long userId = 123L;

        // when
        String refreshToken = jwtService.generateRefreshToken(userId);

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        
        // Refresh Token 타입 확인
        String tokenType = jwtService.getTokenType(refreshToken);
        assertThat(tokenType).isEqualTo("REFRESH");
        
        // 사용자 ID 추출 확인
        Long extractedUserId = jwtService.getUserIdFromToken(refreshToken);
        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    void testAccessTokenType() {
        // given
        Long userId = 123L;
        String mainCharacterName = "testUser";
        UserRole role = UserRole.ADMIN;
        String token = jwtService.generateAccessToken(userId, mainCharacterName, role);

        // when
        String tokenType = jwtService.getTokenType(token);

        // then
        assertThat(tokenType).isEqualTo("ACCESS");
    }

    @Test
    void testIsRefreshToken() {
        // given
        Long userId = 123L;
        String mainCharacterName = "testUser";
        
        String accessToken = jwtService.generateAccessToken(userId, mainCharacterName, UserRole.ADMIN);
        String refreshToken = jwtService.generateRefreshToken(userId);

        // when & then
        assertThat(jwtService.isRefreshToken(accessToken)).isFalse();
        assertThat(jwtService.isRefreshToken(refreshToken)).isTrue();
    }

    @Test
    void testGetRoleFromTokenWithInvalidRole() {
        // given - 수동으로 잘못된 role이 포함된 토큰 생성
        Long userId = 123L;
        String mainCharacterName = "testUser";
        
        // 잘못된 role 값으로 토큰 생성
        String tokenWithInvalidRole = Jwts.builder()
                .subject(userId.toString())
                .claim("characterName", mainCharacterName)
                .claim("role", "INVALID_ROLE")
                .claim("tokenType", "ACCESS")
                .signWith(Keys.hmacShaKeyFor(testSecretKey.getBytes()))
                .compact();

        // when
        UserRole extractedRole = jwtService.getRoleFromToken(tokenWithInvalidRole);

        // then - 잘못된 값인 경우 기본값(NORMAL) 반환
        assertThat(extractedRole).isEqualTo(UserRole.NORMAL);
    }

    @Test
    void testGetRoleFromTokenWithNullRole() {
        // given - role 클레임이 없는 토큰 생성
        Long userId = 123L;
        String mainCharacterName = "testUser";
        
        String tokenWithoutRole = Jwts.builder()
                .subject(userId.toString())
                .claim("characterName", mainCharacterName)
                .claim("tokenType", "ACCESS")
                .signWith(Keys.hmacShaKeyFor(testSecretKey.getBytes()))
                .compact();

        // when
        UserRole extractedRole = jwtService.getRoleFromToken(tokenWithoutRole);

        // then - role이 없는 경우 기본값(NORMAL) 반환
        assertThat(extractedRole).isEqualTo(UserRole.NORMAL);
    }
}
