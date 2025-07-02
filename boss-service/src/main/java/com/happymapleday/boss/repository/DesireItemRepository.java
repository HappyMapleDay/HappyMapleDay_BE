package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.DesireItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesireItemRepository extends JpaRepository<DesireItem, Long> {

    // 특정 보스의 물욕템 조회
    List<DesireItem> findByBossOrderByItemName(Boss boss);

    // 보스 ID로 물욕템 조회
    List<DesireItem> findByBossIdOrderByItemName(Long bossId);

    // 랜덤박스 여부로 필터링
    List<DesireItem> findByBossAndIsRandomBoxOrderByItemName(Boss boss, Boolean isRandomBox);

    // 아이템명으로 검색
    List<DesireItem> findByItemNameContainingIgnoreCaseOrderByItemName(String itemName);

    // 특정 보스의 특정 아이템 조회
    Optional<DesireItem> findByBossAndItemName(Boss boss, String itemName);

    // 보스 ID와 아이템명으로 조회
    Optional<DesireItem> findByBossIdAndItemName(Long bossId, String itemName);

    // 특정 보스의 물욕템 개수 조회
    long countByBoss(Boss boss);

    // 랜덤박스 아이템만 조회
    @Query("SELECT di FROM DesireItem di WHERE di.boss.id = :bossId AND di.isRandomBox = true ORDER BY di.itemName")
    List<DesireItem> findRandomBoxItemsByBossId(@Param("bossId") Long bossId);

    // 일반 물욕템만 조회 (랜덤박스가 아닌)
    @Query("SELECT di FROM DesireItem di WHERE di.boss.id = :bossId AND di.isRandomBox = false ORDER BY di.itemName")
    List<DesireItem> findNormalDesireItemsByBossId(@Param("bossId") Long bossId);
} 