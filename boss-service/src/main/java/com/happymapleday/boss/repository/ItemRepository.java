package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByIsRandomBoxTrue();
    List<Item> findByIsRandomBoxFalse();
    
    // 아이템 이름으로 검색 (대소문자 구분 없이 부분 일치)
    List<Item> findByItemNameContainingIgnoreCase(String itemName);
    
    // 아이템 이름 중복 체크
    boolean existsByItemName(String itemName);
} 