package com.happymapleday.user.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @InjectMocks
    private UserService userService;

    private SignupRequestDto signupRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequestDto(
            "testCharacter",
            "password123",
            "test-api-key",
            true
        );

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
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getMainCharacterName()).isEqualTo("testCharacter");
        assertThat(response.getMessage()).isEqualTo("회원가입이 성공적으로 완료되었습니다.");

        verify(userRepository).existsByMainCharacterName("testCharacter");
        verify(passwordEncoder).encode("password123");
        verify(encryptionService).encrypt("test-api-key");
        verify(userRepository).save(any(User.class));
        verify(userSettingsRepository).save(any(UserSettings.class));
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