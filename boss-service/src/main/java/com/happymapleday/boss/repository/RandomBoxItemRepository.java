package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.Item;
import com.happymapleday.boss.entity.RandomBoxItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RandomBoxItemRepository extends JpaRepository<RandomBoxItem, Long> {

    // 특정 아이템의 랜덤박스 아이템 조회
    @Query("SELECT rbi FROM RandomBoxItem rbi " +
           "WHERE rbi.item = :item " +
           "ORDER BY rbi.boxContentItem.itemName")
    List<RandomBoxItem> findByItemOrderByBoxContentItemName(@Param("item") Item item);

    // 아이템 ID로 랜덤박스 아이템 조회
    @Query("SELECT rbi FROM RandomBoxItem rbi " +
           "WHERE rbi.item.id = :itemId " +
           "ORDER BY rbi.boxContentItem.itemName")
    List<RandomBoxItem> findByItemIdOrderByBoxContentItemName(@Param("itemId") Long itemId);

    // 박스 내용물 아이템명으로 검색
    @Query("SELECT rbi FROM RandomBoxItem rbi " +
           "WHERE rbi.boxContentItem.itemName LIKE %:itemName% " +
           "ORDER BY rbi.boxContentItem.itemName")
    List<RandomBoxItem> findByBoxContentItemNameContaining(@Param("itemName") String itemName);

    // 특정 랜덤박스의 레벨이 있는 내용물만 조회
    @Query("SELECT rbi FROM RandomBoxItem rbi " +
           "WHERE rbi.item = :item AND rbi.boxContentItem.itemLevel IS NOT NULL " +
           "ORDER BY rbi.boxContentItem.itemLevel DESC")
    List<RandomBoxItem> findByItemAndBoxContentItemLevelIsNotNull(@Param("item") Item item);

    // 특정 랜덤박스의 레벨이 없는 내용물만 조회
    @Query("SELECT rbi FROM RandomBoxItem rbi " +
           "WHERE rbi.item = :item AND rbi.boxContentItem.itemLevel IS NULL " +
           "ORDER BY rbi.boxContentItem.itemName")
    List<RandomBoxItem> findByItemAndBoxContentItemLevelIsNull(@Param("item") Item item);

    // 특정 랜덤박스의 특정 내용물 조회
    @Query("SELECT rbi FROM RandomBoxItem rbi " +
           "WHERE rbi.item = :item AND rbi.boxContentItem.itemName = :itemName " +
           "AND rbi.boxContentItem.itemLevel = :itemLevel")
    Optional<RandomBoxItem> findByItemAndBoxContentItemNameAndLevel(
            @Param("item") Item item, @Param("itemName") String itemName, @Param("itemLevel") Integer itemLevel);

    // 특정 아이템의 랜덤박스 아이템 개수 조회
    long countByItem(Item item);

    // 보스별 모든 랜덤박스 아이템 조회 (보스 드랍 아이템을 통해)
    @Query("SELECT rbi FROM RandomBoxItem rbi " +
           "JOIN rbi.item i " +
           "JOIN BossDropItem bdi ON bdi.item = i " +
           "WHERE bdi.boss.id = :bossId " +
           "ORDER BY i.itemName, rbi.boxContentItem.itemName")
    List<RandomBoxItem> findAllByBossId(@Param("bossId") Long bossId);

    // 랜덤박스 아이템들만 조회 (isRandomBox = true인 아이템들)
    @Query("SELECT rbi FROM RandomBoxItem rbi " +
           "WHERE rbi.item.isRandomBox = true " +
           "ORDER BY rbi.item.itemName, rbi.boxContentItem.itemName")
    List<RandomBoxItem> findAllRandomBoxItems();
} 