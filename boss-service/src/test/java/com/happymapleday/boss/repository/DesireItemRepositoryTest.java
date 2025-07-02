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
    private Boss lucid;
    private DesireItem randomBoxItem;
    private DesireItem directDropItem;
    private DesireItem lucidRandomBox;
    private DesireItem lucidDirectDrop;

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
                .bossLevel(210)
                .requiredForceType(ForceType.NONE)
                .requiredForceAmount(null)
                .build();

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

        entityManager.persist(suu);
        entityManager.persist(lucid);

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

        lucidRandomBox = DesireItem.builder()
                .boss(lucid)
                .itemName("홍옥의 보스 반지 상자")
                .isRandomBox(true)
                .build();

        lucidDirectDrop = DesireItem.builder()
                .boss(lucid)
                .itemName("몽환의 벨트")
                .isRandomBox(false)
                .build();

        entityManager.persist(randomBoxItem);
        entityManager.persist(directDropItem);
        entityManager.persist(lucidRandomBox);
        entityManager.persist(lucidDirectDrop);
        entityManager.flush();
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
    void testFindByBossIdOrderByItemName() {
        // when
        List<DesireItem> suuItems = desireItemRepository.findByBossIdOrderByItemName(suu.getId());

        // then
        assertThat(suuItems).hasSize(2);
        assertThat(suuItems).extracting(DesireItem::getItemName)
                .containsExactlyInAnyOrder("홍옥의 보스 반지 상자", "손상된 블랙 하트");
    }

    @Test
    void testFindByBossAndIsRandomBoxOrderByItemName() {
        // when
        List<DesireItem> suuRandomBoxes = desireItemRepository.findByBossAndIsRandomBoxOrderByItemName(suu, true);
        List<DesireItem> suuDirectDrops = desireItemRepository.findByBossAndIsRandomBoxOrderByItemName(suu, false);

        // then
        assertThat(suuRandomBoxes).hasSize(1);
        assertThat(suuRandomBoxes.get(0).getItemName()).isEqualTo("홍옥의 보스 반지 상자");
        
        assertThat(suuDirectDrops).hasSize(1);
        assertThat(suuDirectDrops.get(0).getItemName()).isEqualTo("손상된 블랙 하트");
    }

    @Test
    void testFindByItemNameContainingIgnoreCaseOrderByItemName() {
        // when
        List<DesireItem> ringBoxItems = desireItemRepository.findByItemNameContainingIgnoreCaseOrderByItemName("반지 상자");

        // then
        assertThat(ringBoxItems).hasSize(2);
        assertThat(ringBoxItems).contains(randomBoxItem, lucidRandomBox);
    }

    @Test
    void testFindByBossAndItemName() {
        // when
        Optional<DesireItem> foundItem = desireItemRepository.findByBossAndItemName(suu, "손상된 블랙 하트");

        // then
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getItemName()).isEqualTo("손상된 블랙 하트");
        assertThat(foundItem.get().getIsRandomBox()).isFalse();
    }

    @Test
    void testFindByBossIdAndItemName() {
        // when
        Optional<DesireItem> foundItem = desireItemRepository.findByBossIdAndItemName(lucid.getId(), "몽환의 벨트");

        // then
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getItemName()).isEqualTo("몽환의 벨트");
        assertThat(foundItem.get().getBoss().getBossName()).isEqualTo("루시드");
    }

    @Test
    void testFindRandomBoxItemsByBossId() {
        // when
        List<DesireItem> randomBoxItems = desireItemRepository.findRandomBoxItemsByBossId(suu.getId());

        // then
        assertThat(randomBoxItems).hasSize(1);
        assertThat(randomBoxItems.get(0).getItemName()).isEqualTo("홍옥의 보스 반지 상자");
        assertThat(randomBoxItems.get(0).getIsRandomBox()).isTrue();
    }

    @Test
    void testFindNormalDesireItemsByBossId() {
        // when
        List<DesireItem> normalItems = desireItemRepository.findNormalDesireItemsByBossId(lucid.getId());

        // then
        assertThat(normalItems).hasSize(1);
        assertThat(normalItems.get(0).getItemName()).isEqualTo("몽환의 벨트");
        assertThat(normalItems.get(0).getIsRandomBox()).isFalse();
    }

    @Test
    void testCountByBoss() {
        // when
        long suuItemCount = desireItemRepository.countByBoss(suu);
        long lucidItemCount = desireItemRepository.countByBoss(lucid);

        // then
        assertThat(suuItemCount).isEqualTo(2);
        assertThat(lucidItemCount).isEqualTo(2);
    }

    @Test
    void testCreateNewDesireItem() {
        // given - 윌 하드의 새로운 물욕템
        Boss will = Boss.builder()
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
        entityManager.persist(will);

        DesireItem willItem = DesireItem.builder()
                .boss(will)
                .itemName("저주받은 마도서 선택 상자")
                .isRandomBox(false)
                .build();

        // when
        DesireItem savedItem = desireItemRepository.save(willItem);

        // then
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getItemName()).isEqualTo("저주받은 마도서 선택 상자");
        assertThat(savedItem.getIsRandomBox()).isFalse();
        assertThat(savedItem.getBoss().getBossName()).isEqualTo("윌");
    }

    @Test
    void testDeleteDesireItem() {
        // given
        Long itemId = randomBoxItem.getId();

        // when
        desireItemRepository.deleteById(itemId);
        Optional<DesireItem> deletedItem = desireItemRepository.findById(itemId);

        // then
        assertThat(deletedItem).isEmpty();
    }
} 