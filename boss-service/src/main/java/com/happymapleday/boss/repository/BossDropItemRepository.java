package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.BossDropItem;
import com.happymapleday.boss.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BossDropItemRepository extends JpaRepository<BossDropItem, Long> {

    // 특정 보스의 드랍 아이템 조회
    List<BossDropItem> findByBossOrderByItem_ItemName(Boss boss);

    // 보스 ID로 드랍 아이템 조회
    List<BossDropItem> findByBoss_IdOrderByItem_ItemName(Long bossId);

    // 보스 ID로 드랍 아이템과 랜덤박스 아이템을 함께 조회 (Fetch Join)
    @Query("SELECT DISTINCT bdi FROM BossDropItem bdi " +
           "LEFT JOIN FETCH bdi.item i " +
           "LEFT JOIN FETCH i.randomBoxItems rbi " +
           "LEFT JOIN FETCH bdi.boss " +
           "WHERE bdi.boss.id = :bossId " +
           "ORDER BY i.itemName")
    List<BossDropItem> findByBossIdWithRandomBoxItems(@Param("bossId") Long bossId);

    // 특정 보스의 특정 아이템 조회
    Optional<BossDropItem> findByBossAndItem(Boss boss, Item item);

    // 보스 ID와 아이템 ID로 조회
    Optional<BossDropItem> findByBoss_IdAndItem_Id(Long bossId, Long itemId);

    // 특정 보스의 드랍 아이템 개수 조회
    long countByBoss(Boss boss);

    // 랜덤박스 아이템만 조회
    @Query("SELECT bdi FROM BossDropItem bdi " +
           "WHERE bdi.boss.id = :bossId AND bdi.item.isRandomBox = true " +
           "ORDER BY bdi.item.itemName")
    List<BossDropItem> findRandomBoxItemsByBossId(@Param("bossId") Long bossId);

    // 일반 드랍 아이템만 조회 (랜덤박스가 아닌)
    @Query("SELECT bdi FROM BossDropItem bdi " +
           "WHERE bdi.boss.id = :bossId AND bdi.item.isRandomBox = false " +
           "ORDER BY bdi.item.itemName")
    List<BossDropItem> findNormalDropItemsByBossId(@Param("bossId") Long bossId);
} 