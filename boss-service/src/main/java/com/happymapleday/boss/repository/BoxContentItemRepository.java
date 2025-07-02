package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.BoxContentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoxContentItemRepository extends JpaRepository<BoxContentItem, Long> {

    // 아이템명으로 검색
    List<BoxContentItem> findByItemNameContainingIgnoreCaseOrderByItemName(String itemName);

    // 레벨이 있는 아이템만 조회
    List<BoxContentItem> findByItemLevelIsNotNullOrderByItemLevelDesc();

    // 레벨이 없는 아이템만 조회
    List<BoxContentItem> findByItemLevelIsNullOrderByItemName();

    // 특정 아이템명과 레벨로 조회
    Optional<BoxContentItem> findByItemNameAndItemLevel(String itemName, Integer itemLevel);

    // 특별한 아이템만 조회
    List<BoxContentItem> findByIsSpecialTrueOrderByItemName();

    // 중복 체크
    boolean existsByItemNameAndItemLevel(String itemName, Integer itemLevel);
} 