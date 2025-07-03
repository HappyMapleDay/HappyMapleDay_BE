package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("ItemRepository 테스트")
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private Item testItem1;
    private Item testItem2;
    private Item testItem3;

    @BeforeEach
    void setUp() {
        // 테스트 아이템들 생성
        testItem1 = Item.builder()
                .itemName("홍옥의 보스 반지 상자")
                .isRandomBox(true)
                .build();

        testItem2 = Item.builder()
                .itemName("백옥의 보스 반지 상자")
                .isRandomBox(true)
                .build();

        testItem3 = Item.builder()
                .itemName("손상된 블랙 하트")
                .isRandomBox(false)
                .build();

        // 테스트 데이터 저장
        entityManager.persist(testItem1);
        entityManager.persist(testItem2);
        entityManager.persist(testItem3);
        entityManager.flush();
    }

    @Test
    @DisplayName("랜덤박스 아이템 조회 - 성공")
    void findByIsRandomBoxTrue_Success() {
        // when
        List<Item> randomBoxItems = itemRepository.findByIsRandomBoxTrue();

        // then
        assertThat(randomBoxItems).hasSize(2);
        assertThat(randomBoxItems)
                .extracting(Item::getItemName)
                .containsExactlyInAnyOrder("홍옥의 보스 반지 상자", "백옥의 보스 반지 상자");
        assertThat(randomBoxItems)
                .allMatch(Item::getIsRandomBox);
    }

    @Test
    @DisplayName("일반 아이템 조회 - 성공")
    void findByIsRandomBoxFalse_Success() {
        // when
        List<Item> normalItems = itemRepository.findByIsRandomBoxFalse();

        // then
        assertThat(normalItems).hasSize(1);
        assertThat(normalItems.get(0).getItemName()).isEqualTo("손상된 블랙 하트");
        assertThat(normalItems.get(0).getIsRandomBox()).isFalse();
    }

    @Test
    @DisplayName("아이템 이름으로 검색 - 부분 일치 성공")
    void findByItemNameContainingIgnoreCase_PartialMatch() {
        // when
        List<Item> foundItems = itemRepository.findByItemNameContainingIgnoreCase("보스 반지");

        // then
        assertThat(foundItems).hasSize(2);
        assertThat(foundItems)
                .extracting(Item::getItemName)
                .containsExactlyInAnyOrder("홍옥의 보스 반지 상자", "백옥의 보스 반지 상자");
    }

    @Test
    @DisplayName("아이템 이름으로 검색 - 대소문자 구분 없이 검색")
    void findByItemNameContainingIgnoreCase_CaseInsensitive() {
        // when
        List<Item> foundItems = itemRepository.findByItemNameContainingIgnoreCase("홍옥");

        // then
        assertThat(foundItems).hasSize(1);
        assertThat(foundItems.get(0).getItemName()).isEqualTo("홍옥의 보스 반지 상자");
    }

    @Test
    @DisplayName("아이템 이름으로 검색 - 검색 결과 없음")
    void findByItemNameContainingIgnoreCase_NoResults() {
        // when
        List<Item> foundItems = itemRepository.findByItemNameContainingIgnoreCase("존재하지않는아이템");

        // then
        assertThat(foundItems).isEmpty();
    }

    @Test
    @DisplayName("아이템 이름 중복 체크 - 존재하는 이름")
    void existsByItemName_ExistingName() {
        // when
        boolean exists = itemRepository.existsByItemName("홍옥의 보스 반지 상자");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("아이템 이름 중복 체크 - 존재하지 않는 이름")
    void existsByItemName_NonExistingName() {
        // when
        boolean exists = itemRepository.existsByItemName("존재하지 않는 아이템");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("빈 검색어로 아이템 검색")
    void findByItemNameContainingIgnoreCase_EmptyString() {
        // when
        List<Item> foundItems = itemRepository.findByItemNameContainingIgnoreCase("");

        // then
        assertThat(foundItems).hasSize(3); // 빈 문자열은 모든 아이템과 매치
        assertThat(foundItems)
                .extracting(Item::getItemName)
                .containsExactlyInAnyOrder("홍옥의 보스 반지 상자", "백옥의 보스 반지 상자", "손상된 블랙 하트");
    }
} 