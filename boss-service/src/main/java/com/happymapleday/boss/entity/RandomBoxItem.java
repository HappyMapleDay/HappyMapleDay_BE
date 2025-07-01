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
    @JoinColumn(name = "desire_item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_random_box_desire_item"))
    private DesireItem desireItem;

    @Column(name = "drop_item_name", nullable = false, length = 100)
    private String dropItemName;

    @Column(name = "drop_item_level")
    private Integer dropItemLevel;

    @Builder
    public RandomBoxItem(DesireItem desireItem, String dropItemName, Integer dropItemLevel) {
        this.desireItem = desireItem;
        this.dropItemName = dropItemName;
        this.dropItemLevel = dropItemLevel;
    }

    // 비즈니스 메서드
    public String getFullDropItemName() {
        if (dropItemLevel != null) {
            return dropItemName + " (Lv." + dropItemLevel + ")";
        }
        return dropItemName;
    }

    public boolean hasDropLevel() {
        return dropItemLevel != null;
    }
} 