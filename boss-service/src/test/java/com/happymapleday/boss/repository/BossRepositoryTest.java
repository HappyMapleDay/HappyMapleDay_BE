package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.ForceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

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
    private Boss seren;

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
                .bossLevel(180)
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
                .bossLevel(230)
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
                .bossLevel(250)
                .requiredForceType(ForceType.ARCANE)
                .requiredForceAmount(760)
                .build();

        // 선택받은 세렌 (하드)
        seren = Boss.builder()
                .bossName("선택받은 세렌")
                .difficulty("하드")
                .crystalPrice(440000000L)
                .maxPartySize(6)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(260)
                .bossLevel(275)
                .requiredForceType(ForceType.AUTHENTIC)
                .requiredForceAmount(200)
                .build();

        entityManager.persist(zakum);
        entityManager.persist(lucid);
        entityManager.persist(will);
        entityManager.persist(seren);
        entityManager.flush();
    }

    @Test
    void testFindByIsActive() {
        // when
        List<Boss> activeBosses = bossRepository.findByIsActiveTrueOrderByCrystalPriceDesc();

        // then
        assertThat(activeBosses).hasSize(4);
        assertThat(activeBosses).contains(zakum, lucid, will, seren);
    }

    @Test
    void testFindByBossNameContaining() {
        // when
        List<Boss> foundBosses = bossRepository.findByBossNameContainingIgnoreCaseAndIsActiveTrue("루시드");

        // then
        assertThat(foundBosses).hasSize(1);
        assertThat(foundBosses.get(0).getBossName()).isEqualTo("루시드");
    }

    @Test
    void testFindByDifficulty() {
        // when
        List<Boss> hardBosses = bossRepository.findByDifficultyAndIsActiveTrueOrderByCrystalPriceDesc("하드");

        // then
        assertThat(hardBosses).hasSize(3);
        assertThat(hardBosses).contains(lucid, will, seren);
    }

    @Test
    void testFindByRequiredForceType() {
        // when
        List<Boss> arcaneBosses = bossRepository.findByRequiredForceTypeAndIsActiveTrueOrderByCrystalPriceDesc(ForceType.ARCANE);

        // then
        assertThat(arcaneBosses).hasSize(2);
        assertThat(arcaneBosses).contains(lucid, will);
    }

    @Test
    void testFindByCrystalPriceBetween() {
        // when - 5천만 ~ 1억 5천만 메소
        List<Boss> bosses = bossRepository.findByPriceRange(50000000L, 150000000L);

        // then
        assertThat(bosses).hasSize(2);
        assertThat(bosses).contains(lucid, will);
    }

    @Test
    void testFindByCharacterConditions() {
        // when - 레벨 250, 아케인포스 800인 캐릭터가 갈 수 있는 보스
        List<Boss> accessibleBosses = bossRepository.findBossesForCharacterLevel(250);

        // then - 세렌은 입장 레벨이 260이므로 레벨 250으로는 입장 불가
        assertThat(accessibleBosses).hasSize(3); // 자쿰(90), 루시드(220), 윌(235)
        assertThat(accessibleBosses).contains(zakum, lucid, will);
        assertThat(accessibleBosses).doesNotContain(seren); // 세렌은 입장 레벨 260
    }

    @Test
    void testFindBossesForForceCondition() {
        // when - 아케인포스 800, 어센틱포스 300인 캐릭터
        List<Boss> accessibleBosses = bossRepository.findBossesForForceCondition(800, 300);

        // then
        assertThat(accessibleBosses).hasSize(4); // 자쿰(NONE), 루시드(ARCANE 360), 윌(ARCANE 760), 세렌(AUTHENTIC 200)
        assertThat(accessibleBosses).contains(zakum, lucid, will, seren);
    }

    @Test
    void testFindWithPaging() {
        // when
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<Boss> bossPage = bossRepository.findByIsActiveTrueOrderByCrystalPriceDesc(pageRequest);

        // then
        assertThat(bossPage.getContent()).hasSize(2);
        assertThat(bossPage.getTotalElements()).isEqualTo(4);
        assertThat(bossPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    void testFindByIsMonthly() {
        // given - 월간 보스 추가
        Boss blackMage = Boss.builder()
                .bossName("검은 마법사")
                .difficulty("하드")
                .crystalPrice(1000000000L)
                .maxPartySize(6)
                .isMonthly(true)
                .isActive(true)
                .minEntryLevel(255)
                .bossLevel(275)
                .requiredForceType(ForceType.ARCANE)
                .requiredForceAmount(1320)
                .build();
        entityManager.persist(blackMage);
        entityManager.flush();

        // when
        List<Boss> monthlyBosses = bossRepository.findByIsMonthlyAndIsActiveTrueOrderByCrystalPriceDesc(true);

        // then
        assertThat(monthlyBosses).hasSize(1);
        assertThat(monthlyBosses.get(0).getBossName()).isEqualTo("검은 마법사");
    }

    @Test
    void testFindByBossNameAndDifficulty() {
        // when
        var foundBoss = bossRepository.findByBossNameAndDifficultyAndIsActiveTrue("루시드", "하드");

        // then
        assertThat(foundBoss).isPresent();
        assertThat(foundBoss.get().getBossName()).isEqualTo("루시드");
        assertThat(foundBoss.get().getDifficulty()).isEqualTo("하드");
    }

    @Test
    void testExistsByBossNameAndDifficulty() {
        // when
        boolean exists = bossRepository.existsByBossNameAndDifficultyAndIsActiveTrue("자쿰", "카오스");

        // then
        assertThat(exists).isTrue();
    }
} 