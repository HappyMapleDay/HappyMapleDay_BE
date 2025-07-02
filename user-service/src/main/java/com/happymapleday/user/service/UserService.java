package com.happymapleday.user.service;

import com.happymapleday.user.dto.LoginRequestDto;
import com.happymapleday.user.dto.LoginResponseDto;
import com.happymapleday.user.dto.SignupRequestDto;
import com.happymapleday.user.dto.SignupResponseDto;
import com.happymapleday.user.entity.User;
import com.happymapleday.user.entity.UserSettings;
import com.happymapleday.user.repository.UserRepository;
import com.happymapleday.user.repository.UserSettingsRepository;
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
    
    @Autowired
    public UserService(UserRepository userRepository, 
                       UserSettingsRepository userSettingsRepository, 
                       PasswordEncoder passwordEncoder,
                       EncryptionService encryptionService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
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
        
        // JWT 토큰 생성
        String token = jwtService.generateToken(user.getId(), user.getMainCharacterName());
        
        // 응답 DTO 생성
        return LoginResponseDto.of(token, user.getId(), user.getMainCharacterName());
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
} 