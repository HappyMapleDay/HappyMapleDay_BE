package com.happymapleday.user.service;

import com.happymapleday.user.dto.AdminRoleUpdateRequestDto;
import com.happymapleday.user.dto.AdminRoleUpdateResponseDto;
import com.happymapleday.user.dto.AdminUserListResponseDto;
import com.happymapleday.user.dto.AdminVerificationResponseDto;
import com.happymapleday.user.dto.LoginRequestDto;
import com.happymapleday.user.dto.LoginResponseDto;
import com.happymapleday.user.entity.User;
import com.happymapleday.user.entity.UserRole;
import com.happymapleday.user.repository.UserRepository;
import com.happymapleday.user.repository.UserSettingsRepository;
import com.happymapleday.user.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceAdminTest {

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

    private User normalUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        normalUser = new User("normalUser", "password", "apiKey");
        normalUser.updateRole(UserRole.NORMAL);
        
        adminUser = new User("adminUser", "password", "apiKey");
        adminUser.updateRole(UserRole.ADMIN);
        
        // ID 설정을 위해 리플렉션 사용
        setUserId(normalUser, 1L);
        setUserId(adminUser, 2L);
    }

    private void setUserId(User user, Long id) {
        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            // 테스트용이므로 예외 무시
        }
    }

    @Test
    void testLoginWithAdminRole() {
        // given
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setMainCharacterName("adminUser");
        loginRequest.setPassword("password");

        when(userRepository.findByMainCharacterName("adminUser")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(jwtService.generateAccessToken(2L, "adminUser", UserRole.ADMIN)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(2L)).thenReturn("refresh-token");

        // when
        LoginResponseDto response = userService.login(loginRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUser().getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(response.getUser().isAdmin()).isTrue();

        verify(jwtService).generateAccessToken(2L, "adminUser", UserRole.ADMIN);
    }

    @Test
    void testLoginWithNormalRole() {
        // given
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setMainCharacterName("normalUser");
        loginRequest.setPassword("password");

        when(userRepository.findByMainCharacterName("normalUser")).thenReturn(Optional.of(normalUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(jwtService.generateAccessToken(1L, "normalUser", UserRole.NORMAL)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(1L)).thenReturn("refresh-token");

        // when
        LoginResponseDto response = userService.login(loginRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUser().getRole()).isEqualTo(UserRole.NORMAL);
        assertThat(response.getUser().isAdmin()).isFalse();

        verify(jwtService).generateAccessToken(1L, "normalUser", UserRole.NORMAL);
    }

    @Test
    void testGetNormalUsers() {
        // given
        List<User> normalUsers = Arrays.asList(normalUser);
        when(userRepository.findNormalUsersOrderByCreatedAtDesc()).thenReturn(normalUsers);

        // when
        AdminUserListResponseDto response = userService.getNormalUsers();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsers()).hasSize(1);
        assertThat(response.getUsers().get(0).getRole()).isEqualTo(UserRole.NORMAL);
        assertThat(response.getUsers().get(0).getMainCharacterName()).isEqualTo("normalUser");
        assertThat(response.getTotalCount()).isEqualTo(1);
    }

    @Test
    void testGetAdminUsers() {
        // given
        List<User> adminUsers = Arrays.asList(adminUser);
        when(userRepository.findAdminUsersOrderByCreatedAtDesc()).thenReturn(adminUsers);

        // when
        AdminUserListResponseDto response = userService.getAdminUsers();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsers()).hasSize(1);
        assertThat(response.getUsers().get(0).getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(response.getUsers().get(0).getMainCharacterName()).isEqualTo("adminUser");
        assertThat(response.getUsers().get(0).isAdmin()).isTrue();
    }

    @Test
    void testGetAllUsers() {
        // given
        List<User> allUsers = Arrays.asList(normalUser, adminUser);
        when(userRepository.findAllByOrderByCreatedAtDesc()).thenReturn(allUsers);

        // when
        AdminUserListResponseDto response = userService.getAllUsers();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsers()).hasSize(2);
        assertThat(response.getTotalCount()).isEqualTo(2);
    }

    @Test
    void testUpdateUserRoleToAdmin() {
        // given
        AdminRoleUpdateRequestDto request = new AdminRoleUpdateRequestDto(1L, UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));
        when(userRepository.save(any(User.class))).thenReturn(normalUser);

        // when
        AdminRoleUpdateResponseDto response = userService.updateUserRole(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(response.getMessage()).contains("어드민 권한이 부여되었습니다");
        
        verify(secureRefreshTokenService).invalidateAllTokens(1L);
        verify(userRepository).save(normalUser);
        assertThat(normalUser.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void testUpdateUserRoleToNormal() {
        // given
        AdminRoleUpdateRequestDto request = new AdminRoleUpdateRequestDto(2L, UserRole.NORMAL);
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        // when
        AdminRoleUpdateResponseDto response = userService.updateUserRole(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getRole()).isEqualTo(UserRole.NORMAL);
        assertThat(response.getMessage()).contains("일반 사용자 권한으로 변경되었습니다");
        
        verify(secureRefreshTokenService).invalidateAllTokens(2L);
        assertThat(adminUser.getRole()).isEqualTo(UserRole.NORMAL);
    }

    @Test
    void testUpdateUserRoleWithNonExistentUser() {
        // given
        AdminRoleUpdateRequestDto request = new AdminRoleUpdateRequestDto(999L, UserRole.ADMIN);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUserRole(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }

    @Test
    void testIsUserAdmin() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        // when & then
        assertThat(userService.isUserAdmin(1L)).isFalse();
        assertThat(userService.isUserAdmin(2L)).isTrue();
        assertThat(userService.isUserAdmin(999L)).isFalse(); // 존재하지 않는 사용자
    }

    @Test
    void testIsCurrentUserAdmin() {
        // given
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(2L);
            when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

            // when
            boolean isAdmin = userService.isCurrentUserAdmin();

            // then
            assertThat(isAdmin).isTrue();
        }
    }

    @Test
    void testIsCurrentUserAdminWithNormalUser() {
        // given
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));

            // when
            boolean isAdmin = userService.isCurrentUserAdmin();

            // then
            assertThat(isAdmin).isFalse();
        }
    }

    @Test
    void testIsCurrentUserAdminWithException() {
        // given
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUserId).thenThrow(new RuntimeException("No authentication"));

            // when
            boolean isAdmin = userService.isCurrentUserAdmin();

            // then
            assertThat(isAdmin).isFalse();
        }
    }

    @Test
    void testVerifyCurrentUserAdmin() {
        // given
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(2L);
            when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

            // when
            AdminVerificationResponseDto response = userService.verifyCurrentUserAdmin();

            // then
            assertThat(response).isNotNull();
            assertThat(response.isAdmin()).isTrue();
            assertThat(response.getAdminLevel()).isEqualTo("ADMIN");
            assertThat(response.getTokenValidUntil()).isGreaterThan(System.currentTimeMillis());
        }
    }

    @Test
    void testVerifyCurrentUserAdminWithNormalUser() {
        // given
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));

            // when
            AdminVerificationResponseDto response = userService.verifyCurrentUserAdmin();

            // then
            assertThat(response).isNotNull();
            assertThat(response.isAdmin()).isFalse();
            assertThat(response.getAdminLevel()).isNull();
            assertThat(response.getTokenValidUntil()).isEqualTo(0);
        }
    }

    @Test
    void testVerifyCurrentUserAdminWithException() {
        // given
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUserId).thenThrow(new RuntimeException("No authentication"));

            // when
            AdminVerificationResponseDto response = userService.verifyCurrentUserAdmin();

            // then
            assertThat(response).isNotNull();
            assertThat(response.isAdmin()).isFalse();
        }
    }
}
