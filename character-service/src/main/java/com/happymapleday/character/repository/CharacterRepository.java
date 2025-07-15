package com.happymapleday.character.repository;

import com.happymapleday.character.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
    
    // 유저별 캐릭터 목록 조회
    List<Character> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // 유저의 본캐 조회
    Optional<Character> findByUserIdAndIsMainTrue(Long userId);
    
    // OCID로 캐릭터 조회
    Optional<Character> findByUserIdAndOcid(Long userId, String ocid);
    
    // 유저의 모든 본캐 해제
    @Modifying
    @Query("UPDATE Character c SET c.isMain = false WHERE c.userId = :userId")
    void unsetAllMainCharactersByUserId(@Param("userId") Long userId);
    
    // 유저의 특정 캐릭터를 본캐로 설정
    @Modifying
    @Query("UPDATE Character c SET c.isMain = true WHERE c.id = :characterId")
    void setMainCharacterById(@Param("characterId") Long characterId);
    
    // 유저의 캐릭터 수 조회
    long countByUserId(Long userId);
} 