package com.happymapleday.user.service;

import com.happymapleday.user.dto.LoginRequestDto;
import com.happymapleday.user.dto.LoginResponseDto;
import com.happymapleday.user.dto.RefreshTokenRequestDto;
import com.happymapleday.user.dto.RefreshTokenResponseDto;
import com.happymapleday.user.dto.SignupRequestDto;
import com.happymapleday.user.dto.SignupResponseDto;
import com.happymapleday.user.entity.User;
import com.happymapleday.user.entity.UserSettings;
import com.happymapleday.user.repository.UserRepository;
import com.happymapleday.user.repository.UserSettingsRepository;
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

    @InjectMocks
    private UserService userService;

    private SignupRequestDto signupRequest;
    private LoginRequestDto loginRequest;
    private RefreshTokenRequestDto refreshTokenRequest;
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