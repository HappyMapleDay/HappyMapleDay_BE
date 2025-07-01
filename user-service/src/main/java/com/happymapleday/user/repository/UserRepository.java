package com.happymapleday.user.repository;

import com.happymapleday.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 메인 캐릭터명으로 사용자 조회
    Optional<User> findByMainCharacterName(String mainCharacterName);
    
    // 메인 캐릭터명 존재 여부 확인
    boolean existsByMainCharacterName(String mainCharacterName);
} 