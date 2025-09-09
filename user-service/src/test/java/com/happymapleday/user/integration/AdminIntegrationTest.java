package com.happymapleday.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.user.dto.AdminRoleUpdateRequestDto;
import com.happymapleday.user.dto.LoginRequestDto;
import com.happymapleday.user.entity.User;
import com.happymapleday.user.entity.UserRole;
import com.happymapleday.user.repository.UserRepository;
import com.happymapleday.user.service.EncryptionService;
import com.happymapleday.user.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=myTestSecretKeyForIntegrationTesting123456789"
})
@Transactional
class AdminIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User normalUser;
    private User adminUser;
    private String adminToken;
    private String normalToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 테스트 데이터 생성
        createTestUsers();
        generateTokens();
    }

    private void createTestUsers() {
        // 일반 사용자 생성
        normalUser = new User(
                "normalUser",
                passwordEncoder.encode("password123"),
                encryptionService.encrypt("normalApiKey")
        );
        normalUser.updateRole(UserRole.NORMAL);
        normalUser = userRepository.save(normalUser);

        // 어드민 사용자 생성
        adminUser = new User(
                "adminUser",
                passwordEncoder.encode("password123"),
                encryptionService.encrypt("adminApiKey")
        );
        adminUser.updateRole(UserRole.ADMIN);
        adminUser = userRepository.save(adminUser);
    }

    private void generateTokens() {
        adminToken = jwtService.generateAccessToken(adminUser.getId(), adminUser.getMainCharacterName(), UserRole.ADMIN);
        normalToken = jwtService.generateAccessToken(normalUser.getId(), normalUser.getMainCharacterName(), UserRole.NORMAL);
    }

    @Test
    void testFullAdminWorkflow() throws Exception {
        // 1. 어드민으로 로그인
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setMainCharacterName("adminUser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.user.mainCharacterName").value("adminUser"));

        // 2. 어드민 권한으로 일반 유저 목록 조회
        mockMvc.perform(get("/api/user/admin/users/normal")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.users").isArray())
                .andExpect(jsonPath("$.data.totalCount").value(1));

        // 3. 어드민 권한으로 모든 유저 목록 조회
        mockMvc.perform(get("/api/user/admin/users/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(2));

        // 4. 일반 유저를 어드민으로 승격
        AdminRoleUpdateRequestDto updateRequest = new AdminRoleUpdateRequestDto(normalUser.getId(), UserRole.ADMIN);
        
        mockMvc.perform(put("/api/user/admin/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));

        // 5. 어드민 유저 목록 조회 (2명이 되어야 함)
        mockMvc.perform(get("/api/user/admin/users/admin")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(2));
    }

    @Test
    void testNormalUserCannotAccessAdminEndpoints() throws Exception {
        // 일반 유저 토큰으로 어드민 API 접근 시도
        mockMvc.perform(get("/api/user/admin/users/all")
                        .header("Authorization", "Bearer " + normalToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("어드민 권한이 필요합니다."));

        // 권한 변경 시도
        AdminRoleUpdateRequestDto updateRequest = new AdminRoleUpdateRequestDto(normalUser.getId(), UserRole.ADMIN);
        
        mockMvc.perform(put("/api/user/admin/role")
                        .header("Authorization", "Bearer " + normalToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testAdminVerificationEndpoint() throws Exception {
        // 어드민 사용자 검증
        mockMvc.perform(get("/api/user/admin/verify")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.admin").value(true))
                .andExpect(jsonPath("$.data.adminLevel").value("ADMIN"));

        // 일반 사용자 검증
        mockMvc.perform(get("/api/user/admin/verify")
                        .header("Authorization", "Bearer " + normalToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.admin").value(false));
    }

    @Test
    void testJwtTokenContainsRoleInformation() throws Exception {
        // JWT 토큰에서 role 정보 추출 테스트
        UserRole adminRole = jwtService.getRoleFromToken(adminToken);
        UserRole normalRole = jwtService.getRoleFromToken(normalToken);

        assert adminRole == UserRole.ADMIN;
        assert normalRole == UserRole.NORMAL;
        assert jwtService.isAdminFromToken(adminToken);
        assert !jwtService.isAdminFromToken(normalToken);
    }

    @Test
    void testRoleDowngrade() throws Exception {
        // 어드민을 일반 사용자로 강등
        AdminRoleUpdateRequestDto downgradeRequest = new AdminRoleUpdateRequestDto(adminUser.getId(), UserRole.NORMAL);
        
        mockMvc.perform(put("/api/user/admin/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(downgradeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("NORMAL"))
                .andExpect(jsonPath("$.data.message").value("일반 사용자 권한으로 변경되었습니다."));

        // 강등 후 어드민 API 접근 불가 확인 (새로운 토큰 필요)
        User updatedAdminUser = userRepository.findById(adminUser.getId()).orElseThrow();
        String newToken = jwtService.generateAccessToken(updatedAdminUser.getId(), updatedAdminUser.getMainCharacterName(), updatedAdminUser.getRole());
        
        mockMvc.perform(get("/api/user/admin/users/all")
                        .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testInvalidTokenAccess() throws Exception {
        // 잘못된 토큰으로 접근
        mockMvc.perform(get("/api/user/admin/users/all")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());

        // 토큰 없이 접근
        mockMvc.perform(get("/api/user/admin/users/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testNormalUserLogin() throws Exception {
        // 일반 사용자 로그인
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setMainCharacterName("normalUser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.role").value("NORMAL"))
                .andExpect(jsonPath("$.data.user.mainCharacterName").value("normalUser"));
    }

    @Test
    void testUpdateNonExistentUser() throws Exception {
        // 존재하지 않는 사용자 권한 변경 시도
        AdminRoleUpdateRequestDto updateRequest = new AdminRoleUpdateRequestDto(999L, UserRole.ADMIN);
        
        mockMvc.perform(put("/api/user/admin/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."));
    }
}
