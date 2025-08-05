package com.happymapleday.boss.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bosses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_boss_difficulty",
                        columnNames = {"boss_name", "difficulty"}
                )
        },
        indexes = {
                @Index(name = "idx_boss_name", columnList = "boss_name"),
                @Index(name = "idx_difficulty", columnList = "difficulty"),
                @Index(name = "idx_crystal_price", columnList = "crystal_price DESC")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Boss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "boss_name", nullable = false, length = 50)
    private String bossName;

    @Column(name = "boss_name_en", nullable = false, length = 50)
    private String bossNameEn;

    @Column(name = "difficulty", nullable = false, length = 20)
    private String difficulty;

    @Column(name = "difficulty_en", nullable = false, length = 20)
    private String difficultyEn;

    @Column(name = "crystal_price", nullable = false)
    private Long crystalPrice;

    @Column(name = "max_party_size")
    private Integer maxPartySize = 6;

    @Column(name = "is_monthly")
    private Boolean isMonthly = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "min_entry_level")
    private Integer minEntryLevel;

    @Column(name = "boss_level")
    private Integer bossLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_force_type", length = 20)
    private ForceType requiredForceType;

    @Column(name = "required_force_amount")
    private Integer requiredForceAmount;

    // 보스 드랍 아이템과의 연관관계 (일대다)
    @OneToMany(mappedBy = "boss", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BossDropItem> bossDropItems = new ArrayList<>();

    @Builder
    public Boss(String bossName, String bossNameEn, String difficulty, String difficultyEn, Long crystalPrice,
                Integer maxPartySize, Boolean isMonthly, Boolean isActive,
                Integer minEntryLevel, Integer bossLevel, ForceType requiredForceType, Integer requiredForceAmount) {
        this.bossName = bossName;
        this.bossNameEn = bossNameEn;
        this.difficulty = difficulty;
        this.difficultyEn = difficultyEn;
        this.crystalPrice = crystalPrice;
        this.maxPartySize = maxPartySize != null ? maxPartySize : 6;
        this.isMonthly = isMonthly != null ? isMonthly : false;
        this.isActive = isActive != null ? isActive : true;
        this.minEntryLevel = minEntryLevel;
        this.bossLevel = bossLevel;
        this.requiredForceType = requiredForceType;
        this.requiredForceAmount = requiredForceAmount;
    }

    // 비즈니스 메서드
    public String getFullName() {
        return bossName + " (" + difficulty + ")";
    }

    public boolean isWeeklyBoss() {
        return !isMonthly;
    }

    public void updateCrystalPrice(Long newPrice) {
        this.crystalPrice = newPrice;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    // 포스 관련 메서드
    public boolean requiresForce() {
        return requiredForceType != null && requiredForceType.isForceRequired();
    }

    public boolean isArcaneForceRequired() {
        return requiredForceType == ForceType.ARCANE;
    }

    public boolean isAuthenticForceRequired() {
        return requiredForceType == ForceType.AUTHENTIC;
    }

    public boolean canEnterWithLevel(int characterLevel) {
        return minEntryLevel == null || characterLevel >= minEntryLevel;
    }

    public boolean canChallenge(int characterLevel, Integer arcaneForce, Integer authenticForce) {
        // 레벨 체크
        if (!canEnterWithLevel(characterLevel)) {
            return false;
        }

        // 포스 체크
        if (requiresForce()) {
            if (isArcaneForceRequired() && (arcaneForce == null || arcaneForce < requiredForceAmount)) {
                return false;
            }
            if (isAuthenticForceRequired() && (authenticForce == null || authenticForce < requiredForceAmount)) {
                return false;
            }
        }

        return true;
    }
} 