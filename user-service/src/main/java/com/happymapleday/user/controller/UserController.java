package com.happymapleday.user.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.user.dto.LoginRequestDto;
import com.happymapleday.user.dto.LoginResponseDto;
import com.happymapleday.user.dto.SignupRequestDto;
import com.happymapleday.user.dto.SignupResponseDto;
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
} 