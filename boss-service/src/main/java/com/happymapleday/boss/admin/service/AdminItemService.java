package com.happymapleday.boss.admin.service;

import com.happymapleday.boss.admin.dto.request.AdminItemCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminItemUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminItemService {

    // 모든 아이템 조회
    List<AdminItemResponse> getAllItems();

    // 페이징된 모든 아이템 조회
    Page<AdminItemResponse> getAllItems(Pageable pageable);

    // 특정 아이템 조회
    AdminItemResponse getItem(Long id);

    // 아이템 이름으로 검색
    List<AdminItemResponse> searchItemsByName(String itemName);

    // 아이템 생성
    AdminItemResponse createItem(AdminItemCreateRequest request);

    // 아이템 수정
    AdminItemResponse updateItem(Long id, AdminItemUpdateRequest request);

    // 아이템 삭제
    void deleteItem(Long id);
} 