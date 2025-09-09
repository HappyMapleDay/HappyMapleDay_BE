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
    
    // 유저별 캐릭터 목록 조회 (삭제되지 않은 캐릭터만)
    List<Character> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);
    
    // 유저의 본캐 조회 (삭제되지 않은 캐릭터만)
    Optional<Character> findByUserIdAndIsMainTrueAndIsDeletedFalse(Long userId);
    
    // OCID로 캐릭터 조회 (삭제되지 않은 캐릭터만)
    Optional<Character> findByUserIdAndOcidAndIsDeletedFalse(Long userId, String ocid);
    
    // OCID로 캐릭터 조회 (전역, 삭제되지 않은 캐릭터만)
    Optional<Character> findByOcidAndIsDeletedFalse(String ocid);
    
    // 삭제되지 않은 캐릭터 조회 (ID로)
    Optional<Character> findByIdAndIsDeletedFalse(Long id);
    
    // 유저의 모든 본캐 해제 (삭제되지 않은 캐릭터만)
    @Modifying
    @Query("UPDATE Character c SET c.isMain = false WHERE c.userId = :userId AND c.isDeleted = false")
    void unsetAllMainCharactersByUserId(@Param("userId") Long userId);
    
    // 유저의 특정 캐릭터를 본캐로 설정 (삭제되지 않은 캐릭터만)
    @Modifying
    @Query("UPDATE Character c SET c.isMain = true WHERE c.id = :characterId AND c.isDeleted = false")
    void setMainCharacterById(@Param("characterId") Long characterId);
    
    // 유저의 캐릭터 수 조회 (삭제되지 않은 캐릭터만)
    long countByUserIdAndIsDeletedFalse(Long userId);
    
    // 삭제되지 않은 캐릭터들을 ID 목록으로 조회
    List<Character> findByIdInAndIsDeletedFalse(List<Long> ids);
} 