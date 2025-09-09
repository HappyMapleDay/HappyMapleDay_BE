package com.happymapleday.user.repository;

import com.happymapleday.user.entity.User;
import com.happymapleday.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 메인 캐릭터명으로 사용자 조회
    Optional<User> findByMainCharacterName(String mainCharacterName);
    
    // 메인 캐릭터명 존재 여부 확인
    boolean existsByMainCharacterName(String mainCharacterName);
    
    // 권한별 사용자 조회
    List<User> findByRoleOrderByCreatedAtDesc(UserRole role);
    
    // 일반 사용자 조회 (편의 메서드)
    default List<User> findNormalUsersOrderByCreatedAtDesc() {
        return findByRoleOrderByCreatedAtDesc(UserRole.NORMAL);
    }
    
    // 어드민 사용자 조회 (편의 메서드)
    default List<User> findAdminUsersOrderByCreatedAtDesc() {
        return findByRoleOrderByCreatedAtDesc(UserRole.ADMIN);
    }
    
    // 모든 사용자 조회 (생성일 기준 내림차순)
    List<User> findAllByOrderByCreatedAtDesc();
    
    // 기간별 가입자 수 집계
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    long countUsersByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    // 전체 활성 사용자 수 (탈퇴하지 않은 사용자)
    long count();
    
    // 특정 날짜까지의 누적 가입자 수
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt <= :endDate")
    long countUsersByCreatedAtBefore(@Param("endDate") LocalDateTime endDate);
    
    // 일별 가입자 수 집계
    @Query("SELECT DATE(u.createdAt) as date, COUNT(u) as count " +
           "FROM User u " +
           "WHERE u.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(u.createdAt) " +
           "ORDER BY DATE(u.createdAt)")
    List<Object[]> getDailyUserRegistrations(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    // 일반 유저만 일별 가입자 수 집계 (어드민 제외)
    @Query("SELECT DATE(u.createdAt) as date, COUNT(u) as count " +
           "FROM User u " +
           "WHERE u.createdAt BETWEEN :startDate AND :endDate " +
           "AND u.role = :role " +
           "GROUP BY DATE(u.createdAt) " +
           "ORDER BY DATE(u.createdAt)")
    List<Object[]> getDailyUserRegistrationsByRole(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate,
                                                  @Param("role") UserRole role);
    
    // 일반 유저만 기간별 가입자 수 집계 (어드민 제외)
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.role = :role")
    long countUsersByCreatedAtBetweenAndRole(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate,
                                           @Param("role") UserRole role);
    
    // 일반 유저만 특정 날짜까지의 누적 가입자 수 (어드민 제외)
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt <= :endDate AND u.role = :role")
    long countUsersByCreatedAtBeforeAndRole(@Param("endDate") LocalDateTime endDate,
                                          @Param("role") UserRole role);
    
    // 일반 유저 총 수 (어드민 제외)
    long countByRole(UserRole role);
} 