package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.BossDto;
import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.ForceType;
import com.happymapleday.boss.repository.BossRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("BossService 테스트")
class BossServiceTest {

    @Mock
    private BossRepository bossRepository;

    @InjectMocks
    private BossService bossService;

    private Boss testBoss1;
    private Boss testBoss2;

    @BeforeEach
    void setUp() {
        testBoss1 = Boss.builder()
                .bossName("자쿰")
                .difficulty("카오스")
                .crystalPrice(8080000L)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(90)
                .requiredForceType(ForceType.NONE)
                .requiredForceAmount(0)
                .build();

        testBoss2 = Boss.builder()
                .bossName("루시드")
                .difficulty("하드")
                .crystalPrice(94500000L)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(220)
                .requiredForceType(ForceType.ARCANE)
                .requiredForceAmount(360)
                .build();
    }

    @Test
    @DisplayName("모든 활성화된 보스 조회")
    void getAllActiveBosses() {
        // given
        List<Boss> bosses = Arrays.asList(testBoss1, testBoss2);
        given(bossRepository.findByIsActiveTrueOrderByCrystalPriceDesc()).willReturn(bosses);

        // when
        List<BossDto.Response> result = bossService.getAllActiveBosses();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getBossName()).isEqualTo("자쿰");
        assertThat(result.get(0).getDifficulty()).isEqualTo("카오스");
        assertThat(result.get(1).getBossName()).isEqualTo("루시드");
        assertThat(result.get(1).getDifficulty()).isEqualTo("하드");
    }
} 