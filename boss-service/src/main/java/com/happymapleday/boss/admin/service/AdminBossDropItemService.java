package com.happymapleday.boss.admin.service;

import com.happymapleday.boss.admin.dto.request.AdminBossDropItemCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminBossDropItemUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminBossDropItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminBossDropItemService {
    
    // 모든 보스 드랍 아이템 조회
    List<AdminBossDropItemResponse> getAllBossDropItems();
    
    // 페이징된 모든 보스 드랍 아이템 조회
    Page<AdminBossDropItemResponse> getAllBossDropItems(Pageable pageable);
    
    // 특정 보스 드랍 아이템 조회
    AdminBossDropItemResponse getBossDropItem(Long id);
    
    // 특정 보스의 드랍 아이템 조회
    List<AdminBossDropItemResponse> getBossDropItemsByBoss(Long bossId);
    
    // 보스 드랍 아이템 생성
    AdminBossDropItemResponse createBossDropItem(AdminBossDropItemCreateRequest request);
    
    // 보스 드랍 아이템 수정
    AdminBossDropItemResponse updateBossDropItem(Long id, AdminBossDropItemUpdateRequest request);
    
    // 보스 드랍 아이템 삭제
    void deleteBossDropItem(Long id);
} 