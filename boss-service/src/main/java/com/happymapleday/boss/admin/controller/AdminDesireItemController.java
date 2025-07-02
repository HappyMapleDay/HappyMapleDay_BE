package com.happymapleday.boss.admin.controller;

import com.happymapleday.boss.admin.dto.request.AdminDesireItemCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminDesireItemUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminDesireItemResponse;
import com.happymapleday.boss.admin.service.AdminDesireItemService;
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
@RequestMapping("/admin/api/desire-items")
@RequiredArgsConstructor
public class AdminDesireItemController {
    
    private final AdminDesireItemService adminDesireItemService;
    
    // 모든 물욕템 조회 (페이징)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminDesireItemResponse>>> getAllDesireItems(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AdminDesireItemResponse> desireItems = adminDesireItemService.getAllDesireItems(pageable);
        return ResponseEntity.ok(ApiResponse.success(desireItems));
    }
    
    // 모든 물욕템 조회 (리스트)
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<AdminDesireItemResponse>>> getAllDesireItemsList() {
        List<AdminDesireItemResponse> desireItems = adminDesireItemService.getAllDesireItems();
        return ResponseEntity.ok(ApiResponse.success(desireItems));
    }
    
    // 특정 물욕템 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminDesireItemResponse>> getDesireItem(@PathVariable Long id) {
        AdminDesireItemResponse desireItem = adminDesireItemService.getDesireItem(id);
        return ResponseEntity.ok(ApiResponse.success(desireItem));
    }
    
    // 특정 보스의 물욕템 조회
    @GetMapping("/boss/{bossId}")
    public ResponseEntity<ApiResponse<List<AdminDesireItemResponse>>> getDesireItemsByBoss(@PathVariable Long bossId) {
        List<AdminDesireItemResponse> desireItems = adminDesireItemService.getDesireItemsByBoss(bossId);
        return ResponseEntity.ok(ApiResponse.success(desireItems));
    }
    
    // 물욕템 생성
    @PostMapping
    public ResponseEntity<ApiResponse<AdminDesireItemResponse>> createDesireItem(@RequestBody AdminDesireItemCreateRequest request) {
        AdminDesireItemResponse desireItem = adminDesireItemService.createDesireItem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(desireItem));
    }
    
    // 물욕템 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminDesireItemResponse>> updateDesireItem(
            @PathVariable Long id, 
            @RequestBody AdminDesireItemUpdateRequest request) {
        AdminDesireItemResponse desireItem = adminDesireItemService.updateDesireItem(id, request);
        return ResponseEntity.ok(ApiResponse.success(desireItem));
    }
    
    // 물욕템 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDesireItem(@PathVariable Long id) {
        adminDesireItemService.deleteDesireItem(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
} 