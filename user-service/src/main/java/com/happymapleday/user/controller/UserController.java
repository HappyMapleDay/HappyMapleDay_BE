package com.happymapleday.user.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.user.dto.SignupRequestDto;
import com.happymapleday.user.dto.SignupResponseDto;
import com.happymapleday.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponseDto>> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
        try {
            SignupResponseDto response = userService.signup(signupRequest);
            return ResponseEntity.ok(ApiResponse.success(response));
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