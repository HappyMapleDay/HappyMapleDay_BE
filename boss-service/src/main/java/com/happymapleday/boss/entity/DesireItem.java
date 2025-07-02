package com.happymapleday.boss.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "desire_items",
        indexes = {
                @Index(name = "idx_boss_item", columnList = "boss_id, item_name")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DesireItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boss_id", nullable = false, foreignKey = @ForeignKey(name = "fk_desire_item_boss"))
    private Boss boss;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "is_random_box")
    private Boolean isRandomBox = false;

    // 랜덤 상자 아이템과의 연관관계 (일대다)
    @OneToMany(mappedBy = "desireItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RandomBoxItem> randomBoxItems = new ArrayList<>();

    @Builder
    public DesireItem(Boss boss, String itemName, Boolean isRandomBox) {
        this.boss = boss;
        this.itemName = itemName;
        this.isRandomBox = isRandomBox != null ? isRandomBox : false;
    }

    // 비즈니스 메서드
    public String getFullItemName() {
        return itemName;
    }



    public void addRandomBoxItem(RandomBoxItem randomBoxItem) {
        this.randomBoxItems.add(randomBoxItem);
    }
} 