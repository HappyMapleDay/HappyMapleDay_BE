package com.happymapleday.boss.admin.service;

import com.happymapleday.boss.admin.dto.request.AdminDesireItemCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminDesireItemUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminDesireItemResponse;
import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.DesireItem;
import com.happymapleday.boss.repository.BossRepository;
import com.happymapleday.boss.repository.DesireItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDesireItemService {
    
    private final DesireItemRepository desireItemRepository;
    private final BossRepository bossRepository;
    
    // 모든 물욕템 조회
    public List<AdminDesireItemResponse> getAllDesireItems() {
        return desireItemRepository.findAll().stream()
                .map(AdminDesireItemResponse::new)
                .collect(Collectors.toList());
    }
    
    // 페이징된 모든 물욕템 조회
    public Page<AdminDesireItemResponse> getAllDesireItems(Pageable pageable) {
        return desireItemRepository.findAll(pageable)
                .map(AdminDesireItemResponse::new);
    }
    
    // 특정 물욕템 조회
    public AdminDesireItemResponse getDesireItem(Long id) {
        DesireItem desireItem = desireItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 물욕템입니다. ID: " + id));
        return new AdminDesireItemResponse(desireItem);
    }
    
    // 특정 보스의 물욕템 조회
    public List<AdminDesireItemResponse> getDesireItemsByBoss(Long bossId) {
        return desireItemRepository.findByBossIdOrderByItemName(bossId).stream()
                .map(AdminDesireItemResponse::new)
                .collect(Collectors.toList());
    }
    
    // 물욕템 생성
    @Transactional
    public AdminDesireItemResponse createDesireItem(AdminDesireItemCreateRequest request) {
        Boss boss = bossRepository.findById(request.getBossId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스입니다. ID: " + request.getBossId()));
        
        DesireItem desireItem = DesireItem.builder()
                .boss(boss)
                .itemName(request.getItemName())
                .isRandomBox(request.getIsRandomBox())
                .build();
        
        DesireItem savedDesireItem = desireItemRepository.save(desireItem);
        return new AdminDesireItemResponse(savedDesireItem);
    }
    
    // 물욕템 수정
    @Transactional
    public AdminDesireItemResponse updateDesireItem(Long id, AdminDesireItemUpdateRequest request) {
        DesireItem desireItem = desireItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 물욕템입니다. ID: " + id));
        
        Boss boss = bossRepository.findById(request.getBossId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스입니다. ID: " + request.getBossId()));
        
        // 새로운 물욕템으로 교체
        DesireItem updatedDesireItem = DesireItem.builder()
                .boss(boss)
                .itemName(request.getItemName())
                .isRandomBox(request.getIsRandomBox())
                .build();
        
        desireItemRepository.delete(desireItem);
        DesireItem savedDesireItem = desireItemRepository.save(updatedDesireItem);
        return new AdminDesireItemResponse(savedDesireItem);
    }
    
    // 물욕템 삭제
    @Transactional
    public void deleteDesireItem(Long id) {
        DesireItem desireItem = desireItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 물욕템입니다. ID: " + id));
        
        desireItemRepository.delete(desireItem);
    }
} 