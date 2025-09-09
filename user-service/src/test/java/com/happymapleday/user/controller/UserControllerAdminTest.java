package com.happymapleday.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.user.dto.AdminRoleUpdateRequestDto;
import com.happymapleday.user.dto.AdminRoleUpdateResponseDto;
import com.happymapleday.user.dto.AdminUserListResponseDto;
import com.happymapleday.user.dto.AdminVerificationResponseDto;
import com.happymapleday.user.entity.UserRole;
import com.happymapleday.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetNormalUsersSuccess() throws Exception {
        // given
        AdminUserListResponseDto.UserSummary userSummary = new AdminUserListResponseDto.UserSummary(
                1L, "normalUser", UserRole.NORMAL, LocalDateTime.now()
        );
        AdminUserListResponseDto response = AdminUserListResponseDto.of(Arrays.asList(userSummary));

        when(userService.isCurrentUserAdmin()).thenReturn(true);
        when(userService.getNormalUsers()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/user/admin/users/normal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.users").isArray())
                .andExpect(jsonPath("$.data.users[0].role").value("NORMAL"))
                .andExpect(jsonPath("$.data.users[0].mainCharacterName").value("normalUser"))
                .andExpect(jsonPath("$.data.totalCount").value(1));

        verify(userService).isCurrentUserAdmin();
        verify(userService).getNormalUsers();
    }

    @Test
    void testGetNormalUsersWithoutAdminPermission() throws Exception {
        // given
        when(userService.isCurrentUserAdmin()).thenReturn(false);

        // when & then
        mockMvc.perform(get("/api/user/admin/users/normal"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("어드민 권한이 필요합니다."));

        verify(userService).isCurrentUserAdmin();
        verify(userService, never()).getNormalUsers();
    }

    @Test
    void testGetAdminUsersSuccess() throws Exception {
        // given
        AdminUserListResponseDto.UserSummary adminSummary = new AdminUserListResponseDto.UserSummary(
                2L, "adminUser", UserRole.ADMIN, LocalDateTime.now()
        );
        AdminUserListResponseDto response = AdminUserListResponseDto.of(Arrays.asList(adminSummary));

        when(userService.isCurrentUserAdmin()).thenReturn(true);
        when(userService.getAdminUsers()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/user/admin/users/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.users[0].role").value("ADMIN"))
                .andExpect(jsonPath("$.data.users[0].mainCharacterName").value("adminUser"));

        verify(userService).getAdminUsers();
    }

    @Test
    void testGetAllUsersSuccess() throws Exception {
        // given
        AdminUserListResponseDto.UserSummary normalUser = new AdminUserListResponseDto.UserSummary(
                1L, "normalUser", UserRole.NORMAL, LocalDateTime.now()
        );
        AdminUserListResponseDto.UserSummary adminUser = new AdminUserListResponseDto.UserSummary(
                2L, "adminUser", UserRole.ADMIN, LocalDateTime.now()
        );
        AdminUserListResponseDto response = AdminUserListResponseDto.of(Arrays.asList(normalUser, adminUser));

        when(userService.isCurrentUserAdmin()).thenReturn(true);
        when(userService.getAllUsers()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/user/admin/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.users").isArray())
                .andExpect(jsonPath("$.data.users").value(org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.data.totalCount").value(2));

        verify(userService).getAllUsers();
    }

    @Test
    void testUpdateUserRoleSuccess() throws Exception {
        // given
        AdminRoleUpdateRequestDto request = new AdminRoleUpdateRequestDto(1L, UserRole.ADMIN);
        AdminRoleUpdateResponseDto response = AdminRoleUpdateResponseDto.success(1L, "normalUser", UserRole.ADMIN);

        when(userService.isCurrentUserAdmin()).thenReturn(true);
        when(userService.updateUserRole(any(AdminRoleUpdateRequestDto.class))).thenReturn(response);

        // when & then
        mockMvc.perform(put("/api/user/admin/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.mainCharacterName").value("normalUser"))
                .andExpect(jsonPath("$.data.message").value("어드민 권한이 부여되었습니다."));

        verify(userService).updateUserRole(any(AdminRoleUpdateRequestDto.class));
    }

    @Test
    void testUpdateUserRoleWithoutAdminPermission() throws Exception {
        // given
        AdminRoleUpdateRequestDto request = new AdminRoleUpdateRequestDto(1L, UserRole.ADMIN);

        when(userService.isCurrentUserAdmin()).thenReturn(false);

        // when & then
        mockMvc.perform(put("/api/user/admin/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("어드민 권한이 필요합니다."));

        verify(userService, never()).updateUserRole(any());
    }

    @Test
    void testUpdateUserRoleWithInvalidRequest() throws Exception {
        // given
        AdminRoleUpdateRequestDto request = new AdminRoleUpdateRequestDto(); // userId, role이 null

        when(userService.isCurrentUserAdmin()).thenReturn(true);

        // when & then
        mockMvc.perform(put("/api/user/admin/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUserRole(any());
    }

    @Test
    void testUpdateUserRoleWithNonExistentUser() throws Exception {
        // given
        AdminRoleUpdateRequestDto request = new AdminRoleUpdateRequestDto(999L, UserRole.ADMIN);

        when(userService.isCurrentUserAdmin()).thenReturn(true);
        when(userService.updateUserRole(any(AdminRoleUpdateRequestDto.class)))
                .thenThrow(new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // when & then
        mockMvc.perform(put("/api/user/admin/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."));
    }

    @Test
    void testVerifyAdminRoleSuccess() throws Exception {
        // given
        AdminVerificationResponseDto response = AdminVerificationResponseDto.admin("ADMIN", System.currentTimeMillis() + 3600000);

        when(userService.verifyCurrentUserAdmin()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/user/admin/verify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.admin").value(true))
                .andExpect(jsonPath("$.data.adminLevel").value("ADMIN"))
                .andExpect(jsonPath("$.data.tokenValidUntil").isNumber());

        verify(userService).verifyCurrentUserAdmin();
    }

    @Test
    void testVerifyAdminRoleForNormalUser() throws Exception {
        // given
        AdminVerificationResponseDto response = AdminVerificationResponseDto.notAdmin();

        when(userService.verifyCurrentUserAdmin()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/user/admin/verify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.admin").value(false))
                .andExpect(jsonPath("$.data.adminLevel").isEmpty())
                .andExpect(jsonPath("$.data.tokenValidUntil").value(0));
    }

    @Test
    void testVerifyAdminRoleSimpleSuccess() throws Exception {
        // given
        when(userService.isCurrentUserAdmin()).thenReturn(true);

        // when & then
        mockMvc.perform(get("/api/user/admin/verify/simple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));

        verify(userService).isCurrentUserAdmin();
    }

    @Test
    void testVerifyAdminRoleSimpleForNormalUser() throws Exception {
        // given
        when(userService.isCurrentUserAdmin()).thenReturn(false);

        // when & then
        mockMvc.perform(get("/api/user/admin/verify/simple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    void testGetNormalUsersServiceException() throws Exception {
        // given
        when(userService.isCurrentUserAdmin()).thenReturn(true);
        when(userService.getNormalUsers()).thenThrow(new RuntimeException("Database error"));

        // when & then
        mockMvc.perform(get("/api/user/admin/users/normal"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("일반 유저 목록 조회 중 오류가 발생했습니다."));
    }

    @Test
    void testUpdateUserRoleServiceException() throws Exception {
        // given
        AdminRoleUpdateRequestDto request = new AdminRoleUpdateRequestDto(1L, UserRole.ADMIN);

        when(userService.isCurrentUserAdmin()).thenReturn(true);
        when(userService.updateUserRole(any(AdminRoleUpdateRequestDto.class)))
                .thenThrow(new RuntimeException("Database error"));

        // when & then
        mockMvc.perform(put("/api/user/admin/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("사용자 권한 변경 중 오류가 발생했습니다."));
    }

    @Test
    void testEmptyUserListResponse() throws Exception {
        // given
        AdminUserListResponseDto emptyResponse = AdminUserListResponseDto.of(Collections.emptyList());

        when(userService.isCurrentUserAdmin()).thenReturn(true);
        when(userService.getNormalUsers()).thenReturn(emptyResponse);

        // when & then
        mockMvc.perform(get("/api/user/admin/users/normal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.users").isArray())
                .andExpect(jsonPath("$.data.users").isEmpty())
                .andExpect(jsonPath("$.data.totalCount").value(0));
    }
}
