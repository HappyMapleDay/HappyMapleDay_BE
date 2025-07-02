package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.response.BossPresetResponse;

import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.BossPreset;
import com.happymapleday.boss.entity.ForceType;
import com.happymapleday.boss.repository.BossPresetRepository;
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
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("BossPresetService 테스트")
class BossPresetServiceTest {

    @Mock
    private BossPresetRepository bossPresetRepository;

    @Mock
    private BossRepository bossRepository;

    @InjectMocks
    private BossPresetService bossPresetService;

    private BossPreset testPreset;
    private Boss testBoss1;
    private Boss testBoss2;
    private Boss testBoss3;

    @BeforeEach
    void setUp() {
        List<Map<String, Object>> bossIds = List.of(
            Map.of("boss_id", 1L),
            Map.of("boss_id", 2L),
            Map.of("boss_id", 3L)
        );
        
        testPreset = BossPreset.builder()
                .presetName("주간보스 세트")
                .bossIds(bossIds)
                .build();

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

        testBoss3 = Boss.builder()
                .bossName("윌")
                .difficulty("하드")
                .crystalPrice(116000000L)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(235)
                .requiredForceType(ForceType.ARCANE)
                .requiredForceAmount(760)
                .build();
    }

    @Test
    @DisplayName("보스 정보를 포함한 모든 프리셋 조회")
    void getAllPresetsWithBosses() {
        // given
        List<BossPreset> presets = Arrays.asList(testPreset);
        given(bossPresetRepository.findAllByOrderByCreatedAtDesc()).willReturn(presets);
        given(bossRepository.findAllById(Arrays.asList(1L, 2L, 3L))).willReturn(Arrays.asList(testBoss1, testBoss2, testBoss3));

        // when
        List<BossPresetResponse> result = bossPresetService.getAllPresetsWithBosses();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPresetName()).isEqualTo("주간보스 세트");
        assertThat(result.get(0).getBosses()).hasSize(3);
        assertThat(result.get(0).getBosses().get(0).getBossName()).isEqualTo("자쿰");
    }




} 