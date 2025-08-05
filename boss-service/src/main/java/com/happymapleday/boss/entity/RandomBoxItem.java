package com.happymapleday.boss.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "random_box_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RandomBoxItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_random_box_item"))
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_content_item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_random_box_content_item"))
    private BoxContentItem boxContentItem;

    @Builder
    public RandomBoxItem(Item item, BoxContentItem boxContentItem) {
        this.item = item;
        this.boxContentItem = boxContentItem;
    }

    // 비즈니스 메서드
    public String getDropItemName() {
        return boxContentItem.getItemName();
    }

    public String getDropItemNameEn(){
        return boxContentItem.getItemNameEn();
    }

    public Integer getDropItemLevel() {
        return boxContentItem.getItemLevel();
    }

    public String getFullDropItemName() {
        return boxContentItem.getFullItemName();
    }

    public boolean hasDropLevel() {
        return boxContentItem.hasLevel();
    }
} 