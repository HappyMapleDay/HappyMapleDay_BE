package com.happymapleday.boss.admin.service;

import com.happymapleday.boss.admin.dto.request.AdminBossCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminBossUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminBossResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminBossService {
    
    // 모든 보스 조회 (관리자용 - 비활성화된 것도 포함)
    List<AdminBossResponse> getAllBosses();
    
    // 페이징된 모든 보스 조회
    Page<AdminBossResponse> getAllBosses(Pageable pageable);
    
    // 특정 보스 조회
    AdminBossResponse getBoss(Long id);
    
    // 보스 생성
    AdminBossResponse createBoss(AdminBossCreateRequest request);
    
    // 보스 수정
    AdminBossResponse updateBoss(Long id, AdminBossUpdateRequest request);
    
    // 보스 삭제 (소프트 삭제 - 비활성화)
    void deleteBoss(Long id);
    
    // 보스 완전 삭제 (하드 삭제)
    void deleteBossCompletely(Long id);
    
    // 보스 활성화
    AdminBossResponse activateBoss(Long id);
} 