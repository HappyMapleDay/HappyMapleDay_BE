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
                .partySize(2)
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
                .partySize(1)
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
                .partySize(6)
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
                .partySize(1)
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
                .partySize(2)
                .build();

        // when
        boolean result = bossSelection.isSoloBoss();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Builder 패턴으로 객체 생성 확인")
    void builderPattern_CreatesObjectCorrectly() {
        // given & when
        BossSelection bossSelection = BossSelection.builder()
                .bossId(1L)
                .partySize(1)
                .build();

        // then
        assertThat(bossSelection.getBossId()).isEqualTo(1L);
        assertThat(bossSelection.getPartySize()).isEqualTo(1);
    }
} 