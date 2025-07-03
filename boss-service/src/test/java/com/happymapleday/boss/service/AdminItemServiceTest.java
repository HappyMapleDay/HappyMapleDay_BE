package com.happymapleday.boss.service;

import com.happymapleday.boss.admin.dto.request.AdminItemCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminItemUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminItemResponse;
import com.happymapleday.boss.admin.service.impl.AdminItemServiceImpl;
import com.happymapleday.boss.entity.Item;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminItemService 테스트")
class AdminItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private AdminItemServiceImpl adminItemService;

    private Item testItem1;
    private Item testItem2;
    private AdminItemCreateRequest createRequest;
    private AdminItemUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        // 테스트 아이템들 생성
        testItem1 = Item.builder()
                .itemName("테스트 아이템 1")
                .isRandomBox(true)
                .build();

        testItem2 = Item.builder()
                .itemName("테스트 아이템 2")
                .isRandomBox(false)
                .build();

        // 테스트 요청 DTO 생성
        createRequest = new AdminItemCreateRequest("새로운 아이템", false);
        updateRequest = new AdminItemUpdateRequest("수정된 아이템", true);
    }

    @Test
    @DisplayName("모든 아이템 조회 - 성공")
    void getAllItems_Success() {
        // given
        List<Item> items = Arrays.asList(testItem1, testItem2);
        given(itemRepository.findAll()).willReturn(items);

        // when
        List<AdminItemResponse> result = adminItemService.getAllItems();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getItemName()).isEqualTo("테스트 아이템 1");
        assertThat(result.get(0).getIsRandomBox()).isTrue();
        assertThat(result.get(1).getItemName()).isEqualTo("테스트 아이템 2");
        assertThat(result.get(1).getIsRandomBox()).isFalse();
        
        verify(itemRepository).findAll();
    }

    @Test
    @DisplayName("페이징된 모든 아이템 조회 - 성공")
    void getAllItemsWithPaging_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = Arrays.asList(testItem1, testItem2);
        Page<Item> itemPage = new PageImpl<>(items, pageable, 2);
        given(itemRepository.findAll(pageable)).willReturn(itemPage);

        // when
        Page<AdminItemResponse> result = adminItemService.getAllItems(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getItemName()).isEqualTo("테스트 아이템 1");
        
        verify(itemRepository).findAll(pageable);
    }

    @Test
    @DisplayName("특정 아이템 조회 - 성공")
    void getItem_Success() {
        // given
        Long itemId = 1L;
        given(itemRepository.findById(itemId)).willReturn(Optional.of(testItem1));

        // when
        AdminItemResponse result = adminItemService.getItem(itemId);

        // then
        assertThat(result.getItemName()).isEqualTo("테스트 아이템 1");
        assertThat(result.getIsRandomBox()).isTrue();
        
        verify(itemRepository).findById(itemId);
    }

    @Test
    @DisplayName("특정 아이템 조회 - 존재하지 않는 아이템")
    void getItem_NotFound() {
        // given
        Long itemId = 999L;
        given(itemRepository.findById(itemId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminItemService.getItem(itemId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 아이템입니다. ID: " + itemId);
        
        verify(itemRepository).findById(itemId);
    }

    @Test
    @DisplayName("아이템 이름으로 검색 - 성공")
    void searchItemsByName_Success() {
        // given
        String searchName = "테스트";
        List<Item> items = Arrays.asList(testItem1, testItem2);
        given(itemRepository.findByItemNameContainingIgnoreCase(searchName)).willReturn(items);

        // when
        List<AdminItemResponse> result = adminItemService.searchItemsByName(searchName);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getItemName()).contains("테스트");
        assertThat(result.get(1).getItemName()).contains("테스트");
        
        verify(itemRepository).findByItemNameContainingIgnoreCase(searchName);
    }

    @Test
    @DisplayName("아이템 이름으로 검색 - 결과 없음")
    void searchItemsByName_EmptyResult() {
        // given
        String searchName = "존재하지않는아이템";
        given(itemRepository.findByItemNameContainingIgnoreCase(searchName)).willReturn(Collections.emptyList());

        // when
        List<AdminItemResponse> result = adminItemService.searchItemsByName(searchName);

        // then
        assertThat(result).isEmpty();
        
        verify(itemRepository).findByItemNameContainingIgnoreCase(searchName);
    }

    @Test
    @DisplayName("아이템 생성 - 성공")
    void createItem_Success() {
        // given
        given(itemRepository.existsByItemName(createRequest.getItemName())).willReturn(false);
        given(itemRepository.save(any(Item.class))).willReturn(testItem1);

        // when
        AdminItemResponse result = adminItemService.createItem(createRequest);

        // then
        assertThat(result.getItemName()).isEqualTo("테스트 아이템 1");
        assertThat(result.getIsRandomBox()).isTrue();
        
        verify(itemRepository).existsByItemName(createRequest.getItemName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("아이템 생성 - 중복된 이름")
    void createItem_DuplicateName() {
        // given
        given(itemRepository.existsByItemName(createRequest.getItemName())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> adminItemService.createItem(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 아이템 이름입니다: " + createRequest.getItemName());
        
        verify(itemRepository).existsByItemName(createRequest.getItemName());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("아이템 수정 - 성공")
    void updateItem_Success() {
        // given
        Long itemId = 1L;
        given(itemRepository.findById(itemId)).willReturn(Optional.of(testItem1));
        given(itemRepository.existsByItemName(updateRequest.getItemName())).willReturn(false);
        given(itemRepository.save(any(Item.class))).willReturn(testItem2);

        // when
        AdminItemResponse result = adminItemService.updateItem(itemId, updateRequest);

        // then
        assertThat(result.getItemName()).isEqualTo("테스트 아이템 2");
        assertThat(result.getIsRandomBox()).isFalse();
        
        verify(itemRepository).findById(itemId);
        verify(itemRepository).existsByItemName(updateRequest.getItemName());
        verify(itemRepository).delete(testItem1);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("아이템 수정 - 존재하지 않는 아이템")
    void updateItem_NotFound() {
        // given
        Long itemId = 999L;
        given(itemRepository.findById(itemId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminItemService.updateItem(itemId, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 아이템입니다. ID: " + itemId);
        
        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).delete(any(Item.class));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("아이템 수정 - 중복된 이름 (다른 아이템과)")
    void updateItem_DuplicateName() {
        // given
        Long itemId = 1L;
        testItem1 = Item.builder()
                .itemName("기존 아이템")
                .isRandomBox(false)
                .build();
        updateRequest = new AdminItemUpdateRequest("중복된 이름", true);
        
        given(itemRepository.findById(itemId)).willReturn(Optional.of(testItem1));
        given(itemRepository.existsByItemName(updateRequest.getItemName())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> adminItemService.updateItem(itemId, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 아이템 이름입니다: " + updateRequest.getItemName());
        
        verify(itemRepository).findById(itemId);
        verify(itemRepository).existsByItemName(updateRequest.getItemName());
        verify(itemRepository, never()).delete(any(Item.class));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("아이템 수정 - 같은 이름으로 수정 (본인 이름)")
    void updateItem_SameName() {
        // given
        Long itemId = 1L;
        updateRequest = new AdminItemUpdateRequest("테스트 아이템 1", false);
        given(itemRepository.findById(itemId)).willReturn(Optional.of(testItem1));
        given(itemRepository.save(any(Item.class))).willReturn(testItem2);

        // when
        AdminItemResponse result = adminItemService.updateItem(itemId, updateRequest);

        // then
        assertThat(result.getItemName()).isEqualTo("테스트 아이템 2");
        
        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).existsByItemName(any(String.class)); // 같은 이름이므로 중복 체크 건너뜀
        verify(itemRepository).delete(testItem1);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("아이템 삭제 - 성공")
    void deleteItem_Success() {
        // given
        Long itemId = 1L;
        given(itemRepository.findById(itemId)).willReturn(Optional.of(testItem1));

        // when
        adminItemService.deleteItem(itemId);

        // then
        verify(itemRepository).findById(itemId);
        verify(itemRepository).delete(testItem1);
    }

    @Test
    @DisplayName("아이템 삭제 - 존재하지 않는 아이템")
    void deleteItem_NotFound() {
        // given
        Long itemId = 999L;
        given(itemRepository.findById(itemId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminItemService.deleteItem(itemId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 아이템입니다. ID: " + itemId);
        
        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).delete(any(Item.class));
    }
} 