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
import com.happymapleday.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

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
} 