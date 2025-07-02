package com.happymapleday.boss.admin.service;

import com.happymapleday.boss.admin.dto.request.AdminBossDropItemCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminBossDropItemUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminBossDropItemResponse;
import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.BossDropItem;
import com.happymapleday.boss.entity.Item;
import com.happymapleday.boss.repository.BossDropItemRepository;
import com.happymapleday.boss.repository.BossRepository;
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
public class AdminBossDropItemService {
    
    private final BossDropItemRepository bossDropItemRepository;
    private final BossRepository bossRepository;
    private final ItemRepository itemRepository;
    
    // 모든 보스 드랍 아이템 조회
    public List<AdminBossDropItemResponse> getAllBossDropItems() {
        return bossDropItemRepository.findAll().stream()
                .map(AdminBossDropItemResponse::from)
                .toList();
    }
    
    // 페이징된 모든 보스 드랍 아이템 조회
    public Page<AdminBossDropItemResponse> getAllBossDropItems(Pageable pageable) {
        return bossDropItemRepository.findAll(pageable)
                .map(AdminBossDropItemResponse::from);
    }
    
    // 특정 보스 드랍 아이템 조회
    public AdminBossDropItemResponse getBossDropItem(Long id) {
        BossDropItem bossDropItem = bossDropItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스 드랍 아이템입니다. ID: " + id));
        return AdminBossDropItemResponse.fromWithRandomBoxItems(bossDropItem);
    }
    
    // 특정 보스의 드랍 아이템 조회
    public List<AdminBossDropItemResponse> getBossDropItemsByBoss(Long bossId) {
        return bossDropItemRepository.findByBossIdWithRandomBoxItems(bossId).stream()
                .map(AdminBossDropItemResponse::fromWithRandomBoxItems)
                .toList();
    }
    
    // 보스 드랍 아이템 생성
    @Transactional
    public AdminBossDropItemResponse createBossDropItem(AdminBossDropItemCreateRequest request) {
        Boss boss = bossRepository.findById(request.getBossId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스입니다. ID: " + request.getBossId()));
        
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다. ID: " + request.getItemId()));
        
        BossDropItem bossDropItem = BossDropItem.builder()
                .boss(boss)
                .item(item)
                .build();
        
        BossDropItem savedBossDropItem = bossDropItemRepository.save(bossDropItem);
        return AdminBossDropItemResponse.from(savedBossDropItem);
    }
    
    // 보스 드랍 아이템 수정
    @Transactional
    public AdminBossDropItemResponse updateBossDropItem(Long id, AdminBossDropItemUpdateRequest request) {
        BossDropItem bossDropItem = bossDropItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스 드랍 아이템입니다. ID: " + id));
        
        Boss boss = bossRepository.findById(request.getBossId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스입니다. ID: " + request.getBossId()));
        
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다. ID: " + request.getItemId()));
        
        // 기존 엔티티 삭제 후 새로 생성
        bossDropItemRepository.delete(bossDropItem);
        
        BossDropItem newBossDropItem = BossDropItem.builder()
                .boss(boss)
                .item(item)
                .build();
        
        BossDropItem savedBossDropItem = bossDropItemRepository.save(newBossDropItem);
        
        return AdminBossDropItemResponse.from(savedBossDropItem);
    }
    
    // 보스 드랍 아이템 삭제
    @Transactional
    public void deleteBossDropItem(Long id) {
        BossDropItem bossDropItem = bossDropItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스 드랍 아이템입니다. ID: " + id));
        
        bossDropItemRepository.delete(bossDropItem);
    }
} 