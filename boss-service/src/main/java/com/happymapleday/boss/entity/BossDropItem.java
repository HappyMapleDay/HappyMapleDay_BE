package com.happymapleday.boss.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "boss_drop_items",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_boss_item", columnNames = {"boss_id", "item_id"})
        },
        indexes = {
                @Index(name = "idx_boss_drop_boss", columnList = "boss_id"),
                @Index(name = "idx_boss_drop_item", columnList = "item_id")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BossDropItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boss_id", nullable = false, foreignKey = @ForeignKey(name = "fk_boss_drop_boss"))
    private Boss boss;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_boss_drop_item"))
    private Item item;

    @Builder
    public BossDropItem(Boss boss, Item item) {
        this.boss = boss;
        this.item = item;
    }

    // 비즈니스 메서드
    public String getItemName() {
        return item.getItemName();
    }

    public String getFullItemName() {
        return item.getFullItemName();
    }

    public Boolean getIsRandomBox() {
        return item.getIsRandomBox();
    }
} 