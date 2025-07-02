package com.happymapleday.boss.admin.controller;

import com.happymapleday.boss.admin.dto.request.AdminBossCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminBossUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminBossResponse;
import com.happymapleday.boss.admin.service.AdminBossService;
import com.happymapleday.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/bosses")
@RequiredArgsConstructor
public class AdminBossController {
    
    private final AdminBossService adminBossService;
    
    // 모든 보스 조회 (페이징)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminBossResponse>>> getAllBosses(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AdminBossResponse> bosses = adminBossService.getAllBosses(pageable);
        return ResponseEntity.ok(ApiResponse.success(bosses));
    }
    
    // 모든 보스 조회 (리스트)
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<AdminBossResponse>>> getAllBossesList() {
        List<AdminBossResponse> bosses = adminBossService.getAllBosses();
        return ResponseEntity.ok(ApiResponse.success(bosses));
    }
    
    // 특정 보스 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminBossResponse>> getBoss(@PathVariable Long id) {
        AdminBossResponse boss = adminBossService.getBoss(id);
        return ResponseEntity.ok(ApiResponse.success(boss));
    }
    
    // 보스 생성
    @PostMapping
    public ResponseEntity<ApiResponse<AdminBossResponse>> createBoss(@RequestBody AdminBossCreateRequest request) {
        AdminBossResponse boss = adminBossService.createBoss(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(boss));
    }
    
    // 보스 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminBossResponse>> updateBoss(
            @PathVariable Long id, 
            @RequestBody AdminBossUpdateRequest request) {
        AdminBossResponse boss = adminBossService.updateBoss(id, request);
        return ResponseEntity.ok(ApiResponse.success(boss));
    }
    
    // 보스 삭제 (소프트 삭제 - 비활성화)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoss(@PathVariable Long id) {
        adminBossService.deleteBoss(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    // 보스 완전 삭제 (하드 삭제)
    @DeleteMapping("/{id}/completely")
    public ResponseEntity<ApiResponse<Void>> deleteBossCompletely(@PathVariable Long id) {
        adminBossService.deleteBossCompletely(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    // 보스 활성화
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<AdminBossResponse>> activateBoss(@PathVariable Long id) {
        AdminBossResponse boss = adminBossService.activateBoss(id);
        return ResponseEntity.ok(ApiResponse.success(boss));
    }
} 