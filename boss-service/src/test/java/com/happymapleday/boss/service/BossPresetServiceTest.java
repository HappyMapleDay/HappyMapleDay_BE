package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.response.BossPresetResponse;
import com.happymapleday.boss.dto.request.ValidateLimitsRequest;
import com.happymapleday.boss.dto.response.ValidateLimitsResponse;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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



    @Test
    @DisplayName("보스 선택 제한 검증 - 성공")
    void validateLimits_Success() {
        // given
        List<ValidateLimitsRequest.SelectedBoss> selectedBosses = Arrays.asList(
            ValidateLimitsRequest.SelectedBoss.builder()
                .characterId(1L)
                .bossId(1L)
                .build(),
            ValidateLimitsRequest.SelectedBoss.builder()
                .characterId(1L)
                .bossId(2L)
                .build()
        );

        ValidateLimitsRequest request = ValidateLimitsRequest.builder()
            .userId(1L)
            .selectedBosses(selectedBosses)
            .build();

        // when
        ValidateLimitsResponse result = bossPresetService.validateLimits(request);

        // then
        assertThat(result.getIsValid()).isTrue();
        assertThat(result.getViolations()).isEmpty();
        assertThat(result.getCharacterLimitStatus()).hasSize(1);
        assertThat(result.getServerLimitStatus().getCurrent()).isEqualTo(2);
        assertThat(result.getServerLimitStatus().getLimit()).isEqualTo(90);
    }

    @Test
    @DisplayName("보스 선택 제한 검증 - 캐릭터 제한 초과")
    void validateLimits_CharacterLimitExceeded() {
        // given - 캐릭터 1명이 13개 보스 선택
        List<ValidateLimitsRequest.SelectedBoss> selectedBosses = new java.util.ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            selectedBosses.add(ValidateLimitsRequest.SelectedBoss.builder()
                .characterId(1L)
                .bossId((long) i)
                .build());
        }

        ValidateLimitsRequest request = ValidateLimitsRequest.builder()
            .userId(1L)
            .selectedBosses(selectedBosses)
            .build();

        // when
        ValidateLimitsResponse result = bossPresetService.validateLimits(request);

        // then
        assertThat(result.getIsValid()).isFalse();
        assertThat(result.getViolations()).isNotEmpty();
        assertThat(result.getCharacterLimitStatus().get("1").getCurrent()).isEqualTo(13);
        assertThat(result.getCharacterLimitStatus().get("1").getLimit()).isEqualTo(12);
    }

    @Test
    @DisplayName("보스 선택 제한 검증 - 서버 제한 초과")
    void validateLimits_ServerLimitExceeded() {
        // given - 서버 전체 91개 보스 선택
        List<ValidateLimitsRequest.SelectedBoss> selectedBosses = new java.util.ArrayList<>();
        for (int i = 1; i <= 91; i++) {
            selectedBosses.add(ValidateLimitsRequest.SelectedBoss.builder()
                .characterId((long) ((i - 1) / 12 + 1)) // 캐릭터당 12개씩 배분
                .bossId((long) i)
                .build());
        }

        ValidateLimitsRequest request = ValidateLimitsRequest.builder()
            .userId(1L)
            .selectedBosses(selectedBosses)
            .build();

        // when
        ValidateLimitsResponse result = bossPresetService.validateLimits(request);

        // then
        assertThat(result.getIsValid()).isFalse();
        assertThat(result.getViolations()).isNotEmpty();
        assertThat(result.getServerLimitStatus().getCurrent()).isEqualTo(91);
        assertThat(result.getServerLimitStatus().getLimit()).isEqualTo(90);
    }
} 