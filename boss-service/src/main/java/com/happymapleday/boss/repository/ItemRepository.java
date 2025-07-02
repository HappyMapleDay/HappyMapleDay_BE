package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByIsRandomBoxTrue();
    List<Item> findByIsRandomBoxFalse();
} 