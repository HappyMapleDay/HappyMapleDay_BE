package com.happymapleday.boss.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "boss_presets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_preset_name",
                        columnNames = {"preset_name"}
                )
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BossPreset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "preset_name", nullable = false, length = 50, unique = true)
    private String presetName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "boss_ids", nullable = false, columnDefinition = "JSON")
    private List<Map<String, Object>> bossIds;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public BossPreset(String presetName, List<Map<String, Object>> bossIds) {
        this.presetName = presetName;
        this.bossIds = bossIds;
    }

    // 비즈니스 메서드
    public void updateBossIds(List<Map<String, Object>> newBossIds) {
        this.bossIds = newBossIds;
    }

    public int getBossCount() {
        return bossIds != null ? bossIds.size() : 0;
    }

    // 프리셋에 포함된 보스 ID 목록 추출
    public List<Long> extractBossIds() {
        if (bossIds == null) {
            return List.of();
        }
        
        return bossIds.stream()
                .map(bossData -> {
                    Object bossId = bossData.get("boss_id");
                    if (bossId instanceof Number) {
                        return ((Number) bossId).longValue();
                    }
                    return null;
                })
                .filter(id -> id != null)
                .toList();
    }
} 