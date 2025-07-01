package com.happymapleday.user.repository;

import com.happymapleday.user.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    
    // 사용자 ID로 설정 조회
    Optional<UserSettings> findByUserId(Long userId);
    
    // 사용자 ID로 설정 존재 여부 확인
    boolean existsByUserId(Long userId);
} 