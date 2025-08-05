package com.happymapleday.boss.admin.service.impl;

import com.happymapleday.boss.admin.dto.request.AdminItemCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminItemUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminItemResponse;
import com.happymapleday.boss.admin.service.AdminItemService;
import com.happymapleday.boss.entity.Item;
import com.happymapleday.boss.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminItemServiceImpl implements AdminItemService {
    
    private final ItemRepository itemRepository;
    
    // 모든 아이템 조회
    @Override
    public List<AdminItemResponse> getAllItems() {
        return itemRepository.findAll().stream()
                .map(AdminItemResponse::from)
                .toList();
    }
    
    // 페이징된 모든 아이템 조회
    @Override
    public Page<AdminItemResponse> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(AdminItemResponse::from);
    }
    
    // 특정 아이템 조회
    @Override
    public AdminItemResponse getItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다. ID: " + id));
        return AdminItemResponse.from(item);
    }
    
    // 아이템 이름으로 검색
    @Override
    public List<AdminItemResponse> searchItemsByName(String itemName) {
        return itemRepository.findByItemNameContainingIgnoreCase(itemName).stream()
                .map(AdminItemResponse::from)
                .toList();
    }
    
    // 아이템 생성
    @Override
    @Transactional
    public AdminItemResponse createItem(AdminItemCreateRequest request) {
        // 아이템 이름 중복 체크
        if (itemRepository.existsByItemName(request.getItemName())) {
            throw new IllegalArgumentException("이미 존재하는 아이템 이름입니다: " + request.getItemName());
        }
        
        Item item = Item.builder()
                .itemName(request.getItemName())
                .itemNameEn(request.getItemNameEn())
                .isRandomBox(request.getIsRandomBox())
                .build();
        
        Item savedItem = itemRepository.save(item);
        return AdminItemResponse.from(savedItem);
    }
    
    // 아이템 수정
    @Override
    @Transactional
    public AdminItemResponse updateItem(Long id, AdminItemUpdateRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다. ID: " + id));
        
        // 아이템 이름 중복 체크 (자기 자신 제외)
        if (!item.getItemName().equals(request.getItemName()) && 
            itemRepository.existsByItemName(request.getItemName())) {
            throw new IllegalArgumentException("이미 존재하는 아이템 이름입니다: " + request.getItemName());
        }
        
        // 기존 엔티티 삭제 후 새로 생성
        itemRepository.delete(item);
        
        Item newItem = Item.builder()
                .itemName(request.getItemName())
                .itemNameEn(request.getItemNameEn())
                .isRandomBox(request.getIsRandomBox())
                .build();
        
        Item savedItem = itemRepository.save(newItem);
        return AdminItemResponse.from(savedItem);
    }
    
    // 아이템 삭제
    @Override
    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다. ID: " + id));
        
        itemRepository.delete(item);
    }
} 