package com.happymapleday.settlement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Entity
@Table(name = "desire_item_records",
       indexes = {
           @Index(name = "idx_weekly_boss_record", columnList = "weekly_boss_record_id"),
           @Index(name = "idx_character", columnList = "character_id")
       })
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesireItemRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "weekly_boss_record_id", nullable = false)
    private Long weeklyBossRecordId;

    @NotNull
    @Column(name = "character_id", nullable = false)
    private Long characterId;
    
    @NotNull
    @Column(name = "desire_item_id", nullable = false)
    private Long desireItemId;
    
    // 랜덤박스 출처 아이템 ID(랜덤박스에서 나온 결과를 기록할 때, 원본 박스 ID)
    @Column(name = "source_box_item_id")
    private Long sourceBoxItemId;

    @NotNull
    @Column(name = "sale_price", nullable = false)
    private BigInteger salePrice;
    
    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_boss_record_id", insertable = false, updatable = false)
    private WeeklyBossRecord weeklyBossRecord;
} 