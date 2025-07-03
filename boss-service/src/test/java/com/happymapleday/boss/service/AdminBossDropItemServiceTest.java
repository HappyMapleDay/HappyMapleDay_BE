package com.happymapleday.boss.service;

import com.happymapleday.boss.admin.dto.request.AdminBossDropItemCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminBossDropItemUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminBossDropItemResponse;
import com.happymapleday.boss.admin.service.impl.AdminBossDropItemServiceImpl;
import com.happymapleday.boss.entity.*;
import com.happymapleday.boss.repository.BossDropItemRepository;
import com.happymapleday.boss.repository.BossRepository;
import com.happymapleday.boss.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminBossDropItemService 테스트")
class AdminBossDropItemServiceTest {

    @Mock
    private BossDropItemRepository bossDropItemRepository;

    @Mock
    private BossRepository bossRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private AdminBossDropItemServiceImpl adminBossDropItemService;

    private Boss testBoss;
    private Item testItem;
    private BossDropItem testDropItem;
    private AdminBossDropItemCreateRequest createRequest;
    private AdminBossDropItemUpdateRequest updateRequest;

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
                .bossLevel(180)
                .requiredForceType(ForceType.NONE)
                .requiredForceAmount(null)
                .build();

        // 테스트 아이템 생성
        testItem = Item.builder()
                .itemName("홍옥의 보스 반지 상자")
                .isRandomBox(true)
                .build();

        // 테스트 드랍 아이템 생성
        testDropItem = BossDropItem.builder()
                .boss(testBoss)
                .item(testItem)
                .build();

        // 테스트 요청 DTO들 생성
        createRequest = new AdminBossDropItemCreateRequest();
        createRequest.setBossId(1L);
        createRequest.setItemId(1L);

        updateRequest = new AdminBossDropItemUpdateRequest();
        updateRequest.setBossId(1L);
        updateRequest.setItemId(2L);
    }

    @Test
    @DisplayName("모든 보스 드랍 아이템 조회 - 성공")
    void getAllBossDropItems_Success() {
        // given
        List<BossDropItem> dropItems = Arrays.asList(testDropItem);
        given(bossDropItemRepository.findAll()).willReturn(dropItems);

        // when
        List<AdminBossDropItemResponse> result = adminBossDropItemService.getAllBossDropItems();

        // then
        assertThat(result).hasSize(1);
        verify(bossDropItemRepository).findAll();
    }

    @Test
    @DisplayName("페이징된 모든 보스 드랍 아이템 조회 - 성공")
    void getAllBossDropItemsWithPaging_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<BossDropItem> dropItems = Arrays.asList(testDropItem);
        Page<BossDropItem> dropItemPage = new PageImpl<>(dropItems, pageable, 1);
        given(bossDropItemRepository.findAll(pageable)).willReturn(dropItemPage);

        // when
        Page<AdminBossDropItemResponse> result = adminBossDropItemService.getAllBossDropItems(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(bossDropItemRepository).findAll(pageable);
    }

    @Test
    @DisplayName("특정 보스 드랍 아이템 조회 - 성공")
    void getBossDropItem_Success() {
        // given
        Long dropItemId = 1L;
        given(bossDropItemRepository.findById(dropItemId)).willReturn(Optional.of(testDropItem));

        // when
        AdminBossDropItemResponse result = adminBossDropItemService.getBossDropItem(dropItemId);

        // then
        assertThat(result).isNotNull();
        verify(bossDropItemRepository).findById(dropItemId);
    }

    @Test
    @DisplayName("특정 보스 드랍 아이템 조회 - 존재하지 않는 아이템")
    void getBossDropItem_NotFound() {
        // given
        Long dropItemId = 999L;
        given(bossDropItemRepository.findById(dropItemId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminBossDropItemService.getBossDropItem(dropItemId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 보스 드랍 아이템입니다. ID: " + dropItemId);

        verify(bossDropItemRepository).findById(dropItemId);
    }

    @Test
    @DisplayName("특정 보스의 드랍 아이템 조회 - 성공")
    void getBossDropItemsByBoss_Success() {
        // given
        Long bossId = 1L;
        List<BossDropItem> dropItems = Arrays.asList(testDropItem);
        given(bossDropItemRepository.findByBossIdWithRandomBoxItems(bossId)).willReturn(dropItems);

        // when
        List<AdminBossDropItemResponse> result = adminBossDropItemService.getBossDropItemsByBoss(bossId);

        // then
        assertThat(result).hasSize(1);
        verify(bossDropItemRepository).findByBossIdWithRandomBoxItems(bossId);
    }

    @Test
    @DisplayName("보스 드랍 아이템 생성 - 기존 아이템 사용")
    void createBossDropItem_WithExistingItem() {
        // given
        given(bossRepository.findById(1L)).willReturn(Optional.of(testBoss));
        given(itemRepository.findById(1L)).willReturn(Optional.of(testItem));
        given(bossDropItemRepository.save(any(BossDropItem.class))).willReturn(testDropItem);

        // when
        AdminBossDropItemResponse result = adminBossDropItemService.createBossDropItem(createRequest);

        // then
        assertThat(result).isNotNull();
        verify(bossRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verify(bossDropItemRepository).save(any(BossDropItem.class));
    }

    @Test
    @DisplayName("보스 드랍 아이템 생성 - 보스가 존재하지 않음")
    void createBossDropItem_BossNotFound() {
        // given
        given(bossRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminBossDropItemService.createBossDropItem(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 보스입니다. ID: 1");

        verify(bossRepository).findById(1L);
        verify(itemRepository, never()).findById(any());
        verify(bossDropItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("보스 드랍 아이템 생성 - 기존 아이템이 존재하지 않음")
    void createBossDropItem_ItemNotFound() {
        // given
        given(bossRepository.findById(1L)).willReturn(Optional.of(testBoss));
        given(itemRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminBossDropItemService.createBossDropItem(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 아이템입니다. ID: 1");

        verify(bossRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verify(bossDropItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("보스 드랍 아이템 수정 - 성공")
    void updateBossDropItem_Success() {
        // given
        Long dropItemId = 1L;
        Item newItem = Item.builder()
                .itemName("수정된 아이템")
                .isRandomBox(false)
                .build();

        given(bossDropItemRepository.findById(dropItemId)).willReturn(Optional.of(testDropItem));
        given(bossRepository.findById(1L)).willReturn(Optional.of(testBoss));
        given(itemRepository.findById(2L)).willReturn(Optional.of(newItem));
        given(bossDropItemRepository.save(any(BossDropItem.class))).willReturn(testDropItem);

        // when
        AdminBossDropItemResponse result = adminBossDropItemService.updateBossDropItem(dropItemId, updateRequest);

        // then
        assertThat(result).isNotNull();
        verify(bossDropItemRepository).findById(dropItemId);
        verify(bossRepository).findById(1L);
        verify(itemRepository).findById(2L);
        verify(bossDropItemRepository).delete(testDropItem);
        verify(bossDropItemRepository).save(any(BossDropItem.class));
    }

    @Test
    @DisplayName("보스 드랍 아이템 삭제 - 성공")
    void deleteBossDropItem_Success() {
        // given
        Long dropItemId = 1L;
        given(bossDropItemRepository.findById(dropItemId)).willReturn(Optional.of(testDropItem));

        // when
        adminBossDropItemService.deleteBossDropItem(dropItemId);

        // then
        verify(bossDropItemRepository).findById(dropItemId);
        verify(bossDropItemRepository).delete(testDropItem);
    }

    @Test
    @DisplayName("보스 드랍 아이템 삭제 - 존재하지 않는 아이템")
    void deleteBossDropItem_NotFound() {
        // given
        Long dropItemId = 999L;
        given(bossDropItemRepository.findById(dropItemId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminBossDropItemService.deleteBossDropItem(dropItemId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 보스 드랍 아이템입니다. ID: " + dropItemId);

        verify(bossDropItemRepository).findById(dropItemId);
        verify(bossDropItemRepository, never()).delete(any());
    }
} 