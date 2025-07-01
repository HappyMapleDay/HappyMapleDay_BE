package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.BossPreset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class BossPresetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BossPresetRepository bossPresetRepository;

    private BossPreset sdemyPreset;
    private BossPreset earWillPreset;

    @BeforeEach
    void setUp() {
        // 스데미 프리셋 (데미안 노말부터 아래로 12개)
        List<Map<String, Object>> sdemyBossIds = List.of(
                Map.of("boss_id", 15), Map.of("boss_id", 12), Map.of("boss_id", 4), Map.of("boss_id", 8),
                Map.of("boss_id", 2), Map.of("boss_id", 5), Map.of("boss_id", 6), Map.of("boss_id", 7),
                Map.of("boss_id", 1), Map.of("boss_id", 11), Map.of("boss_id", 9), Map.of("boss_id", 3)
        );

        sdemyPreset = BossPreset.builder()
                .presetName("스데미")
                .bossIds(sdemyBossIds)
                .build();

        // 이루윌 프리셋 (윌 이지부터 아래로 12개)
        List<Map<String, Object>> earWillBossIds = List.of(
                Map.of("boss_id", 22), Map.of("boss_id", 19), Map.of("boss_id", 17), Map.of("boss_id", 15),
                Map.of("boss_id", 12), Map.of("boss_id", 4), Map.of("boss_id", 8), Map.of("boss_id", 2),
                Map.of("boss_id", 5), Map.of("boss_id", 6), Map.of("boss_id", 7), Map.of("boss_id", 1)
        );

        earWillPreset = BossPreset.builder()
                .presetName("이루윌")
                .bossIds(earWillBossIds)
                .build();

        entityManager.persist(sdemyPreset);
        entityManager.persist(earWillPreset);
        entityManager.flush();
    }

    @Test
    void testFindByPresetName() {
        // when
        Optional<BossPreset> foundPreset = bossPresetRepository.findByPresetName("스데미");

        // then
        assertThat(foundPreset).isPresent();
        assertThat(foundPreset.get().getPresetName()).isEqualTo("스데미");
    }

    @Test
    void testFindAllByOrderByCreatedAtDesc() {
        // when
        List<BossPreset> presets = bossPresetRepository.findAllByOrderByCreatedAtDesc();

        // then
        assertThat(presets).hasSize(2);
        assertThat(presets).extracting(BossPreset::getPresetName)
                .containsExactlyInAnyOrder("스데미", "이루윌");
    }

    @Test
    void testExistsByPresetName() {
        // when
        boolean exists = bossPresetRepository.existsByPresetName("스데미");
        boolean notExists = bossPresetRepository.existsByPresetName("존재하지않는프리셋");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindPresetsContainingBoss() {
        // when - boss_id가 15인 프리셋 찾기 (데미안)
        List<BossPreset> presets = bossPresetRepository.findPresetsContainingBoss("{\"boss_id\": 15}");

        // then
        assertThat(presets).hasSize(2); // 스데미, 이루윌 둘 다 포함
        assertThat(presets).extracting(BossPreset::getPresetName)
                .containsExactlyInAnyOrder("스데미", "이루윌");
    }

    @Test
    void testFindByPresetNameContaining() {
        // when
        List<BossPreset> presets = bossPresetRepository.findByPresetNameContainingIgnoreCaseOrderByCreatedAtDesc("스");

        // then
        assertThat(presets).hasSize(1);
        assertThat(presets.get(0).getPresetName()).isEqualTo("스데미");
    }

    @Test
    void testBossIdsStructure() {
        // when
        Optional<BossPreset> preset = bossPresetRepository.findByPresetName("스데미");

        // then
        assertThat(preset).isPresent();
        List<Map<String, Object>> bossIds = preset.get().getBossIds();
        assertThat(bossIds).hasSize(12);
        assertThat(bossIds.get(0)).containsKey("boss_id");
        assertThat(bossIds.get(0).get("boss_id")).isEqualTo(15); // 첫 번째는 데미안 (boss_id: 15)
    }

    @Test
    void testExtractBossIds() {
        // when
        Optional<BossPreset> preset = bossPresetRepository.findByPresetName("스데미");

        // then
        assertThat(preset).isPresent();
        List<Long> extractedIds = preset.get().extractBossIds();
        assertThat(extractedIds).hasSize(12);
        assertThat(extractedIds).containsExactly(15L, 12L, 4L, 8L, 2L, 5L, 6L, 7L, 1L, 11L, 9L, 3L);
    }

    @Test
    void testGetBossCount() {
        // when
        Optional<BossPreset> preset = bossPresetRepository.findByPresetName("스데미");

        // then
        assertThat(preset).isPresent();
        assertThat(preset.get().getBossCount()).isEqualTo(12);
    }

    @Test
    void testCreatePresetWithCustomBossIds() {
        // given - 노듄더 프리셋 (듄켈 노말부터 아래로 12개)
        List<Map<String, Object>> customBossIds = List.of(
                Map.of("boss_id", 29), Map.of("boss_id", 25), Map.of("boss_id", 23), Map.of("boss_id", 20),
                Map.of("boss_id", 17), Map.of("boss_id", 15), Map.of("boss_id", 12), Map.of("boss_id", 4),
                Map.of("boss_id", 8), Map.of("boss_id", 2), Map.of("boss_id", 5), Map.of("boss_id", 6)
        );

        BossPreset customPreset = BossPreset.builder()
                .presetName("노듄더")
                .bossIds(customBossIds)
                .build();

        // when
        BossPreset savedPreset = bossPresetRepository.save(customPreset);

        // then
        assertThat(savedPreset.getId()).isNotNull();
        assertThat(savedPreset.getPresetName()).isEqualTo("노듄더");
        assertThat(savedPreset.getBossCount()).isEqualTo(12);
        assertThat(savedPreset.extractBossIds().get(0)).isEqualTo(29L); // 듄켈 노말
    }

    @Test
    void testDeletePreset() {
        // given
        Long presetId = sdemyPreset.getId();

        // when
        bossPresetRepository.deleteById(presetId);
        Optional<BossPreset> deletedPreset = bossPresetRepository.findById(presetId);

        // then
        assertThat(deletedPreset).isEmpty();
    }

    @Test
    void testUpdatePreset() {
        // given
        List<Map<String, Object>> updatedBossIds = List.of(
                Map.of("boss_id", 1), Map.of("boss_id", 2), Map.of("boss_id", 3)
        );

        // when
        sdemyPreset.updateBossIds(updatedBossIds);
        BossPreset updatedPreset = bossPresetRepository.save(sdemyPreset);

        // then
        assertThat(updatedPreset.getBossCount()).isEqualTo(3);
        assertThat(updatedPreset.extractBossIds()).containsExactly(1L, 2L, 3L);
    }
} 