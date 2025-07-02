package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.DesireItem;
import com.happymapleday.boss.entity.RandomBoxItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RandomBoxItemRepository extends JpaRepository<RandomBoxItem, Long> {

    // 특정 물욕템의 랜덤박스 아이템 조회
    List<RandomBoxItem> findByDesireItemOrderByDropItemName(DesireItem desireItem);

    // 물욕템 ID로 랜덤박스 아이템 조회
    List<RandomBoxItem> findByDesireItemIdOrderByDropItemName(Long desireItemId);

    // 드랍 아이템명으로 검색
    List<RandomBoxItem> findByDropItemNameContainingIgnoreCaseOrderByDropItemName(String dropItemName);

    // 레벨이 있는 아이템만 조회
    List<RandomBoxItem> findByDesireItemAndDropItemLevelIsNotNullOrderByDropItemLevelDesc(DesireItem desireItem);

    // 레벨이 없는 아이템만 조회
    List<RandomBoxItem> findByDesireItemAndDropItemLevelIsNullOrderByDropItemName(DesireItem desireItem);

    // 특정 물욕템의 특정 드랍 아이템 조회
    Optional<RandomBoxItem> findByDesireItemAndDropItemNameAndDropItemLevel(
            DesireItem desireItem, String dropItemName, Integer dropItemLevel);

    // 특정 물욕템의 랜덤박스 아이템 개수 조회
    long countByDesireItem(DesireItem desireItem);

    // 보스별 모든 랜덤박스 아이템 조회
    @Query("SELECT rbi FROM RandomBoxItem rbi " +
           "JOIN rbi.desireItem di " +
           "WHERE di.boss.id = :bossId " +
           "ORDER BY di.itemName, rbi.dropItemName")
    List<RandomBoxItem> findAllByBossId(@Param("bossId") Long bossId);
} 