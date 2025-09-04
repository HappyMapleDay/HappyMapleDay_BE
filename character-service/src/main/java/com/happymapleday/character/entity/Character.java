package com.happymapleday.character.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "characters")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Character {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "character_name", nullable = false, length = 12)
    private String characterName;
    
    @Column(name = "ocid", nullable = false, length = 50)
    private String ocid;
    
    @Column(name = "is_main", nullable = false)
    private Boolean isMain = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 생성자
    public Character(Long userId, String characterName, String ocid, Boolean isMain) {
        this.userId = userId;
        this.characterName = characterName;
        this.ocid = ocid;
        this.isMain = isMain != null ? isMain : false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // 본캐 설정
    public void setAsMainCharacter() {
        this.isMain = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    // 본캐 해제
    public void unsetAsMainCharacter() {
        this.isMain = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    // 업데이트 시간 갱신
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
} 