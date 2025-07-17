package com.happymapleday.recommendation.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BossSelectionTest {

    @Test
    @DisplayName("파티 보스 확인 - 파티 사이즈 2 이상")
    void isPartyBoss_PartySize2OrMore_ReturnsTrue() {
        // given
        BossSelection bossSelection = BossSelection.builder()
                .bossId(1L)
                .bossName("루타비스")
                .difficulty("하드")
                .crystalPrice(200000L)
                .partySize(2)
                .maxPartySize(6)
                .build();

        // when
        boolean result = bossSelection.isPartyBoss();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("파티 보스 확인 - 파티 사이즈 1")
    void isPartyBoss_PartySize1_ReturnsFalse() {
        // given
        BossSelection bossSelection = BossSelection.builder()
                .bossId(1L)
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(10000L)
                .partySize(1)
                .maxPartySize(6)
                .build();

        // when
        boolean result = bossSelection.isPartyBoss();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("파티 보스 확인 - 파티 사이즈 6")
    void isPartyBoss_PartySize6_ReturnsTrue() {
        // given
        BossSelection bossSelection = BossSelection.builder()
                .bossId(1L)
                .bossName("검은마법사")
                .difficulty("하드")
                .crystalPrice(1000000L)
                .partySize(6)
                .maxPartySize(6)
                .build();

        // when
        boolean result = bossSelection.isPartyBoss();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("솔로 보스 확인 - 파티 사이즈 1")
    void isSoloBoss_PartySize1_ReturnsTrue() {
        // given
        BossSelection bossSelection = BossSelection.builder()
                .bossId(1L)
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(10000L)
                .partySize(1)
                .maxPartySize(6)
                .build();

        // when
        boolean result = bossSelection.isSoloBoss();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("솔로 보스 확인 - 파티 사이즈 2")
    void isSoloBoss_PartySize2_ReturnsFalse() {
        // given
        BossSelection bossSelection = BossSelection.builder()
                .bossId(1L)
                .bossName("루타비스")
                .difficulty("하드")
                .crystalPrice(200000L)
                .partySize(2)
                .maxPartySize(6)
                .build();

        // when
        boolean result = bossSelection.isSoloBoss();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("솔로 보스 확인 - 파티 사이즈 6")
    void isSoloBoss_PartySize6_ReturnsFalse() {
        // given
        BossSelection bossSelection = BossSelection.builder()
                .bossId(1L)
                .bossName("검은마법사")
                .difficulty("하드")
                .crystalPrice(1000000L)
                .partySize(6)
                .maxPartySize(6)
                .build();

        // when
        boolean result = bossSelection.isSoloBoss();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("파티 보스와 솔로 보스 상호 배타적 확인")
    void isPartyBossAndSoloBoss_MutuallyExclusive() {
        // given
        BossSelection soloBoss = BossSelection.builder()
                .bossId(1L)
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(10000L)
                .partySize(1)
                .maxPartySize(6)
                .build();

        BossSelection partyBoss = BossSelection.builder()
                .bossId(2L)
                .bossName("루타비스")
                .difficulty("하드")
                .crystalPrice(200000L)
                .partySize(3)
                .maxPartySize(6)
                .build();

        // when & then
        assertThat(soloBoss.isSoloBoss()).isTrue();
        assertThat(soloBoss.isPartyBoss()).isFalse();

        assertThat(partyBoss.isPartyBoss()).isTrue();
        assertThat(partyBoss.isSoloBoss()).isFalse();
    }

    @Test
    @DisplayName("Builder 패턴으로 객체 생성 확인")
    void builderPattern_CreatesObjectCorrectly() {
        // given & when
        BossSelection bossSelection = BossSelection.builder()
                .bossId(1L)
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(10000L)
                .partySize(1)
                .maxPartySize(6)
                .build();

        // then
        assertThat(bossSelection.getBossId()).isEqualTo(1L);
        assertThat(bossSelection.getBossName()).isEqualTo("자쿰");
        assertThat(bossSelection.getDifficulty()).isEqualTo("이지");
        assertThat(bossSelection.getCrystalPrice()).isEqualTo(10000L);
        assertThat(bossSelection.getPartySize()).isEqualTo(1);
        assertThat(bossSelection.getMaxPartySize()).isEqualTo(6);
    }

    @Test
    @DisplayName("다양한 난이도 보스 테스트")
    void variousDifficultyBosses() {
        // given
        BossSelection easyBoss = BossSelection.builder()
                .bossId(1L)
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(10000L)
                .partySize(1)
                .maxPartySize(6)
                .build();

        BossSelection normalBoss = BossSelection.builder()
                .bossId(2L)
                .bossName("자쿰")
                .difficulty("노말")
                .crystalPrice(50000L)
                .partySize(1)
                .maxPartySize(6)
                .build();

        BossSelection hardBoss = BossSelection.builder()
                .bossId(3L)
                .bossName("자쿰")
                .difficulty("하드")
                .crystalPrice(100000L)
                .partySize(1)
                .maxPartySize(6)
                .build();

        // when & then
        assertThat(easyBoss.getDifficulty()).isEqualTo("이지");
        assertThat(easyBoss.getCrystalPrice()).isEqualTo(10000L);
        assertThat(easyBoss.isSoloBoss()).isTrue();

        assertThat(normalBoss.getDifficulty()).isEqualTo("노말");
        assertThat(normalBoss.getCrystalPrice()).isEqualTo(50000L);
        assertThat(normalBoss.isSoloBoss()).isTrue();

        assertThat(hardBoss.getDifficulty()).isEqualTo("하드");
        assertThat(hardBoss.getCrystalPrice()).isEqualTo(100000L);
        assertThat(hardBoss.isSoloBoss()).isTrue();
    }
} 