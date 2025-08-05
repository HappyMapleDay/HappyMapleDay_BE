package com.happymapleday.boss.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items",
        indexes = {
                @Index(name = "idx_item_name", columnList = "item_name")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", nullable = false, length = 100, unique = true)
    private String itemName;

    @Column(name="item_name_en", nullable = false, length = 100)
    private String itemNameEn;

    @Column(name = "is_random_box")
    private Boolean isRandomBox = false;

    // 랜덤 상자 아이템과의 연관관계 (일대다)
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RandomBoxItem> randomBoxItems = new ArrayList<>();

    @Builder
    public Item(String itemName, String itemNameEn, Boolean isRandomBox) {
        this.itemName = itemName;
        this.itemNameEn = itemNameEn;
        this.isRandomBox = isRandomBox != null ? isRandomBox : false;
    }

    // 비즈니스 메서드
    public String getFullItemName() {
        return itemName;
    }
} 