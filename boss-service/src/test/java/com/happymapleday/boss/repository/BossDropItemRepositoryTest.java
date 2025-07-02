package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never"
})
@DisplayName("BossDropItemRepository 테스트")
class BossDropItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BossDropItemRepository bossDropItemRepository;

    private Boss testBoss;
    private Item testItem1;
    private Item testItem2;
    private BossDropItem testDropItem1;
    private BossDropItem testDropItem2;

    @BeforeEach
    void setUp() {
        // 테스트 보스 생성 및 저장
        testBoss = Boss.builder()
                .bossName("자쿰")
                .difficulty("카오스")
                .crystalPrice(8080000L)
                .maxPartySize(6)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(90)
                .bossLevel(90)
                .requiredForceType(ForceType.NONE)
                .requiredForceAmount(0)
                .build();
        entityManager.persistAndFlush(testBoss);

        // 테스트 아이템들 생성 및 저장
        testItem1 = Item.builder()
                .itemName("홍옥의 보스 반지 상자")
                .isRandomBox(true)
                .build();
        entityManager.persistAndFlush(testItem1);

        testItem2 = Item.builder()
                .itemName("손상된 블랙 하트")
                .isRandomBox(false)
                .build();
        entityManager.persistAndFlush(testItem2);

        // 테스트 드랍 아이템들 생성 및 저장
        testDropItem1 = BossDropItem.builder()
                .boss(testBoss)
                .item(testItem1)
                .build();
        entityManager.persistAndFlush(testDropItem1);

        testDropItem2 = BossDropItem.builder()
                .boss(testBoss)
                .item(testItem2)
                .build();
        entityManager.persistAndFlush(testDropItem2);
    }

    @Test
    @DisplayName("보스 ID로 드랍 아이템 조회")
    void findByBoss_IdOrderByItem_ItemName() {
        // when
        List<BossDropItem> result = bossDropItemRepository.findByBoss_IdOrderByItem_ItemName(testBoss.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getItem().getItemName()).isEqualTo("손상된 블랙 하트"); // 이름순 정렬
        assertThat(result.get(1).getItem().getItemName()).isEqualTo("홍옥의 보스 반지 상자");
    }

    @Test
    @DisplayName("보스와 아이템으로 드랍 아이템 조회")
    void findByBossAndItem() {
        // when
        Optional<BossDropItem> result = bossDropItemRepository.findByBossAndItem(testBoss, testItem1);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getBoss().getBossName()).isEqualTo("자쿰");
        assertThat(result.get().getItem().getItemName()).isEqualTo("홍옥의 보스 반지 상자");
    }

    @Test
    @DisplayName("보스 ID와 아이템 ID로 드랍 아이템 조회")
    void findByBoss_IdAndItem_Id() {
        // when
        Optional<BossDropItem> result = bossDropItemRepository.findByBoss_IdAndItem_Id(testBoss.getId(), testItem1.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getBoss().getBossName()).isEqualTo("자쿰");
        assertThat(result.get().getItem().getItemName()).isEqualTo("홍옥의 보스 반지 상자");
    }

    @Test
    @DisplayName("보스의 드랍 아이템 개수 조회")
    void countByBoss() {
        // when
        long count = bossDropItemRepository.countByBoss(testBoss);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("랜덤박스 아이템만 조회")
    void findRandomBoxItemsByBossId() {
        // when
        List<BossDropItem> result = bossDropItemRepository.findRandomBoxItemsByBossId(testBoss.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItem().getItemName()).isEqualTo("홍옥의 보스 반지 상자");
        assertThat(result.get(0).getItem().getIsRandomBox()).isTrue();
    }

    @Test
    @DisplayName("일반 드랍 아이템만 조회")
    void findNormalDropItemsByBossId() {
        // when
        List<BossDropItem> result = bossDropItemRepository.findNormalDropItemsByBossId(testBoss.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItem().getItemName()).isEqualTo("손상된 블랙 하트");
        assertThat(result.get(0).getItem().getIsRandomBox()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 보스 ID로 조회시 빈 결과 반환")
    void findByBoss_IdOrderByItem_ItemName_NotFound() {
        // when
        List<BossDropItem> result = bossDropItemRepository.findByBoss_IdOrderByItem_ItemName(999L);

        // then
        assertThat(result).isEmpty();
    }
} 