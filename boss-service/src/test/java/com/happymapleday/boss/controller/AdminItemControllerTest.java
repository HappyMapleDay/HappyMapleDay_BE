package com.happymapleday.boss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.boss.admin.controller.AdminItemController;
import com.happymapleday.boss.admin.dto.request.AdminItemCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminItemUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminItemResponse;
import com.happymapleday.boss.admin.service.AdminItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminItemController.class)
@DisplayName("AdminItemController 테스트")
class AdminItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminItemService adminItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminItemResponse testResponse1;
    private AdminItemResponse testResponse2;
    private AdminItemCreateRequest createRequest;
    private AdminItemUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testResponse1 = AdminItemResponse.builder()
                .id(1L)
                .itemName("테스트 아이템 1")
                .isRandomBox(true)
                .build();

        testResponse2 = AdminItemResponse.builder()
                .id(2L)
                .itemName("테스트 아이템 2")
                .isRandomBox(false)
                .build();

        createRequest = new AdminItemCreateRequest("새로운 아이템", false);
        updateRequest = new AdminItemUpdateRequest("수정된 아이템", true);
    }

    @Test
    @DisplayName("모든 아이템 조회 - 성공")
    void getAllItems_Success() throws Exception {
        // given
        List<AdminItemResponse> items = Arrays.asList(testResponse1, testResponse2);
        given(adminItemService.getAllItems()).willReturn(items);

        // when & then
        mockMvc.perform(get("/api/boss/admin/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].itemName").value("테스트 아이템 1"))
                .andExpect(jsonPath("$.data[0].isRandomBox").value(true))
                .andExpect(jsonPath("$.data[1].itemName").value("테스트 아이템 2"))
                .andExpect(jsonPath("$.data[1].isRandomBox").value(false));

        verify(adminItemService).getAllItems();
    }

    @Test
    @DisplayName("페이징된 모든 아이템 조회 - 성공")
    void getAllItemsWithPaging_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<AdminItemResponse> items = Arrays.asList(testResponse1, testResponse2);
        Page<AdminItemResponse> itemPage = new PageImpl<>(items, pageable, 2);
        given(adminItemService.getAllItems(any(Pageable.class))).willReturn(itemPage);

        // when & then
        mockMvc.perform(get("/api/boss/admin/items/page")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].itemName").value("테스트 아이템 1"));

        verify(adminItemService).getAllItems(any(Pageable.class));
    }

    @Test
    @DisplayName("특정 아이템 조회 - 성공")
    void getItem_Success() throws Exception {
        // given
        Long itemId = 1L;
        given(adminItemService.getItem(itemId)).willReturn(testResponse1);

        // when & then
        mockMvc.perform(get("/api/boss/admin/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.itemName").value("테스트 아이템 1"))
                .andExpect(jsonPath("$.data.isRandomBox").value(true));

        verify(adminItemService).getItem(itemId);
    }

    @Test
    @DisplayName("특정 아이템 조회 - 존재하지 않는 아이템")
    void getItem_NotFound() throws Exception {
        // given
        Long itemId = 999L;
        given(adminItemService.getItem(itemId))
                .willThrow(new IllegalArgumentException("존재하지 않는 아이템입니다. ID: " + itemId));

        // when & then
        mockMvc.perform(get("/api/boss/admin/items/{id}", itemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 아이템입니다. ID: " + itemId));

        verify(adminItemService).getItem(itemId);
    }

    @Test
    @DisplayName("아이템 이름으로 검색 - 성공")
    void searchItems_Success() throws Exception {
        // given
        String searchName = "테스트";
        List<AdminItemResponse> items = Arrays.asList(testResponse1, testResponse2);
        given(adminItemService.searchItemsByName(searchName)).willReturn(items);

        // when & then
        mockMvc.perform(get("/api/boss/admin/items/search")
                        .param("itemName", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].itemName").value("테스트 아이템 1"))
                .andExpect(jsonPath("$.data[1].itemName").value("테스트 아이템 2"));

        verify(adminItemService).searchItemsByName(searchName);
    }

    @Test
    @DisplayName("아이템 이름으로 검색 - 결과 없음")
    void searchItems_EmptyResult() throws Exception {
        // given
        String searchName = "존재하지않는아이템";
        given(adminItemService.searchItemsByName(searchName)).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/boss/admin/items/search")
                        .param("itemName", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(adminItemService).searchItemsByName(searchName);
    }

    @Test
    @DisplayName("아이템 생성 - 성공")
    void createItem_Success() throws Exception {
        // given
        given(adminItemService.createItem(any(AdminItemCreateRequest.class))).willReturn(testResponse1);

        // when & then
        mockMvc.perform(post("/api/boss/admin/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.itemName").value("테스트 아이템 1"))
                .andExpect(jsonPath("$.data.isRandomBox").value(true));

        verify(adminItemService).createItem(any(AdminItemCreateRequest.class));
    }

    @Test
    @DisplayName("아이템 생성 - 중복된 이름")
    void createItem_DuplicateName() throws Exception {
        // given
        given(adminItemService.createItem(any(AdminItemCreateRequest.class)))
                .willThrow(new IllegalArgumentException("이미 존재하는 아이템 이름입니다: " + createRequest.getItemName()));

        // when & then
        mockMvc.perform(post("/api/boss/admin/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 존재하는 아이템 이름입니다: " + createRequest.getItemName()));

        verify(adminItemService).createItem(any(AdminItemCreateRequest.class));
    }

    @Test
    @DisplayName("아이템 수정 - 성공")
    void updateItem_Success() throws Exception {
        // given
        Long itemId = 1L;
        given(adminItemService.updateItem(eq(itemId), any(AdminItemUpdateRequest.class))).willReturn(testResponse2);

        // when & then
        mockMvc.perform(put("/api/boss/admin/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.itemName").value("테스트 아이템 2"))
                .andExpect(jsonPath("$.data.isRandomBox").value(false));

        verify(adminItemService).updateItem(eq(itemId), any(AdminItemUpdateRequest.class));
    }

    @Test
    @DisplayName("아이템 수정 - 존재하지 않는 아이템")
    void updateItem_NotFound() throws Exception {
        // given
        Long itemId = 999L;
        given(adminItemService.updateItem(eq(itemId), any(AdminItemUpdateRequest.class)))
                .willThrow(new IllegalArgumentException("존재하지 않는 아이템입니다. ID: " + itemId));

        // when & then
        mockMvc.perform(put("/api/boss/admin/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 아이템입니다. ID: " + itemId));

        verify(adminItemService).updateItem(eq(itemId), any(AdminItemUpdateRequest.class));
    }

    @Test
    @DisplayName("아이템 삭제 - 성공")
    void deleteItem_Success() throws Exception {
        // given
        Long itemId = 1L;
        doNothing().when(adminItemService).deleteItem(itemId);

        // when & then
        mockMvc.perform(delete("/api/boss/admin/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(adminItemService).deleteItem(itemId);
    }

    @Test
    @DisplayName("아이템 삭제 - 존재하지 않는 아이템")
    void deleteItem_NotFound() throws Exception {
        // given
        Long itemId = 999L;
        doThrow(new IllegalArgumentException("존재하지 않는 아이템입니다. ID: " + itemId))
                .when(adminItemService).deleteItem(itemId);

        // when & then
        mockMvc.perform(delete("/api/boss/admin/items/{id}", itemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 아이템입니다. ID: " + itemId));

        verify(adminItemService).deleteItem(itemId);
    }

    @Test
    @DisplayName("아이템 조회 시 서버 오류")
    void getAllItems_ServerError() throws Exception {
        // given
        given(adminItemService.getAllItems()).willThrow(new RuntimeException("서버 오류"));

        // when & then
        mockMvc.perform(get("/api/boss/admin/items"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("아이템 목록 조회 중 오류가 발생했습니다."));

        verify(adminItemService).getAllItems();
    }
} 