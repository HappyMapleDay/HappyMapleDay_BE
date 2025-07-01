package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.BossPresetDto;
import com.happymapleday.boss.entity.BossPreset;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
    private BossPresetDto.CreateRequest createRequest;

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

        createRequest = BossPresetDto.CreateRequest.builder()
                .presetName("새로운 프리셋")
                .bossIds(List.of(
                    Map.of("boss_id", 1L),
                    Map.of("boss_id", 2L),
                    Map.of("boss_id", 3L)
                ))
                .build();
    }

    @Test
    @DisplayName("모든 프리셋 조회")
    void getAllPresets() {
        // given
        List<BossPreset> presets = Arrays.asList(testPreset);
        given(bossPresetRepository.findAllByOrderByCreatedAtDesc()).willReturn(presets);

        // when
        List<BossPresetDto.SimpleResponse> result = bossPresetService.getAllPresets();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPresetName()).isEqualTo("주간보스 세트");
    }

    @Test
    @DisplayName("프리셋 생성 - 성공")
    void createPreset_Success() {
        // given
        given(bossPresetRepository.existsByPresetName("새로운 프리셋")).willReturn(false);
        given(bossRepository.existsById(1L)).willReturn(true);
        given(bossRepository.existsById(2L)).willReturn(true);
        given(bossRepository.existsById(3L)).willReturn(true);
        given(bossPresetRepository.save(any(BossPreset.class))).willReturn(testPreset);

        // when
        BossPresetDto.Response result = bossPresetService.createPreset(createRequest);

        // then
        assertThat(result.getPresetName()).isEqualTo("주간보스 세트");
        verify(bossPresetRepository).save(any(BossPreset.class));
    }

    @Test
    @DisplayName("프리셋 생성 - 실패 (중복된 이름)")
    void createPreset_DuplicateName() {
        // given
        given(bossPresetRepository.existsByPresetName("새로운 프리셋")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> bossPresetService.createPreset(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 프리셋 이름입니다: 새로운 프리셋");
    }

    @Test
    @DisplayName("프리셋 생성 - 실패 (보스 개수 초과)")
    void createPreset_TooManyBosses() {
        // given
        List<Map<String, Object>> tooManyBosses = List.of(
            Map.of("boss_id", 1L), Map.of("boss_id", 2L), Map.of("boss_id", 3L), Map.of("boss_id", 4L),
            Map.of("boss_id", 5L), Map.of("boss_id", 6L), Map.of("boss_id", 7L), Map.of("boss_id", 8L),
            Map.of("boss_id", 9L), Map.of("boss_id", 10L), Map.of("boss_id", 11L), Map.of("boss_id", 12L),
            Map.of("boss_id", 13L)
        );
        BossPresetDto.CreateRequest invalidRequest = BossPresetDto.CreateRequest.builder()
                .presetName("큰 프리셋")
                .bossIds(tooManyBosses)
                .build();

        // when & then
        assertThatThrownBy(() -> bossPresetService.createPreset(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("프리셋에는 최대 12개의 보스만 포함할 수 있습니다.");
    }

    @Test
    @DisplayName("프리셋 삭제")
    void deletePreset() {
        // given
        given(bossPresetRepository.findById(1L)).willReturn(Optional.of(testPreset));

        // when
        bossPresetService.deletePreset(1L);

        // then
        verify(bossPresetRepository).delete(testPreset);
    }
} 