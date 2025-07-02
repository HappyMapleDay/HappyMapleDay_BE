package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.DesireItem;
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
public class DesireItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DesireItemRepository desireItemRepository;

    private Boss suu;
    private DesireItem randomBoxItem;
    private DesireItem directDropItem;

    @BeforeEach
    void setUp() {
        // 보스 데이터 설정
        suu = Boss.builder()
                .bossName("스우")
                .difficulty("하드")
                .crystalPrice(77400000L)
                .maxPartySize(6)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(190)
                .requiredForceType(ForceType.NONE)
                .build();

        entityManager.persist(suu);

        // 물욕템 데이터 설정
        randomBoxItem = DesireItem.builder()
                .boss(suu)
                .itemName("홍옥의 보스 반지 상자")
                .isRandomBox(true)
                .build();

        directDropItem = DesireItem.builder()
                .boss(suu)
                .itemName("손상된 블랙 하트")
                .isRandomBox(false)
                .build();

        entityManager.persist(randomBoxItem);
        entityManager.persist(directDropItem);
        entityManager.flush();
    }

    @Test
    void testFindByBossIdOrderByItemName() {
        // when
        List<DesireItem> suuItems = desireItemRepository.findByBossIdOrderByItemName(suu.getId());

        // then
        assertThat(suuItems).hasSize(2);
        assertThat(suuItems).extracting(DesireItem::getItemName)
                .containsExactlyInAnyOrder("홍옥의 보스 반지 상자", "손상된 블랙 하트");
    }

    @Test
    void testFindByBossOrderByItemName() {
        // when
        List<DesireItem> suuItems = desireItemRepository.findByBossOrderByItemName(suu);

        // then
        assertThat(suuItems).hasSize(2);
        assertThat(suuItems).contains(randomBoxItem, directDropItem);
    }

    @Test
    void testFindById() {
        // when
        Optional<DesireItem> foundItem = desireItemRepository.findById(randomBoxItem.getId());

        // then
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getItemName()).isEqualTo("홍옥의 보스 반지 상자");
    }

    @Test
    void testSave() {
        // given
        DesireItem newItem = DesireItem.builder()
                .boss(suu)
                .itemName("새로운 물욕템")
                .isRandomBox(false)
                .build();

        // when
        DesireItem savedItem = desireItemRepository.save(newItem);

        // then
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getItemName()).isEqualTo("새로운 물욕템");
        assertThat(desireItemRepository.findByBossOrderByItemName(suu)).hasSize(3);
    }

    @Test
    void testCountByBoss() {
        // when
        long suuItemCount = desireItemRepository.countByBoss(suu);

        // then
        assertThat(suuItemCount).isEqualTo(2);
    }
} 