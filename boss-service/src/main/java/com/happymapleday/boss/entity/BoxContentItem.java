package com.happymapleday.boss.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "box_content_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoxContentItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name="item_name_en", nullable=false)
    private String itemNameEn;
    
    @Column(name = "item_level")
    private Integer itemLevel;
    
    @Column(name = "full_item_name")
    private String fullItemName;
    
    @Column(name = "is_special")
    private Boolean isSpecial;
    
    @Column(name = "notes")
    private String notes;
    
    // 풀네임 자동 생성
    @PrePersist
    @PreUpdate
    private void generateFullItemName() {
        if (itemLevel != null) {
            this.fullItemName = itemName + " " + itemLevel + "레벨";
        } else {
            this.fullItemName = itemName;
        }
    }
    
    // 레벨이 있는 아이템인지 확인
    public boolean hasLevel() {
        return itemLevel != null;
    }
} 