package com.happymapleday.user.service;

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
import com.happymapleday.user.entity.User;
import com.happymapleday.user.entity.UserRole;
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

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;
    private final JwtService jwtService;
    private final SecureRefreshTokenService secureRefreshTokenService;
    private final NexonApiService nexonApiService;
    
    @Autowired
    public UserService(UserRepository userRepository, 
                       UserSettingsRepository userSettingsRepository, 
                       PasswordEncoder passwordEncoder,
                       EncryptionService encryptionService,
                       JwtService jwtService,
                       SecureRefreshTokenService secureRefreshTokenService,
                       NexonApiService nexonApiService) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.secureRefreshTokenService = secureRefreshTokenService;
        this.nexonApiService = nexonApiService;
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
        
        // Access Token과 Refresh Token 생성 (role 정보 포함)
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getMainCharacterName(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId());
        
        // 응답 DTO 생성 (role 정보 포함)
        return LoginResponseDto.of(accessToken, refreshToken, user.getId(), user.getMainCharacterName(), user.getRole());
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
            
            // 새로운 Access Token과 Refresh Token 생성 (role 정보 포함)
            String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getMainCharacterName(), user.getRole());
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
    
    // 비밀번호 재설정 처리
    @Transactional
    public PasswordResetResponseDto resetPassword(PasswordResetRequestDto request) {
        // 사용자 조회
        User user = userRepository.findByMainCharacterName(request.getMainCharacterName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 넥슨 API Key 검증
        NexonApiService.ApiKeyValidationResult apiResult = nexonApiService.validateApiKey(request.getNexonApiKey());
        if (!apiResult.isValid()) {
            throw new IllegalArgumentException("유효하지 않은 Nexon API Key입니다.");
        }
        
        // 사용자의 암호화된 API Key와 비교 검증 (추가 보안)
        try {
            String decryptedApiKey = encryptionService.decrypt(user.getNexonApiKey());
            if (!decryptedApiKey.equals(request.getNexonApiKey())) {
                throw new IllegalArgumentException("일치하는 사용자 정보를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("일치하는 사용자 정보를 찾을 수 없습니다.");
        }
                
        // 임시 비밀번호 생성
        String temporaryPassword = generateTemporaryPassword();
        
        // 비밀번호 암호화 후 업데이트
        String encodedPassword = passwordEncoder.encode(temporaryPassword);
        user.updatePassword(encodedPassword);
        
        userRepository.save(user);
        
        // 해당 사용자의 모든 Refresh Token 무효화 (보안상 로그아웃 처리)
        secureRefreshTokenService.invalidateAllTokens(user.getId());
        
        return PasswordResetResponseDto.success(temporaryPassword);
    }
    
    // 임시 비밀번호 생성 (8자리, 영문 대소문자 + 숫자 + 특수문자)
    private String generateTemporaryPassword() {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*";
        String allChars = upperCase + lowerCase + digits + specialChars;
        
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(8);
        
        // 각 문자 종류에서 최소 1개씩 포함
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // 나머지 4자리는 모든 문자에서 랜덤 선택
        for (int i = 0; i < 4; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // 문자열 섞기
        for (int i = password.length() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(j));
            password.setCharAt(j, temp);
        }
        
        return password.toString();
    }
    
    // 본캐 변경 처리
    @Transactional
    public MainCharacterUpdateResponseDto updateMainCharacter(MainCharacterUpdateRequestDto request) {
        // 현재 인증된 사용자 ID 가져오기
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 사용자 조회
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        String previousName = user.getMainCharacterName();
        String newName = request.getNewMainCharacterName();
        
        // 중복 체크 (자신의 현재 이름과 같으면 허용)
        if (!previousName.equals(newName) && userRepository.existsByMainCharacterName(newName)) {
            throw new IllegalArgumentException("이미 사용 중인 본캐명입니다.");
        }
        
        // 본캐명 업데이트
        user.updateMainCharacterName(newName);
        userRepository.save(user);
        
        return MainCharacterUpdateResponseDto.of(previousName, newName);
    }
    
    // 사용자 설정 조회
    public UserSettingsResponseDto getUserSettings() {
        // 현재 인증된 사용자 ID 가져오기
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 사용자 조회
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 사용자 설정 조회
        UserSettings userSettings = userSettingsRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 설정을 찾을 수 없습니다."));
        
        return UserSettingsResponseDto.from(
            user.getMainCharacterName(),
            userSettings.getDataCollectionAgreed(),
            userSettings.getWeeklyResetEnabled()
        );
    }
    
    // 개인정보 수집 동의 설정 수정
    @Transactional
    public UserSettingsResponseDto updatePrivacySettings(PrivacySettingsUpdateRequestDto request) {
        // 현재 인증된 사용자 ID 가져오기
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 사용자 설정 조회
        UserSettings userSettings = userSettingsRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 설정을 찾을 수 없습니다."));
        
        // 개인정보 설정 업데이트
        userSettings.updateDataCollectionAgreed(request.getDataCollectionAgreed());
        
        userSettingsRepository.save(userSettings);
        
        // 업데이트된 설정 반환
        return getUserSettings();
    }
    
    // 주간 초기화 설정 수정
    @Transactional
    public UserSettingsResponseDto updateWeeklyResetSettings(WeeklyResetSettingsUpdateRequestDto request) {
        // 현재 인증된 사용자 ID 가져오기
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 사용자 설정 조회
        UserSettings userSettings = userSettingsRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 설정을 찾을 수 없습니다."));
        
        // 주간 초기화 설정 업데이트
        userSettings.updateWeeklyResetEnabled(request.getWeeklyResetEnabled());
        
        userSettingsRepository.save(userSettings);
        
        // 업데이트된 설정 반환
        return getUserSettings();
    }
    
    // 비밀번호 변경 처리
    @Transactional
    public PasswordChangeResponseDto changePassword(PasswordChangeRequestDto request) {
        // 현재 인증된 사용자 ID 가져오기
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 사용자 조회
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 요청의 mainCharacterName과 현재 사용자의 mainCharacterName 일치 확인
        if (!user.getMainCharacterName().equals(request.getMainCharacterName())) {
            throw new IllegalArgumentException("본캐명이 일치하지 않습니다.");
        }
        
        // 새 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        
        // 비밀번호 업데이트
        user.updatePassword(encodedPassword);
        userRepository.save(user);
        
        // 보안상 모든 Refresh Token 무효화
        secureRefreshTokenService.invalidateAllTokens(currentUserId);
        
        return PasswordChangeResponseDto.success();
    }
    
    // Nexon API Key 검증 처리
    public ApiKeyValidationResponseDto validateApiKey(ApiKeyValidationRequestDto request) {
        try {
            String apiKey = request.getNexonApiKey();
            
            // API Key 기본 검증 (길이, 형식 등)
            if (apiKey == null || apiKey.trim().isEmpty()) {
                return ApiKeyValidationResponseDto.failure("API Key가 비어있습니다.");
            }
            
            // 실제 Nexon API 호출을 통한 유효성 검증
            NexonApiService.ApiKeyValidationResult result = nexonApiService.validateApiKey(apiKey);
            
            if (result.isValid()) {
                return ApiKeyValidationResponseDto.success(result.getCharacterCount());
            } else {
                return ApiKeyValidationResponseDto.failure(result.getErrorMessage());
            }
            
        } catch (Exception e) {
            return ApiKeyValidationResponseDto.failure("API Key 검증 처리 중 오류가 발생했습니다.");
        }
    }
    
    // ========== 어드민 관련 메서드들 ==========
    
    // 일반 권한 유저 목록 조회
    public AdminUserListResponseDto getNormalUsers() {
        List<User> normalUsers = userRepository.findNormalUsersOrderByCreatedAtDesc();
        
        List<AdminUserListResponseDto.UserSummary> userSummaries = normalUsers.stream()
                .map(user -> new AdminUserListResponseDto.UserSummary(
                        user.getId(),
                        user.getMainCharacterName(),
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());
        
        return AdminUserListResponseDto.of(userSummaries);
    }
    
    // 어드민 권한 유저 목록 조회
    public AdminUserListResponseDto getAdminUsers() {
        List<User> adminUsers = userRepository.findAdminUsersOrderByCreatedAtDesc();
        
        List<AdminUserListResponseDto.UserSummary> userSummaries = adminUsers.stream()
                .map(user -> new AdminUserListResponseDto.UserSummary(
                        user.getId(),
                        user.getMainCharacterName(),
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());
        
        return AdminUserListResponseDto.of(userSummaries);
    }
    
    // 모든 유저 목록 조회
    public AdminUserListResponseDto getAllUsers() {
        List<User> allUsers = userRepository.findAllByOrderByCreatedAtDesc();
        
        List<AdminUserListResponseDto.UserSummary> userSummaries = allUsers.stream()
                .map(user -> new AdminUserListResponseDto.UserSummary(
                        user.getId(),
                        user.getMainCharacterName(),
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());
        
        return AdminUserListResponseDto.of(userSummaries);
    }
    
    // 사용자 권한 변경
    @Transactional
    public AdminRoleUpdateResponseDto updateUserRole(AdminRoleUpdateRequestDto request) {
        // 사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 권한 업데이트
        user.updateRole(request.getRole());
        userRepository.save(user);
        
        // 보안상 해당 사용자의 모든 토큰 무효화 (권한 변경 시)
        secureRefreshTokenService.invalidateAllTokens(user.getId());
        
        return AdminRoleUpdateResponseDto.success(
                user.getId(),
                user.getMainCharacterName(),
                user.getRole()
        );
    }
    
    // 어드민 권한 검증
    public boolean isUserAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(User::isAdmin)
                .orElse(false);
    }
    
    // 현재 사용자가 어드민인지 확인
    public boolean isCurrentUserAdmin() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            return isUserAdmin(currentUserId);
        } catch (Exception e) {
            return false;
        }
    }
    
    // 강화된 어드민 권한 검증 (실시간 DB 조회 + 추가 보안 정보)
    public AdminVerificationResponseDto verifyCurrentUserAdmin() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            
            // 실시간 DB 조회로 권한 확인
            User user = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            if (user.isAdmin()) {
                // 토큰 만료 시간 계산 (예: 1시간 후)
                long tokenValidUntil = System.currentTimeMillis() + (60 * 60 * 1000);
                
                // 어드민 레벨 결정 (향후 확장 가능)
                String adminLevel = "ADMIN"; // 기본값
                
                return AdminVerificationResponseDto.admin(adminLevel, tokenValidUntil);
            } else {
                return AdminVerificationResponseDto.notAdmin();
            }
            
        } catch (Exception e) {
            return AdminVerificationResponseDto.notAdmin();
        }
    }
    
    // 유저 수 집계 (가입자 현황 추이)
    public UserMetricsResponseDto getUserMetrics(String period, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        
        // 기간별 처리
        if (startDate != null && endDate != null) {
            // 직접 검색인 경우
            startDateTime = startDate.atStartOfDay();
            endDateTime = endDate.atTime(LocalTime.MAX);
            period = "custom";
        } else {
            // 미리 정의된 기간인 경우
            LocalDate today = LocalDate.now();
            endDateTime = today.atTime(LocalTime.MAX);
            
            switch (period.toLowerCase()) {
                case "today":
                    startDateTime = today.atStartOfDay();
                    break;
                case "1w":
                    startDateTime = today.minusWeeks(1).atStartOfDay();
                    break;
                case "1m":
                    startDateTime = today.minusMonths(1).atStartOfDay();
                    break;
                case "3m":
                    startDateTime = today.minusMonths(3).atStartOfDay();
                    break;
                case "6m":
                    startDateTime = today.minusMonths(6).atStartOfDay();
                    break;
                default:
                    startDateTime = today.minusMonths(1).atStartOfDay();
                    period = "1m";
            }
        }
        
        // 일별 가입자 수 조회
        List<Object[]> dailyRegistrations = userRepository.getDailyUserRegistrations(startDateTime, endDateTime);
        
        List<UserMetricsResponseDto.UserCountByDate> userCounts = new ArrayList<>();
        
        // 누적 가입자 수 계산을 위한 시작점 조회
        long baseCount = userRepository.countUsersByCreatedAtBefore(startDateTime);
        long cumulativeCount = baseCount;
        
        for (Object[] row : dailyRegistrations) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            long dailyCount = ((Number) row[1]).longValue();
            cumulativeCount += dailyCount;
            
            userCounts.add(new UserMetricsResponseDto.UserCountByDate(date, cumulativeCount, dailyCount));
        }
        
        // 전체 활성 사용자 수
        int totalActiveUsers = (int) userRepository.count();
        
        return UserMetricsResponseDto.of(userCounts, totalActiveUsers, period);
    }
    
    // 일반 유저 수 집계 (어드민 제외)
    public UserMetricsResponseDto getNormalUserMetrics(String period, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        
        // 기간별 처리
        if (startDate != null && endDate != null) {
            // 직접 검색인 경우
            startDateTime = startDate.atStartOfDay();
            endDateTime = endDate.atTime(LocalTime.MAX);
            period = "custom";
        } else {
            // 미리 정의된 기간인 경우
            LocalDate today = LocalDate.now();
            endDateTime = today.atTime(LocalTime.MAX);
            
            switch (period.toLowerCase()) {
                case "today":
                    startDateTime = today.atStartOfDay();
                    break;
                case "1w":
                    startDateTime = today.minusWeeks(1).atStartOfDay();
                    break;
                case "1m":
                    startDateTime = today.minusMonths(1).atStartOfDay();
                    break;
                case "3m":
                    startDateTime = today.minusMonths(3).atStartOfDay();
                    break;
                case "6m":
                    startDateTime = today.minusMonths(6).atStartOfDay();
                    break;
                default:
                    startDateTime = today.minusMonths(1).atStartOfDay();
                    period = "1m";
            }
        }
        
        // 일반 유저만 일별 가입자 수 조회 (어드민 제외)
        List<Object[]> dailyRegistrations = userRepository.getDailyUserRegistrationsByRole(
                startDateTime, endDateTime, UserRole.NORMAL);
        
        List<UserMetricsResponseDto.UserCountByDate> userCounts = new ArrayList<>();
        
        // 일반 유저만 누적 가입자 수 계산을 위한 시작점 조회 (어드민 제외)
        long baseCount = userRepository.countUsersByCreatedAtBeforeAndRole(startDateTime, UserRole.NORMAL);
        long cumulativeCount = baseCount;
        
        for (Object[] row : dailyRegistrations) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            long dailyCount = ((Number) row[1]).longValue();
            cumulativeCount += dailyCount;
            
            userCounts.add(new UserMetricsResponseDto.UserCountByDate(date, cumulativeCount, dailyCount));
        }
        
        // 전체 일반 유저 수 (어드민 제외)
        int totalNormalUsers = (int) userRepository.countByRole(UserRole.NORMAL);
        
        return UserMetricsResponseDto.of(userCounts, totalNormalUsers, period);
    }
} 