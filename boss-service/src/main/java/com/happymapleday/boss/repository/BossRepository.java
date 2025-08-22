package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.ForceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BossRepository extends JpaRepository<Boss, Long> {

    // 활성화된 보스만 조회
    List<Boss> findByIsActiveTrueOrderByCrystalPriceDesc();

    // 보스명으로 검색
    List<Boss> findByBossNameContainingIgnoreCaseAndIsActiveTrue(String bossName);

    // 난이도별 조회
    List<Boss> findByDifficultyAndIsActiveTrueOrderByCrystalPriceDesc(String difficulty);

    // 주간/월간 보스 구분 조회
    List<Boss> findByIsMonthlyAndIsActiveTrueOrderByCrystalPriceDesc(Boolean isMonthly);

    // 포스 타입별 조회
    List<Boss> findByRequiredForceTypeAndIsActiveTrueOrderByCrystalPriceDesc(ForceType forceType);

    // 특정 보스명과 난이도로 조회
    Optional<Boss> findByBossNameAndDifficultyAndIsActiveTrue(String bossName, String difficulty);

    // 결정석 가격 범위로 조회
    @Query("SELECT b FROM Boss b WHERE b.isActive = true AND b.crystalPrice BETWEEN :minPrice AND :maxPrice ORDER BY b.crystalPrice DESC")
    List<Boss> findByPriceRange(@Param("minPrice") Long minPrice, @Param("maxPrice") Long maxPrice);

    // 캐릭터 레벨에 맞는 보스 조회
    @Query("SELECT b FROM Boss b WHERE b.isActive = true AND (b.minEntryLevel IS NULL OR b.minEntryLevel <= :characterLevel) ORDER BY b.crystalPrice DESC")
    List<Boss> findBossesForCharacterLevel(@Param("characterLevel") Integer characterLevel);

    // 포스 조건에 맞는 보스 조회
    @Query("SELECT b FROM Boss b WHERE b.isActive = true AND " +
           "(b.requiredForceType = 'NONE' OR " +
           "(b.requiredForceType = 'ARCANE' AND :arcaneForce >= b.requiredForceAmount) OR " +
           "(b.requiredForceType = 'AUTHENTIC' AND :authenticForce >= b.requiredForceAmount)) " +
           "ORDER BY b.crystalPrice DESC")
    List<Boss> findBossesForForceCondition(@Param("arcaneForce") Integer arcaneForce, 
                                          @Param("authenticForce") Integer authenticForce);

    // 페이징을 지원하는 활성화된 보스 조회
    Page<Boss> findByIsActiveTrueOrderByCrystalPriceDesc(Pageable pageable);

    // 보스명과 난이도 중복 체크
    boolean existsByBossNameAndDifficultyAndIsActiveTrue(String bossName, String difficulty);

    // 보스 드랍 아이템과 함께 활성화된 보스 조회
    @Query("SELECT DISTINCT b FROM Boss b " +
           "LEFT JOIN FETCH b.bossDropItems bdi " +
           "LEFT JOIN FETCH bdi.item i " +
           "WHERE b.isActive = true " +
           "ORDER BY b.crystalPrice DESC")
    List<Boss> findByIsActiveTrueWithBossDropItemsOrderByCrystalPriceDesc();

    // 특정 ID 목록으로 보스 조회 (드랍 아이템 포함)
    @Query("SELECT DISTINCT b FROM Boss b " +
           "LEFT JOIN FETCH b.bossDropItems bdi " +
           "LEFT JOIN FETCH bdi.item i " +
           "WHERE b.isActive = true AND b.id IN :ids")
    List<Boss> findActiveByIdInWithDropItems(@Param("ids") List<Long> ids);
} 