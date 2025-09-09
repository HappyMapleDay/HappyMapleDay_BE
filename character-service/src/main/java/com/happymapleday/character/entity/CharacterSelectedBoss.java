package com.happymapleday.character.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "character_selected_boss",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_character_boss", columnNames = {"character_id", "boss_id"})
        },
        indexes = {
                @Index(name = "idx_character_id", columnList = "character_id")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CharacterSelectedBoss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "character_id", nullable = false)
    private Long characterId;

    @Column(name = "boss_id", nullable = false)
    private Long bossId;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public CharacterSelectedBoss(Long characterId, Long bossId) {
        this.characterId = characterId;
        this.bossId = bossId;
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // soft delete
    public void markAsDeleted() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    // soft delete 복원
    public void markAsUndeleted() {
        this.isDeleted = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    // 업데이트 시간 갱신
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}


