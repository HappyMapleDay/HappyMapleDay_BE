package com.happymapleday.user.service;

import com.happymapleday.user.dto.LoginRequestDto;
import com.happymapleday.user.dto.LoginResponseDto;
import com.happymapleday.user.dto.LogoutRequestDto;
import com.happymapleday.user.dto.LogoutResponseDto;
import com.happymapleday.user.dto.RefreshTokenRequestDto;
import com.happymapleday.user.dto.RefreshTokenResponseDto;
import com.happymapleday.user.dto.SignupRequestDto;
import com.happymapleday.user.dto.SignupResponseDto;
import com.happymapleday.user.entity.User;
import com.happymapleday.user.entity.UserSettings;
import com.happymapleday.user.repository.UserRepository;
import com.happymapleday.user.repository.UserSettingsRepository;
import com.happymapleday.user.util.SecurityUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;
    private final JwtService jwtService;
    private final SecureRefreshTokenService secureRefreshTokenService;
    
    @Autowired
    public UserService(UserRepository userRepository, 
                       UserSettingsRepository userSettingsRepository, 
                       PasswordEncoder passwordEncoder,
                       EncryptionService encryptionService,
                       JwtService jwtService,
                       SecureRefreshTokenService secureRefreshTokenService) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.secureRefreshTokenService = secureRefreshTokenService;
    }
    
    // 로그인 처리
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        // 사용자 조회
        User user = userRepository.findByMainCharacterName(loginRequest.getMainCharacterName())
                .orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호가 잘못되었습니다."));
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 잘못되었습니다.");
        }
        
        // Access Token과 Refresh Token 생성
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getMainCharacterName());
        String refreshToken = jwtService.generateRefreshToken(user.getId());
        
        // 응답 DTO 생성
        return LoginResponseDto.of(accessToken, refreshToken, user.getId(), user.getMainCharacterName());
    }
    
    // 토큰 갱신 처리
    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto request) {
        String refreshToken = request.getRefreshToken();
        
        try {
            // Refresh Token 유효성 검증
            if (!jwtService.isTokenValid(refreshToken)) {
                throw new BadCredentialsException("유효하지 않은 Refresh Token입니다.");
            }
            
            // Refresh Token인지 확인
            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new BadCredentialsException("올바른 Refresh Token이 아닙니다.");
            }
            
            // 사용자 ID 추출
            Long userId = jwtService.getUserIdFromToken(refreshToken);
            
            // 사용자 정보 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadCredentialsException("사용자를 찾을 수 없습니다."));
            
            // 새로운 Access Token과 Refresh Token 생성
            String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getMainCharacterName());
            String newRefreshToken = jwtService.generateRefreshToken(user.getId());
            
            return RefreshTokenResponseDto.of(newAccessToken, newRefreshToken);
        } catch (JwtException e) {
            throw new BadCredentialsException("토큰 갱신에 실패했습니다.");
        }
    }
    
    // 회원가입 처리
    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequest) {
        // 중복 사용자 체크
        if (userRepository.existsByMainCharacterName(signupRequest.getMainCharacterName())) {
            throw new IllegalArgumentException("이미 존재하는 메인 캐릭터명입니다.");
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        
        // API 키 암호화
        String encryptedApiKey = encryptionService.encrypt(signupRequest.getNexonApiKey());
        
        // 사용자 생성
        User user = new User(
            signupRequest.getMainCharacterName(),
            encodedPassword,
            encryptedApiKey
        );
        
        User savedUser = userRepository.save(user);
        
        // 사용자 설정 생성
        UserSettings userSettings = new UserSettings(
            savedUser.getId(),
            signupRequest.getDataCollectionAgreed()
        );
        
        userSettingsRepository.save(userSettings);
        
        return SignupResponseDto.success();
    }
    
    // 메인 캐릭터명 중복 체크
    public boolean isMainCharacterNameExists(String mainCharacterName) {
        return userRepository.existsByMainCharacterName(mainCharacterName);
    }
    
    // 로그아웃 처리
    @Transactional
    public LogoutResponseDto logout(LogoutRequestDto logoutRequest) {
        try {
            // 현재 인증된 사용자 ID 가져오기
            Long currentUserId = SecurityUtil.getCurrentUserId();
            
            // Refresh Token 검증
            String refreshToken = logoutRequest.getRefreshToken();
            if (!jwtService.isTokenValid(refreshToken)) {
                throw new BadCredentialsException("유효하지 않은 Refresh Token입니다.");
            }
            
            // 토큰에서 사용자 ID 추출하여 현재 사용자와 일치 확인
            Long tokenUserId = jwtService.getUserIdFromToken(refreshToken);
            if (!currentUserId.equals(tokenUserId)) {
                throw new BadCredentialsException("토큰의 사용자 정보가 일치하지 않습니다.");
            }
            
            // 해당 사용자의 모든 Refresh Token 무효화
            secureRefreshTokenService.invalidateAllTokens(currentUserId);
            
            return LogoutResponseDto.success();
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("로그아웃 처리 중 오류가 발생했습니다.", e);
        }
    }
} 