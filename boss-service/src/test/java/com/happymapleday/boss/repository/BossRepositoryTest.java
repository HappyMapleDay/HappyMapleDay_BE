package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.ForceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class BossRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BossRepository bossRepository;

    private Boss zakum;
    private Boss lucid;
    private Boss will;

    @BeforeEach
    void setUp() {
        // 자쿰 (카오스)
        zakum = Boss.builder()
                .bossName("자쿰")
                .difficulty("카오스")
                .crystalPrice(8080000L)
                .maxPartySize(6)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(90)
                .requiredForceType(ForceType.NONE)
                .requiredForceAmount(null)
                .build();

        // 루시드 (하드)
        lucid = Boss.builder()
                .bossName("루시드")
                .difficulty("하드")
                .crystalPrice(94500000L)
                .maxPartySize(6)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(220)
                .requiredForceType(ForceType.ARCANE)
                .requiredForceAmount(360)
                .build();

        // 윌 (하드)
        will = Boss.builder()
                .bossName("윌")
                .difficulty("하드")
                .crystalPrice(116000000L)
                .maxPartySize(6)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(235)
                .requiredForceType(ForceType.ARCANE)
                .requiredForceAmount(760)
                .build();

        entityManager.persist(zakum);
        entityManager.persist(lucid);
        entityManager.persist(will);
        entityManager.flush();
    }

    @Test
    void testFindByIsActiveTrueOrderByCrystalPriceDesc() {
        // when
        List<Boss> activeBosses = bossRepository.findByIsActiveTrueOrderByCrystalPriceDesc();

        // then
        assertThat(activeBosses).hasSize(3);
        assertThat(activeBosses).containsExactly(will, lucid, zakum); // 결정석 가격 내림차순
    }

    @Test
    void testFindById() {
        // when
        Optional<Boss> foundBoss = bossRepository.findById(zakum.getId());

        // then
        assertThat(foundBoss).isPresent();
        assertThat(foundBoss.get().getBossName()).isEqualTo("자쿰");
    }

    @Test
    void testFindAll() {
        // when
        List<Boss> allBosses = bossRepository.findAll();

        // then
        assertThat(allBosses).hasSize(3);
        assertThat(allBosses).contains(zakum, lucid, will);
    }

    @Test
    void testExistsById() {
        // when
        boolean exists = bossRepository.existsById(zakum.getId());
        boolean notExists = bossRepository.existsById(999L);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testSave() {
        // given
        Boss newBoss = Boss.builder()
                .bossName("발록")
                .difficulty("이지")
                .crystalPrice(2000000L)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(50)
                .requiredForceType(ForceType.NONE)
                .build();

        // when
        Boss savedBoss = bossRepository.save(newBoss);

        // then
        assertThat(savedBoss.getId()).isNotNull();
        assertThat(savedBoss.getBossName()).isEqualTo("발록");
        assertThat(bossRepository.findAll()).hasSize(4);
    }
} 