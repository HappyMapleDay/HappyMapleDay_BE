package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.response.DesireItemResponse;
import com.happymapleday.boss.entity.*;
import com.happymapleday.boss.repository.BossDropItemRepository;
import com.happymapleday.boss.service.impl.BossDropItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("BossDropItemService 테스트")
class BossDropItemServiceTest {

    @Mock
    private BossDropItemRepository bossDropItemRepository;

    @InjectMocks
    private BossDropItemServiceImpl bossDropItemService;

    private Boss testBoss;
    private Item testItem1;
    private Item testItem2;
    private BossDropItem testDropItem1;
    private BossDropItem testDropItem2;

    @BeforeEach
    void setUp() {
        // 테스트 보스 생성
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

        // 테스트 아이템들 생성
        testItem1 = Item.builder()
                .itemName("홍옥의 보스 반지 상자")
                .isRandomBox(true)
                .build();

        testItem2 = Item.builder()
                .itemName("손상된 블랙 하트")
                .isRandomBox(false)
                .build();

        // 테스트 드랍 아이템들 생성
        testDropItem1 = BossDropItem.builder()
                .boss(testBoss)
                .item(testItem1)
                .build();

        testDropItem2 = BossDropItem.builder()
                .boss(testBoss)
                .item(testItem2)
                .build();
    }

    @Test
    @DisplayName("특정 보스의 모든 드랍 아이템 조회 - 성공")
    void getDropItemsByBossId_Success() {
        // given
        Long bossId = 1L;
        List<BossDropItem> dropItems = Arrays.asList(testDropItem1, testDropItem2);
        given(bossDropItemRepository.findByBossIdWithRandomBoxItems(bossId)).willReturn(dropItems);

        // when
        List<DesireItemResponse> result = bossDropItemService.getDropItemsByBossId(bossId);

        // then
        assertThat(result).hasSize(2);
        
        DesireItemResponse firstItem = result.get(0);
        assertThat(firstItem.getItemName()).isEqualTo("홍옥의 보스 반지 상자");
        assertThat(firstItem.getIsRandomBox()).isTrue();
        assertThat(firstItem.getBossName()).isEqualTo("자쿰");

        DesireItemResponse secondItem = result.get(1);
        assertThat(secondItem.getItemName()).isEqualTo("손상된 블랙 하트");
        assertThat(secondItem.getIsRandomBox()).isFalse();
        assertThat(secondItem.getBossName()).isEqualTo("자쿰");

        verify(bossDropItemRepository).findByBossIdWithRandomBoxItems(bossId);
    }

    @Test
    @DisplayName("특정 보스의 드랍 아이템이 없을 때 빈 리스트 반환")
    void getDropItemsByBossId_EmptyList() {
        // given
        Long bossId = 999L;
        given(bossDropItemRepository.findByBossIdWithRandomBoxItems(bossId)).willReturn(Collections.emptyList());

        // when
        List<DesireItemResponse> result = bossDropItemService.getDropItemsByBossId(bossId);

        // then
        assertThat(result).isEmpty();
        verify(bossDropItemRepository).findByBossIdWithRandomBoxItems(bossId);
    }

    @Test
    @DisplayName("랜덤박스 아이템 확인")
    void getDropItemsByBossId_RandomBoxItems() {
        // given
        Long bossId = 1L;
        List<BossDropItem> dropItems = Arrays.asList(testDropItem1); // 랜덤박스 아이템만
        given(bossDropItemRepository.findByBossIdWithRandomBoxItems(bossId)).willReturn(dropItems);

        // when
        List<DesireItemResponse> result = bossDropItemService.getDropItemsByBossId(bossId);

        // then
        assertThat(result).hasSize(1);
        DesireItemResponse randomBoxItem = result.get(0);
        assertThat(randomBoxItem.getIsRandomBox()).isTrue();
        assertThat(randomBoxItem.getItemName()).isEqualTo("홍옥의 보스 반지 상자");
    }

    @Test
    @DisplayName("일반 아이템 확인")
    void getDropItemsByBossId_NormalItems() {
        // given
        Long bossId = 1L;
        List<BossDropItem> dropItems = Arrays.asList(testDropItem2); // 일반 아이템만
        given(bossDropItemRepository.findByBossIdWithRandomBoxItems(bossId)).willReturn(dropItems);

        // when
        List<DesireItemResponse> result = bossDropItemService.getDropItemsByBossId(bossId);

        // then
        assertThat(result).hasSize(1);
        DesireItemResponse normalItem = result.get(0);
        assertThat(normalItem.getIsRandomBox()).isFalse();
        assertThat(normalItem.getItemName()).isEqualTo("손상된 블랙 하트");
    }
} 