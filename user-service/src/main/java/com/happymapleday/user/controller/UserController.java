package com.happymapleday.user.controller;

import com.happymapleday.common.dto.ApiResponse;
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
import com.happymapleday.user.dto.AdminUserListResponseDto;
import com.happymapleday.user.dto.AdminRoleUpdateRequestDto;
import com.happymapleday.user.dto.AdminRoleUpdateResponseDto;
import com.happymapleday.user.dto.UserMetricsResponseDto;
import com.happymapleday.user.dto.AdminVerificationResponseDto;
import com.happymapleday.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            LoginResponseDto response = userService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("아이디 또는 비밀번호가 잘못되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("로그인 처리 중 오류가 발생했습니다."));
        }
    }
    
    // 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDto>> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        try {
            RefreshTokenResponseDto response = userService.refreshToken(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("토큰 갱신 처리 중 오류가 발생했습니다."));
        }
    }
    
    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponseDto>> logout(@Valid @RequestBody LogoutRequestDto logoutRequest) {
        try {
            LogoutResponseDto response = userService.logout(logoutRequest);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("로그아웃 처리 중 오류가 발생했습니다."));
        }
    }
    
    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<SignupResponseDto>> register(@Valid @RequestBody SignupRequestDto signupRequest) {
        try {
            // 비밀번호 확인 검증
            if (!signupRequest.getPassword().equals(signupRequest.getPasswordConfirm())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("비밀번호가 일치하지 않습니다."));
            }
            
            SignupResponseDto response = userService.signup(signupRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("회원가입 처리 중 오류가 발생했습니다."));
        }
    }
    
    // 메인 캐릭터명 중복 체크
    @GetMapping("/check-username/{mainCharacterName}")
    public ResponseEntity<ApiResponse<Boolean>> checkMainCharacterName(@PathVariable String mainCharacterName) {
        try {
            boolean exists = userService.isMainCharacterNameExists(mainCharacterName);
            return ResponseEntity.ok(ApiResponse.success(!exists)); // 사용 가능하면 true
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("중복 체크 처리 중 오류가 발생했습니다."));
        }
    }
    
    // Nexon API Key 검증
    @PostMapping("/validate-api-key")
    public ResponseEntity<ApiResponse<ApiKeyValidationResponseDto>> validateApiKey(@Valid @RequestBody ApiKeyValidationRequestDto request) {
        try {
            ApiKeyValidationResponseDto response = userService.validateApiKey(request);
            
            if (response.isValid()) {
                return ResponseEntity.ok(ApiResponse.success(response));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.success(response));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("API Key 검증 처리 중 오류가 발생했습니다."));
        }
    }
    
    // 비밀번호 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<PasswordResetResponseDto>> resetPassword(@Valid @RequestBody PasswordResetRequestDto request) {
        try {
            PasswordResetResponseDto response = userService.resetPassword(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("비밀번호 재설정 처리 중 오류가 발생했습니다."));
        }
    }
    
    // 본캐 변경
    @PutMapping("/main-character")
    public ResponseEntity<ApiResponse<MainCharacterUpdateResponseDto>> updateMainCharacter(@Valid @RequestBody MainCharacterUpdateRequestDto request) {
        try {
            MainCharacterUpdateResponseDto response = userService.updateMainCharacter(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("본캐 변경 처리 중 오류가 발생했습니다."));
        }
    }
    
    // 사용자 설정 조회
    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<UserSettingsResponseDto>> getUserSettings() {
        try {
            UserSettingsResponseDto response = userService.getUserSettings();
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("사용자 설정 조회 중 오류가 발생했습니다."));
        }
    }
    
    // 개인정보 수집 동의 설정 수정
    @PutMapping("/settings/privacy")
    public ResponseEntity<ApiResponse<UserSettingsResponseDto>> updatePrivacySettings(@Valid @RequestBody PrivacySettingsUpdateRequestDto request) {
        try {
            UserSettingsResponseDto response = userService.updatePrivacySettings(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("개인정보 설정 수정 중 오류가 발생했습니다."));
        }
    }
    
    // 비밀번호 변경
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<PasswordChangeResponseDto>> changePassword(@Valid @RequestBody PasswordChangeRequestDto request) {
        try {
            PasswordChangeResponseDto response = userService.changePassword(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("비밀번호 변경 처리 중 오류가 발생했습니다."));
        }
    }
    
    // 주간 초기화 설정 수정
    @PutMapping("/settings/weekly-reset")
    public ResponseEntity<ApiResponse<UserSettingsResponseDto>> updateWeeklyResetSettings(@Valid @RequestBody WeeklyResetSettingsUpdateRequestDto request) {
        try {
            UserSettingsResponseDto response = userService.updateWeeklyResetSettings(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("주간 초기화 설정 수정 중 오류가 발생했습니다."));
        }
    }
    
    // 일반 권한 유저 목록 조회
    @GetMapping("/admin/users/normal")
    public ResponseEntity<ApiResponse<AdminUserListResponseDto>> getNormalUsers() {
        try {
            // 어드민 권한 확인
            if (!userService.isCurrentUserAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("어드민 권한이 필요합니다."));
            }
            
            AdminUserListResponseDto response = userService.getNormalUsers();
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("일반 유저 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    // 어드민 권한 유저 목록 조회
    @GetMapping("/admin/users/admin")
    public ResponseEntity<ApiResponse<AdminUserListResponseDto>> getAdminUsers() {
        try {
            // 어드민 권한 확인
            if (!userService.isCurrentUserAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("어드민 권한이 필요합니다."));
            }
            
            AdminUserListResponseDto response = userService.getAdminUsers();
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("어드민 유저 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    // 모든 유저 목록 조회
    @GetMapping("/admin/users/all")
    public ResponseEntity<ApiResponse<AdminUserListResponseDto>> getAllUsers() {
        try {
            // 어드민 권한 확인
            if (!userService.isCurrentUserAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("어드민 권한이 필요합니다."));
            }
            
            AdminUserListResponseDto response = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("전체 유저 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    // 사용자 권한 변경
    @PutMapping("/admin/role")
    public ResponseEntity<ApiResponse<AdminRoleUpdateResponseDto>> updateUserRole(@Valid @RequestBody AdminRoleUpdateRequestDto request) {
        try {
            // 어드민 권한 확인
            if (!userService.isCurrentUserAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("어드민 권한이 필요합니다."));
            }
            
            AdminRoleUpdateResponseDto response = userService.updateUserRole(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("사용자 권한 변경 중 오류가 발생했습니다."));
        }
    }
    
    // 강력한 어드민 권한 검증 (실시간 DB 조회)
    @GetMapping("/admin/verify")
    public ResponseEntity<ApiResponse<AdminVerificationResponseDto>> verifyAdminRole() {
        try {
            AdminVerificationResponseDto response = userService.verifyCurrentUserAdmin();
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("어드민 권한 확인 중 오류가 발생했습니다."));
        }
    }
    
    // 기존 호환성을 위한 간단한 검증
    @GetMapping("/admin/verify/simple")
    public ResponseEntity<ApiResponse<Boolean>> verifyAdminRoleSimple() {
        try {
            boolean isAdmin = userService.isCurrentUserAdmin();
            return ResponseEntity.ok(ApiResponse.success(isAdmin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("어드민 권한 확인 중 오류가 발생했습니다."));
        }
    }
    
    // 유저 수 집계 (가입자 현황 추이)
    @GetMapping("/admin/metrics/users")
    public ResponseEntity<ApiResponse<UserMetricsResponseDto>> getUserMetrics(
            @RequestParam(defaultValue = "1d") String period,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        try {
            // 어드민 권한 확인
            if (!userService.isCurrentUserAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("어드민 권한이 필요합니다."));
            }
            
            UserMetricsResponseDto response = userService.getUserMetrics(period, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("유저 수 집계 조회 중 오류가 발생했습니다."));
        }
    }
    
    // 일반 유저 수 집계 (어드민 제외)
    @GetMapping("/admin/metrics/normal-users")
    public ResponseEntity<ApiResponse<UserMetricsResponseDto>> getNormalUserMetrics(
            @RequestParam(defaultValue = "1d") String period,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        try {
            // 어드민 권한 확인
            if (!userService.isCurrentUserAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("어드민 권한이 필요합니다."));
            }
            
            UserMetricsResponseDto response = userService.getNormalUserMetrics(period, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("일반 유저 수 집계 조회 중 오류가 발생했습니다."));
        }
    }
} 