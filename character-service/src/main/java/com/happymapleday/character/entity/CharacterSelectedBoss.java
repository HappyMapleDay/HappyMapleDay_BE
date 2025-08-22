package com.happymapleday.character.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public CharacterSelectedBoss(Long characterId, Long bossId) {
        this.characterId = characterId;
        this.bossId = bossId;
    }
}


