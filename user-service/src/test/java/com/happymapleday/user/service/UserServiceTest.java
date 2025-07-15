package com.happymapleday.user.service;

import com.happymapleday.user.dto.ApiKeyValidationRequestDto;
import com.happymapleday.user.dto.ApiKeyValidationResponseDto;
import com.happymapleday.user.dto.LoginRequestDto;
import com.happymapleday.user.dto.LoginResponseDto;
import com.happymapleday.user.dto.LogoutRequestDto;
import com.happymapleday.user.dto.LogoutResponseDto;
import com.happymapleday.user.dto.PasswordResetRequestDto;
import com.happymapleday.user.dto.PasswordResetResponseDto;
import com.happymapleday.user.dto.RefreshTokenRequestDto;
import com.happymapleday.user.dto.RefreshTokenResponseDto;
import com.happymapleday.user.dto.SignupRequestDto;
import com.happymapleday.user.dto.SignupResponseDto;
import com.happymapleday.user.dto.MainCharacterUpdateRequestDto;
import com.happymapleday.user.dto.MainCharacterUpdateResponseDto;
import com.happymapleday.user.dto.UserSettingsResponseDto;
import com.happymapleday.user.dto.PrivacySettingsUpdateRequestDto;
import com.happymapleday.user.dto.WeeklyResetSettingsUpdateRequestDto;
import com.happymapleday.user.dto.PasswordChangeRequestDto;
import com.happymapleday.user.dto.PasswordChangeResponseDto;
import com.happymapleday.user.entity.User;
import com.happymapleday.user.entity.UserSettings;
import com.happymapleday.user.repository.UserRepository;
import com.happymapleday.user.repository.UserSettingsRepository;
import com.happymapleday.user.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

        @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserSettingsRepository userSettingsRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private EncryptionService encryptionService;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private SecureRefreshTokenService secureRefreshTokenService;
    
    @Mock
    private NexonApiService nexonApiService;

    @InjectMocks
    private UserService userService;

    private SignupRequestDto signupRequest;
    private LoginRequestDto loginRequest;
    private RefreshTokenRequestDto refreshTokenRequest;
    private LogoutRequestDto logoutRequest;
    private PasswordResetRequestDto passwordResetRequest;
    private PasswordChangeRequestDto passwordChangeRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequestDto(
            "test-api-key",
            "testCharacter",
            null,
            "password123",
            "password123",
            true
        );

        loginRequest = new LoginRequestDto("testCharacter", "password123");
        refreshTokenRequest = new RefreshTokenRequestDto("valid-refresh-token");
        logoutRequest = new LogoutRequestDto("valid-refresh-token");
        passwordResetRequest = new PasswordResetRequestDto("testCharacter", "test_api_key_12345");
        passwordChangeRequest = new PasswordChangeRequestDto("testCharacter", "newPassword123");

        savedUser = new User("testCharacter", "encodedPassword", "encryptedApiKey");
        // 리플렉션을 사용하여 ID와 생성시간 설정
        setField(savedUser, "id", 1L);
        setField(savedUser, "createdAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() {
        // given
        given(userRepository.existsByMainCharacterName(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(encryptionService.encrypt(anyString())).willReturn("encryptedApiKey");
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        UserSettings mockUserSettings = new UserSettings(1L, true, true);
        given(userSettingsRepository.save(any(UserSettings.class))).willReturn(mockUserSettings);

        // when
        SignupResponseDto response = userService.signup(signupRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("회원가입이 완료되었습니다.");

        verify(userRepository).existsByMainCharacterName("testCharacter");
        verify(passwordEncoder).encode("password123");
        verify(encryptionService).encrypt("test-api-key");
        verify(userRepository).save(any(User.class));
        verify(userSettingsRepository).save(any(UserSettings.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        given(userRepository.findByMainCharacterName("testCharacter")).willReturn(Optional.of(savedUser));
        given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
        given(jwtService.generateAccessToken(anyLong(), anyString())).willReturn("access-token");
        given(jwtService.generateRefreshToken(anyLong())).willReturn("refresh-token");

        // when
        LoginResponseDto response = userService.login(loginRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUser().getId()).isEqualTo(1L);
        assertThat(response.getUser().getMainCharacterName()).isEqualTo("testCharacter");

        verify(userRepository).findByMainCharacterName("testCharacter");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService).generateAccessToken(1L, "testCharacter");
        verify(jwtService).generateRefreshToken(1L);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_UserNotFound_ThrowsException() {
        // given
        given(userRepository.findByMainCharacterName("nonExistentUser")).willReturn(Optional.empty());

        LoginRequestDto nonExistentUserRequest = new LoginRequestDto("nonExistentUser", "password123");

        // when & then
        assertThatThrownBy(() -> userService.login(nonExistentUserRequest))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("아이디 또는 비밀번호가 잘못되었습니다.");

        verify(userRepository).findByMainCharacterName("nonExistentUser");
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_WrongPassword_ThrowsException() {
        // given
        given(userRepository.findByMainCharacterName("testCharacter")).willReturn(Optional.of(savedUser));
        given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

        LoginRequestDto wrongPasswordRequest = new LoginRequestDto("testCharacter", "wrongPassword");

        // when & then
        assertThatThrownBy(() -> userService.login(wrongPasswordRequest))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("아이디 또는 비밀번호가 잘못되었습니다.");

        verify(userRepository).findByMainCharacterName("testCharacter");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    }

    @Test
    @DisplayName("중복된 메인 캐릭터명으로 회원가입 실패")
    void signup_DuplicateMainCharacterName_ThrowsException() {
        // given
        given(userRepository.existsByMainCharacterName(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signup(signupRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 존재하는 메인 캐릭터명입니다.");
    }

    @Test
    @DisplayName("메인 캐릭터명 중복 체크 - 존재하는 경우")
    void isMainCharacterNameExists_ExistingName_ReturnsTrue() {
        // given
        given(userRepository.existsByMainCharacterName("existingCharacter")).willReturn(true);

        // when
        boolean result = userService.isMainCharacterNameExists("existingCharacter");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("메인 캐릭터명 중복 체크 - 존재하지 않는 경우")
    void isMainCharacterNameExists_NonExistingName_ReturnsFalse() {
        // given
        given(userRepository.existsByMainCharacterName("newCharacter")).willReturn(false);

        // when
        boolean result = userService.isMainCharacterNameExists("newCharacter");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("로그인 실패 - JWT 토큰 생성 실패")
    void login_JwtGenerationFailed_ThrowsException() {
        // given
        given(userRepository.findByMainCharacterName("testCharacter")).willReturn(Optional.of(savedUser));
        given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
        given(jwtService.generateAccessToken(anyLong(), anyString()))
                .willThrow(new RuntimeException("JWT 생성 실패"));

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("JWT 생성 실패");

        verify(userRepository).findByMainCharacterName("testCharacter");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService).generateAccessToken(1L, "testCharacter");
    }

    @Test
    @DisplayName("로그인 실패 - Refresh Token 생성 실패")
    void login_RefreshTokenGenerationFailed_ThrowsException() {
        // given
        given(userRepository.findByMainCharacterName("testCharacter")).willReturn(Optional.of(savedUser));
        given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
        given(jwtService.generateAccessToken(anyLong(), anyString())).willReturn("access-token");
        given(jwtService.generateRefreshToken(anyLong()))
                .willThrow(new RuntimeException("Refresh Token 생성 실패"));

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Refresh Token 생성 실패");

        verify(userRepository).findByMainCharacterName("testCharacter");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService).generateAccessToken(1L, "testCharacter");
        verify(jwtService).generateRefreshToken(1L);
    }

    @Test
    @DisplayName("로그인 실패 - 데이터베이스 조회 실패")
    void login_DatabaseError_ThrowsException() {
        // given
        given(userRepository.findByMainCharacterName("testCharacter"))
                .willThrow(new RuntimeException("데이터베이스 연결 실패"));

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("데이터베이스 연결 실패");

        verify(userRepository).findByMainCharacterName("testCharacter");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 매칭 중 에러")
    void login_PasswordMatchingError_ThrowsException() {
        // given
        given(userRepository.findByMainCharacterName("testCharacter")).willReturn(Optional.of(savedUser));
        given(passwordEncoder.matches("password123", "encodedPassword"))
                .willThrow(new RuntimeException("비밀번호 매칭 실패"));

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("비밀번호 매칭 실패");

        verify(userRepository).findByMainCharacterName("testCharacter");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    void refreshToken_Success() {
        // given
        given(jwtService.isTokenValid("valid-refresh-token")).willReturn(true);
        given(jwtService.isRefreshToken("valid-refresh-token")).willReturn(true);
        given(jwtService.getUserIdFromToken("valid-refresh-token")).willReturn(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(savedUser));
        given(jwtService.generateAccessToken(1L, "testCharacter")).willReturn("new-access-token");
        given(jwtService.generateRefreshToken(1L)).willReturn("new-refresh-token");

        // when
        RefreshTokenResponseDto response = userService.refreshToken(refreshTokenRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");

        verify(jwtService).isTokenValid("valid-refresh-token");
        verify(jwtService).isRefreshToken("valid-refresh-token");
        verify(jwtService).getUserIdFromToken("valid-refresh-token");
        verify(userRepository).findById(1L);
        verify(jwtService).generateAccessToken(1L, "testCharacter");
        verify(jwtService).generateRefreshToken(1L);
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 유효하지 않은 토큰")
    void refreshToken_InvalidToken_ThrowsException() {
        // given
        given(jwtService.isTokenValid("invalid-token")).willReturn(false);
        RefreshTokenRequestDto invalidRequest = new RefreshTokenRequestDto("invalid-token");

        // when & then
        assertThatThrownBy(() -> userService.refreshToken(invalidRequest))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("유효하지 않은 Refresh Token입니다.");

        verify(jwtService).isTokenValid("invalid-token");
    }

    @Test
    @DisplayName("토큰 갱신 실패 - Refresh Token이 아님")
    void refreshToken_NotRefreshToken_ThrowsException() {
        // given
        given(jwtService.isTokenValid("access-token")).willReturn(true);
        given(jwtService.isRefreshToken("access-token")).willReturn(false);
        RefreshTokenRequestDto accessTokenRequest = new RefreshTokenRequestDto("access-token");

        // when & then
        assertThatThrownBy(() -> userService.refreshToken(accessTokenRequest))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("올바른 Refresh Token이 아닙니다.");

        verify(jwtService).isTokenValid("access-token");
        verify(jwtService).isRefreshToken("access-token");
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 사용자를 찾을 수 없음")
    void refreshToken_UserNotFound_ThrowsException() {
        // given
        given(jwtService.isTokenValid("valid-refresh-token")).willReturn(true);
        given(jwtService.isRefreshToken("valid-refresh-token")).willReturn(true);
        given(jwtService.getUserIdFromToken("valid-refresh-token")).willReturn(999L);
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.refreshToken(refreshTokenRequest))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");

        verify(jwtService).isTokenValid("valid-refresh-token");
        verify(jwtService).isRefreshToken("valid-refresh-token");
        verify(jwtService).getUserIdFromToken("valid-refresh-token");
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("토큰 갱신 실패 - JWT 예외 발생")
    void refreshToken_JwtException_ThrowsException() {
        // given
        given(jwtService.isTokenValid("valid-refresh-token")).willReturn(true);
        given(jwtService.isRefreshToken("valid-refresh-token")).willReturn(true);
        given(jwtService.getUserIdFromToken("valid-refresh-token"))
            .willThrow(new io.jsonwebtoken.JwtException("토큰 파싱 실패"));

        // when & then
        assertThatThrownBy(() -> userService.refreshToken(refreshTokenRequest))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("토큰 갱신에 실패했습니다.");

        verify(jwtService).isTokenValid("valid-refresh-token");
        verify(jwtService).isRefreshToken("valid-refresh-token");
        verify(jwtService).getUserIdFromToken("valid-refresh-token");
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_Success() {
        // given
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            given(jwtService.isTokenValid("valid-refresh-token")).willReturn(true);
            given(jwtService.getUserIdFromToken("valid-refresh-token")).willReturn(1L);

            // when
            LogoutResponseDto response = userService.logout(logoutRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("로그아웃이 완료되었습니다.");

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserId);
            verify(jwtService).isTokenValid("valid-refresh-token");
            verify(jwtService).getUserIdFromToken("valid-refresh-token");
            verify(secureRefreshTokenService).invalidateAllTokens(1L);
        }
    }

    @Test
    @DisplayName("로그아웃 실패 - 유효하지 않은 Refresh Token")
    void logout_InvalidToken_ThrowsException() {
        // given
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            given(jwtService.isTokenValid("invalid-token")).willReturn(false);
            
            LogoutRequestDto invalidRequest = new LogoutRequestDto("invalid-token");

            // when & then
            assertThatThrownBy(() -> userService.logout(invalidRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("유효하지 않은 Refresh Token입니다.");

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserId);
            verify(jwtService).isTokenValid("invalid-token");
        }
    }

    @Test
    @DisplayName("로그아웃 실패 - 토큰 사용자와 현재 사용자 불일치")
    void logout_UserMismatch_ThrowsException() {
        // given
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            given(jwtService.isTokenValid("valid-refresh-token")).willReturn(true);
            given(jwtService.getUserIdFromToken("valid-refresh-token")).willReturn(2L); // 다른 사용자 ID

            // when & then
            assertThatThrownBy(() -> userService.logout(logoutRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("토큰의 사용자 정보가 일치하지 않습니다.");

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserId);
            verify(jwtService).isTokenValid("valid-refresh-token");
            verify(jwtService).getUserIdFromToken("valid-refresh-token");
        }
    }

    @Test
    @DisplayName("로그아웃 실패 - JWT 서비스 예외")
    void logout_JwtServiceException_ThrowsException() {
        // given
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            given(jwtService.isTokenValid("valid-refresh-token"))
                .willThrow(new RuntimeException("JWT 파싱 실패"));

            // when & then
            assertThatThrownBy(() -> userService.logout(logoutRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("로그아웃 처리 중 오류가 발생했습니다.");

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserId);
            verify(jwtService).isTokenValid("valid-refresh-token");
        }
    }

    @Test
    @DisplayName("로그아웃 실패 - SecurityUtil 예외")
    void logout_SecurityUtilException_ThrowsException() {
        // given
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId)
                .thenThrow(new RuntimeException("인증 정보를 찾을 수 없습니다"));

            // when & then
            assertThatThrownBy(() -> userService.logout(logoutRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("로그아웃 처리 중 오류가 발생했습니다.");

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserId);
        }
    }

    @Test
    @DisplayName("로그아웃 실패 - SecureRefreshTokenService 예외")
    void logout_SecureRefreshTokenServiceException_ThrowsException() {
        // given
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            given(jwtService.isTokenValid("valid-refresh-token")).willReturn(true);
            given(jwtService.getUserIdFromToken("valid-refresh-token")).willReturn(1L);
            doThrow(new RuntimeException("데이터베이스 연결 실패"))
                .when(secureRefreshTokenService).invalidateAllTokens(1L);

            // when & then
            assertThatThrownBy(() -> userService.logout(logoutRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("로그아웃 처리 중 오류가 발생했습니다.");

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserId);
            verify(jwtService).isTokenValid("valid-refresh-token");
            verify(jwtService).getUserIdFromToken("valid-refresh-token");
            verify(secureRefreshTokenService).invalidateAllTokens(1L);
        }
    }

    @Test
    @DisplayName("비밀번호 재설정 성공")
    void resetPassword_Success() {
        // given
        NexonApiService.ApiKeyValidationResult apiResult = new NexonApiService.ApiKeyValidationResult(true, 15, null);
        given(userRepository.findByMainCharacterName("testCharacter")).willReturn(Optional.of(savedUser));
        given(nexonApiService.validateApiKey("test_api_key_12345")).willReturn(apiResult);
        given(encryptionService.decrypt("encryptedApiKey")).willReturn("test_api_key_12345");
        given(passwordEncoder.encode(anyString())).willReturn("encodedTemporaryPassword");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        PasswordResetResponseDto response = userService.resetPassword(passwordResetRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("임시 비밀번호가 생성되었습니다. 로그인 후 비밀번호를 변경해주세요.");
        assertThat(response.getTemporaryPassword()).isNotNull();
        assertThat(response.getTemporaryPassword()).hasSize(8);
        
        // 임시 비밀번호 패턴 검증 (영문 대소문자, 숫자, 특수문자 포함)
        String tempPassword = response.getTemporaryPassword();
        assertThat(tempPassword).matches(".*[A-Z].*"); // 대문자 포함
        assertThat(tempPassword).matches(".*[a-z].*"); // 소문자 포함
        assertThat(tempPassword).matches(".*[0-9].*"); // 숫자 포함
        assertThat(tempPassword).matches(".*[!@#$%^&*].*"); // 특수문자 포함

        verify(userRepository).findByMainCharacterName("testCharacter");
        verify(nexonApiService).validateApiKey("test_api_key_12345");
        verify(encryptionService).decrypt("encryptedApiKey");
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(savedUser);
        verify(secureRefreshTokenService).invalidateAllTokens(1L);
    }

    @Test
    @DisplayName("비밀번호 재설정 실패 - 존재하지 않는 사용자")
    void resetPassword_UserNotFound_ThrowsException() {
        // given
        given(userRepository.findByMainCharacterName("nonExistentUser")).willReturn(Optional.empty());
        
        PasswordResetRequestDto nonExistentUserRequest = new PasswordResetRequestDto("nonExistentUser", "test_api_key_12345");

        // when & then
        assertThatThrownBy(() -> userService.resetPassword(nonExistentUserRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 사용자입니다.");

        verify(userRepository).findByMainCharacterName("nonExistentUser");
    }

    @Test
    @DisplayName("비밀번호 재설정 실패 - 유효하지 않은 API Key")
    void resetPassword_InvalidApiKey_ThrowsException() {
        // given
        NexonApiService.ApiKeyValidationResult apiResult = new NexonApiService.ApiKeyValidationResult(false, 0, "유효하지 않은 API Key입니다.");
        given(userRepository.findByMainCharacterName("testCharacter")).willReturn(Optional.of(savedUser));
        given(nexonApiService.validateApiKey("invalid_api_key")).willReturn(apiResult);
        
        PasswordResetRequestDto invalidApiKeyRequest = new PasswordResetRequestDto("testCharacter", "invalid_api_key");

        // when & then
        assertThatThrownBy(() -> userService.resetPassword(invalidApiKeyRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효하지 않은 Nexon API Key입니다.");

        verify(userRepository).findByMainCharacterName("testCharacter");
        verify(nexonApiService).validateApiKey("invalid_api_key");
    }

    @Test
    @DisplayName("비밀번호 재설정 실패 - API Key 불일치")
    void resetPassword_ApiKeyMismatch_ThrowsException() {
        // given
        NexonApiService.ApiKeyValidationResult apiResult = new NexonApiService.ApiKeyValidationResult(true, 15, null);
        given(userRepository.findByMainCharacterName("testCharacter")).willReturn(Optional.of(savedUser));
        given(nexonApiService.validateApiKey("different_api_key")).willReturn(apiResult);
        given(encryptionService.decrypt("encryptedApiKey")).willReturn("test_api_key_12345");
        
        PasswordResetRequestDto mismatchApiKeyRequest = new PasswordResetRequestDto("testCharacter", "different_api_key");

        // when & then
        assertThatThrownBy(() -> userService.resetPassword(mismatchApiKeyRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("일치하는 사용자 정보를 찾을 수 없습니다.");

        verify(userRepository).findByMainCharacterName("testCharacter");
        verify(nexonApiService).validateApiKey("different_api_key");
        verify(encryptionService).decrypt("encryptedApiKey");
    }

    @Test
    @DisplayName("비밀번호 재설정 - 임시 비밀번호 생성 확인")
    void resetPassword_TemporaryPasswordGeneration() {
        // given
        NexonApiService.ApiKeyValidationResult apiResult = new NexonApiService.ApiKeyValidationResult(true, 15, null);
        given(userRepository.findByMainCharacterName("testCharacter")).willReturn(Optional.of(savedUser));
        given(nexonApiService.validateApiKey("test_api_key_12345")).willReturn(apiResult);
        given(encryptionService.decrypt("encryptedApiKey")).willReturn("test_api_key_12345");
        given(passwordEncoder.encode(anyString())).willReturn("encodedTemporaryPassword");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        PasswordResetResponseDto response1 = userService.resetPassword(passwordResetRequest);
        PasswordResetResponseDto response2 = userService.resetPassword(passwordResetRequest);

        // then - 매번 다른 임시 비밀번호가 생성되어야 함
        assertThat(response1.getTemporaryPassword()).isNotEqualTo(response2.getTemporaryPassword());
        
        // 두 임시 비밀번호 모두 요구사항 충족
        assertThat(response1.getTemporaryPassword()).hasSize(8);
        assertThat(response2.getTemporaryPassword()).hasSize(8);
    }

    @Test
    @DisplayName("비밀번호 재설정 - 비밀번호 암호화 확인")
    void resetPassword_PasswordEncryption() {
        // given
        NexonApiService.ApiKeyValidationResult apiResult = new NexonApiService.ApiKeyValidationResult(true, 15, null);
        given(userRepository.findByMainCharacterName("testCharacter")).willReturn(Optional.of(savedUser));
        given(nexonApiService.validateApiKey("test_api_key_12345")).willReturn(apiResult);
        given(encryptionService.decrypt("encryptedApiKey")).willReturn("test_api_key_12345");
        given(passwordEncoder.encode(anyString())).willReturn("encodedTemporaryPassword");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        userService.resetPassword(passwordResetRequest);

        // then - 임시 비밀번호가 암호화되어 저장되는지 확인
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(savedUser);
    }

    @Test
    @DisplayName("비밀번호 재설정 - Refresh Token 무효화 확인")
    void resetPassword_RefreshTokenInvalidation() {
        // given
        NexonApiService.ApiKeyValidationResult apiResult = new NexonApiService.ApiKeyValidationResult(true, 15, null);
        given(userRepository.findByMainCharacterName("testCharacter")).willReturn(Optional.of(savedUser));
        given(nexonApiService.validateApiKey("test_api_key_12345")).willReturn(apiResult);
        given(encryptionService.decrypt("encryptedApiKey")).willReturn("test_api_key_12345");
        given(passwordEncoder.encode(anyString())).willReturn("encodedTemporaryPassword");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        userService.resetPassword(passwordResetRequest);

        // then - 보안상 해당 사용자의 모든 토큰이 무효화되어야 함
        verify(secureRefreshTokenService).invalidateAllTokens(1L);
    }

    @Test
    @DisplayName("비밀번호 재설정 실패 - 데이터베이스 저장 실패")
    void resetPassword_DatabaseSaveError_ThrowsException() {
        // given
        NexonApiService.ApiKeyValidationResult apiResult = new NexonApiService.ApiKeyValidationResult(true, 15, null);
        given(userRepository.findByMainCharacterName("testCharacter")).willReturn(Optional.of(savedUser));
        given(nexonApiService.validateApiKey("test_api_key_12345")).willReturn(apiResult);
        given(encryptionService.decrypt("encryptedApiKey")).willReturn("test_api_key_12345");
        given(passwordEncoder.encode(anyString())).willReturn("encodedTemporaryPassword");
        given(userRepository.save(any(User.class))).willThrow(new RuntimeException("데이터베이스 연결 실패"));

        // when & then
        assertThatThrownBy(() -> userService.resetPassword(passwordResetRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("데이터베이스 연결 실패");

        verify(userRepository).findByMainCharacterName("testCharacter");
        verify(nexonApiService).validateApiKey("test_api_key_12345");
        verify(encryptionService).decrypt("encryptedApiKey");
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(savedUser);
    }

    // ==================== 새로운 4개 API 테스트 ====================

    @Test
    @DisplayName("본캐 변경 성공")
    void updateMainCharacter_Success() {
        // given
        Long userId = 1L;
        String previousName = "oldCharacter";
        String newName = "newCharacter";
        
        User mockUser = new User(previousName, "encodedPassword", "encryptedApiKey");
        setField(mockUser, "id", userId);
        
        MainCharacterUpdateRequestDto request = new MainCharacterUpdateRequestDto(newName);
        
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
            given(userRepository.existsByMainCharacterName(newName)).willReturn(false);
            given(userRepository.save(any(User.class))).willReturn(mockUser);
            
            // when
            MainCharacterUpdateResponseDto response = userService.updateMainCharacter(request);
            
            // then
            assertThat(response).isNotNull();
            assertThat(response.getPreviousMainCharacterName()).isEqualTo(previousName);
            assertThat(response.getNewMainCharacterName()).isEqualTo(newName);
            assertThat(response.getMessage()).isEqualTo("본캐명이 성공적으로 변경되었습니다.");
            
            verify(userRepository).findById(userId);
            verify(userRepository).existsByMainCharacterName(newName);
            verify(userRepository).save(mockUser);
        }
    }

    @Test
    @DisplayName("본캐 변경 실패 - 중복된 본캐명")
    void updateMainCharacter_DuplicateName_ThrowsException() {
        // given
        Long userId = 1L;
        String previousName = "oldCharacter";
        String newName = "existingCharacter";
        
        User mockUser = new User(previousName, "encodedPassword", "encryptedApiKey");
        setField(mockUser, "id", userId);
        
        MainCharacterUpdateRequestDto request = new MainCharacterUpdateRequestDto(newName);
        
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
            given(userRepository.existsByMainCharacterName(newName)).willReturn(true);
            
            // when & then
            assertThatThrownBy(() -> userService.updateMainCharacter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 본캐명입니다.");
            
            verify(userRepository).findById(userId);
            verify(userRepository).existsByMainCharacterName(newName);
        }
    }

    @Test
    @DisplayName("본캐 변경 실패 - 존재하지 않는 사용자")
    void updateMainCharacter_UserNotFound_ThrowsException() {
        // given
        Long userId = 1L;
        MainCharacterUpdateRequestDto request = new MainCharacterUpdateRequestDto("newCharacter");
        
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(userRepository.findById(userId)).willReturn(Optional.empty());
            
            // when & then
            assertThatThrownBy(() -> userService.updateMainCharacter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
            
            verify(userRepository).findById(userId);
        }
    }

    @Test
    @DisplayName("사용자 설정 조회 성공")
    void getUserSettings_Success() {
        // given
        Long userId = 1L;
        String mainCharacterName = "testCharacter";
        
        User mockUser = new User(mainCharacterName, "encodedPassword", "encryptedApiKey");
        setField(mockUser, "id", userId);
        
        UserSettings mockUserSettings = new UserSettings(userId, true, true);
        
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
            given(userSettingsRepository.findByUserId(userId)).willReturn(Optional.of(mockUserSettings));
            
            // when
            UserSettingsResponseDto response = userService.getUserSettings();
            
            // then
            assertThat(response).isNotNull();
            assertThat(response.getMainCharacterName()).isEqualTo(mainCharacterName);
            assertThat(response.getDataCollectionAgreed()).isTrue();
            assertThat(response.getWeeklyResetEnabled()).isTrue();
            
            verify(userRepository).findById(userId);
            verify(userSettingsRepository).findByUserId(userId);
        }
    }

    @Test
    @DisplayName("사용자 설정 조회 실패 - 존재하지 않는 사용자")
    void getUserSettings_UserNotFound_ThrowsException() {
        // given
        Long userId = 1L;
        
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(userRepository.findById(userId)).willReturn(Optional.empty());
            
            // when & then
            assertThatThrownBy(() -> userService.getUserSettings())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
            
            verify(userRepository).findById(userId);
        }
    }

    @Test
    @DisplayName("사용자 설정 조회 실패 - 존재하지 않는 사용자 설정")
    void getUserSettings_UserSettingsNotFound_ThrowsException() {
        // given
        Long userId = 1L;
        String mainCharacterName = "testCharacter";
        
        User mockUser = new User(mainCharacterName, "encodedPassword", "encryptedApiKey");
        setField(mockUser, "id", userId);
        
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
            given(userSettingsRepository.findByUserId(userId)).willReturn(Optional.empty());
            
            // when & then
            assertThatThrownBy(() -> userService.getUserSettings())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자 설정을 찾을 수 없습니다.");
            
            verify(userRepository).findById(userId);
            verify(userSettingsRepository).findByUserId(userId);
        }
    }

    @Test
    @DisplayName("개인정보 수집 동의 설정 수정 성공")
    void updatePrivacySettings_Success() {
        // given
        Long userId = 1L;
        String mainCharacterName = "testCharacter";
        
        User mockUser = new User(mainCharacterName, "encodedPassword", "encryptedApiKey");
        setField(mockUser, "id", userId);
        
        UserSettings mockUserSettings = new UserSettings(userId, true, true);
        PrivacySettingsUpdateRequestDto request = new PrivacySettingsUpdateRequestDto(false);
        
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(userSettingsRepository.findByUserId(userId)).willReturn(Optional.of(mockUserSettings));
            given(userSettingsRepository.save(any(UserSettings.class))).willReturn(mockUserSettings);
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
            
            // when
            UserSettingsResponseDto response = userService.updatePrivacySettings(request);
            
            // then
            assertThat(response).isNotNull();
            assertThat(response.getMainCharacterName()).isEqualTo(mainCharacterName);
            
            // verify - getUserSettings() 호출로 인해 2번 호출됨
            verify(userSettingsRepository, times(2)).findByUserId(userId);
            verify(userSettingsRepository).save(mockUserSettings);
            verify(userRepository).findById(userId);
        }
    }

    @Test
    @DisplayName("개인정보 수집 동의 설정 수정 실패 - 사용자 설정 없음")
    void updatePrivacySettings_UserSettingsNotFound_ThrowsException() {
        // given
        Long userId = 1L;
        PrivacySettingsUpdateRequestDto request = new PrivacySettingsUpdateRequestDto(false);
        
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(userSettingsRepository.findByUserId(userId)).willReturn(Optional.empty());
            
            // when & then
            assertThatThrownBy(() -> userService.updatePrivacySettings(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자 설정을 찾을 수 없습니다.");
            
            verify(userSettingsRepository).findByUserId(userId);
        }
    }

    @Test
    @DisplayName("주간 초기화 설정 수정 성공")
    void updateWeeklyResetSettings_Success() {
        // given
        Long userId = 1L;
        String mainCharacterName = "testCharacter";
        
        User mockUser = new User(mainCharacterName, "encodedPassword", "encryptedApiKey");
        setField(mockUser, "id", userId);
        
        UserSettings mockUserSettings = new UserSettings(userId, true, true);
        WeeklyResetSettingsUpdateRequestDto request = new WeeklyResetSettingsUpdateRequestDto(false);
        
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(userSettingsRepository.findByUserId(userId)).willReturn(Optional.of(mockUserSettings));
            given(userSettingsRepository.save(any(UserSettings.class))).willReturn(mockUserSettings);
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
            
            // when
            UserSettingsResponseDto response = userService.updateWeeklyResetSettings(request);
            
            // then
            assertThat(response).isNotNull();
            assertThat(response.getMainCharacterName()).isEqualTo(mainCharacterName);
            
            // verify - getUserSettings() 호출로 인해 2번 호출됨
            verify(userSettingsRepository, times(2)).findByUserId(userId);
            verify(userSettingsRepository).save(mockUserSettings);
            verify(userRepository).findById(userId);
        }
    }

    @Test
    @DisplayName("주간 초기화 설정 수정 실패 - 사용자 설정 없음")
    void updateWeeklyResetSettings_UserSettingsNotFound_ThrowsException() {
        // given
        Long userId = 1L;
        WeeklyResetSettingsUpdateRequestDto request = new WeeklyResetSettingsUpdateRequestDto(false);
        
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(userSettingsRepository.findByUserId(userId)).willReturn(Optional.empty());
            
            // when & then
            assertThatThrownBy(() -> userService.updateWeeklyResetSettings(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자 설정을 찾을 수 없습니다.");
            
            verify(userSettingsRepository).findByUserId(userId);
        }
    }

    @Test
    @DisplayName("API Key 검증 성공")
    void validateApiKey_Success() {
        // given
        String validApiKey = "valid-api-key-12345";
        ApiKeyValidationRequestDto request = new ApiKeyValidationRequestDto(validApiKey);
        NexonApiService.ApiKeyValidationResult apiResult = new NexonApiService.ApiKeyValidationResult(true, 15, null);
        given(nexonApiService.validateApiKey(validApiKey)).willReturn(apiResult);
        
        // when
        ApiKeyValidationResponseDto response = userService.validateApiKey(request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isTrue();
        assertThat(response.getCharacterCount()).isEqualTo(15);
        assertThat(response.getMessage()).isNull();
        
        verify(nexonApiService).validateApiKey(validApiKey);
    }

    @Test
    @DisplayName("API Key 검증 실패 - 빈 API Key")
    void validateApiKey_EmptyApiKey_ReturnsFailure() {
        // given
        String emptyApiKey = "";
        ApiKeyValidationRequestDto request = new ApiKeyValidationRequestDto(emptyApiKey);
        
        // when
        ApiKeyValidationResponseDto response = userService.validateApiKey(request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isFalse();
        assertThat(response.getCharacterCount()).isNull();
        assertThat(response.getMessage()).isEqualTo("API Key가 비어있습니다.");
    }

    @Test
    @DisplayName("API Key 검증 실패 - 공백으로만 구성된 API Key")
    void validateApiKey_WhitespaceApiKey_ReturnsFailure() {
        // given
        String whitespaceApiKey = "   ";
        ApiKeyValidationRequestDto request = new ApiKeyValidationRequestDto(whitespaceApiKey);
        
        // when
        ApiKeyValidationResponseDto response = userService.validateApiKey(request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isFalse();
        assertThat(response.getCharacterCount()).isNull();
        assertThat(response.getMessage()).isEqualTo("API Key가 비어있습니다.");
    }

    @Test
    @DisplayName("API Key 검증 실패 - null API Key")
    void validateApiKey_NullApiKey_ReturnsFailure() {
        // given
        ApiKeyValidationRequestDto request = new ApiKeyValidationRequestDto(null);
        
        // when
        ApiKeyValidationResponseDto response = userService.validateApiKey(request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isFalse();
        assertThat(response.getCharacterCount()).isNull();
        assertThat(response.getMessage()).isEqualTo("API Key가 비어있습니다.");
    }

    @Test
    @DisplayName("API Key 검증 실패 - 너무 짧은 API Key")
    void validateApiKey_ShortApiKey_ReturnsFailure() {
        // given
        String shortApiKey = "short";
        ApiKeyValidationRequestDto request = new ApiKeyValidationRequestDto(shortApiKey);
        NexonApiService.ApiKeyValidationResult apiResult = new NexonApiService.ApiKeyValidationResult(false, 0, "유효하지 않은 API Key입니다.");
        given(nexonApiService.validateApiKey(shortApiKey)).willReturn(apiResult);
        
        // when
        ApiKeyValidationResponseDto response = userService.validateApiKey(request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isFalse();
        assertThat(response.getCharacterCount()).isNull();
        assertThat(response.getMessage()).isEqualTo("유효하지 않은 API Key입니다.");
        
        verify(nexonApiService).validateApiKey(shortApiKey);
    }

    @Test
    @DisplayName("API Key 검증 실패 - 경계값 테스트 (9자리)")
    void validateApiKey_BoundaryLength_ReturnsFailure() {
        // given
        String boundaryApiKey = "123456789"; // 9자리 (10자리 미만)
        ApiKeyValidationRequestDto request = new ApiKeyValidationRequestDto(boundaryApiKey);
        NexonApiService.ApiKeyValidationResult apiResult = new NexonApiService.ApiKeyValidationResult(false, 0, "인증에 실패했습니다. API Key를 확인해주세요.");
        given(nexonApiService.validateApiKey(boundaryApiKey)).willReturn(apiResult);
        
        // when
        ApiKeyValidationResponseDto response = userService.validateApiKey(request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isFalse();
        assertThat(response.getCharacterCount()).isNull();
        assertThat(response.getMessage()).isEqualTo("인증에 실패했습니다. API Key를 확인해주세요.");
        
        verify(nexonApiService).validateApiKey(boundaryApiKey);
    }

    @Test
    @DisplayName("API Key 검증 성공 - 경계값 테스트 (10자리)")
    void validateApiKey_BoundaryLengthValid_ReturnsSuccess() {
        // given
        String boundaryApiKey = "1234567890"; // 10자리 (최소 길이)
        ApiKeyValidationRequestDto request = new ApiKeyValidationRequestDto(boundaryApiKey);
        NexonApiService.ApiKeyValidationResult apiResult = new NexonApiService.ApiKeyValidationResult(true, 12, null);
        given(nexonApiService.validateApiKey(boundaryApiKey)).willReturn(apiResult);
        
        // when
        ApiKeyValidationResponseDto response = userService.validateApiKey(request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isTrue();
        assertThat(response.getCharacterCount()).isEqualTo(12);
        assertThat(response.getMessage()).isNull();
        
        verify(nexonApiService).validateApiKey(boundaryApiKey);
    }

    // ==================== 비밀번호 변경 API 테스트 ====================

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword_Success() {
        // given
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            given(userRepository.findById(1L)).willReturn(Optional.of(savedUser));
            given(passwordEncoder.encode("newPassword123")).willReturn("encodedNewPassword");
            given(userRepository.save(any(User.class))).willReturn(savedUser);

            // when
            PasswordChangeResponseDto response = userService.changePassword(passwordChangeRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("비밀번호가 성공적으로 변경되었습니다.");

            verify(userRepository).findById(1L);
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(savedUser);
            verify(secureRefreshTokenService).invalidateAllTokens(1L);
        }
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 사용자를 찾을 수 없음")
    void changePassword_UserNotFound_ThrowsException() {
        // given
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            given(userRepository.findById(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.changePassword(passwordChangeRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");

            verify(userRepository).findById(1L);
        }
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 본캐명 불일치")
    void changePassword_MainCharacterNameMismatch_ThrowsException() {
        // given
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            given(userRepository.findById(1L)).willReturn(Optional.of(savedUser));

            PasswordChangeRequestDto mismatchRequest = new PasswordChangeRequestDto("differentCharacter", "newPassword123");

            // when & then
            assertThatThrownBy(() -> userService.changePassword(mismatchRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("본캐명이 일치하지 않습니다.");

            verify(userRepository).findById(1L);
        }
    }

    @Test
    @DisplayName("비밀번호 변경 - 비밀번호 암호화 확인")
    void changePassword_PasswordEncryption() {
        // given
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            given(userRepository.findById(1L)).willReturn(Optional.of(savedUser));
            given(passwordEncoder.encode("newPassword123")).willReturn("encodedNewPassword");
            given(userRepository.save(any(User.class))).willReturn(savedUser);

            // when
            userService.changePassword(passwordChangeRequest);

            // then - 새 비밀번호가 암호화되어 저장되는지 확인
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(savedUser);
        }
    }

    @Test
    @DisplayName("비밀번호 변경 - Refresh Token 무효화 확인")
    void changePassword_RefreshTokenInvalidation() {
        // given
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            given(userRepository.findById(1L)).willReturn(Optional.of(savedUser));
            given(passwordEncoder.encode("newPassword123")).willReturn("encodedNewPassword");
            given(userRepository.save(any(User.class))).willReturn(savedUser);

            // when
            userService.changePassword(passwordChangeRequest);

            // then - 보안상 해당 사용자의 모든 토큰이 무효화되어야 함
            verify(secureRefreshTokenService).invalidateAllTokens(1L);
        }
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 데이터베이스 저장 실패")
    void changePassword_DatabaseSaveError_ThrowsException() {
        // given
        try (var mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            given(userRepository.findById(1L)).willReturn(Optional.of(savedUser));
            given(passwordEncoder.encode("newPassword123")).willReturn("encodedNewPassword");
            given(userRepository.save(any(User.class))).willThrow(new RuntimeException("데이터베이스 연결 실패"));

            // when & then
            assertThatThrownBy(() -> userService.changePassword(passwordChangeRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 연결 실패");

            verify(userRepository).findById(1L);
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(savedUser);
        }
    }

    // 리플렉션을 사용하여 private 필드 설정하는 헬퍼 메서드
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
} 