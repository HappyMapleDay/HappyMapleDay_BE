package com.happymapleday.boss.admin.controller;

import com.happymapleday.boss.admin.dto.request.AdminBossDropItemCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminBossDropItemUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminBossDropItemResponse;
import com.happymapleday.boss.admin.service.AdminBossDropItemService;
import com.happymapleday.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/boss-drop-items")
@RequiredArgsConstructor
@Slf4j
public class AdminBossDropItemController {

    private final AdminBossDropItemService adminBossDropItemService;

    // 모든 보스 드랍 아이템 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminBossDropItemResponse>>> getAllBossDropItems() {
        try {
            List<AdminBossDropItemResponse> bossDropItems = adminBossDropItemService.getAllBossDropItems();
            return ResponseEntity.ok(ApiResponse.success(bossDropItems));
        } catch (Exception e) {
            log.error("보스 드랍 아이템 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 드랍 아이템 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // 페이징된 모든 보스 드랍 아이템 조회
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<AdminBossDropItemResponse>>> getAllBossDropItemsWithPaging(Pageable pageable) {
        try {
            Page<AdminBossDropItemResponse> bossDropItems = adminBossDropItemService.getAllBossDropItems(pageable);
            return ResponseEntity.ok(ApiResponse.success(bossDropItems));
        } catch (Exception e) {
            log.error("보스 드랍 아이템 페이징 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 드랍 아이템 페이징 조회 중 오류가 발생했습니다."));
        }
    }

    // 특정 보스의 드랍 아이템 조회
    @GetMapping("/boss/{bossId}")
    public ResponseEntity<ApiResponse<List<AdminBossDropItemResponse>>> getBossDropItemsByBoss(@PathVariable Long bossId) {
        try {
            List<AdminBossDropItemResponse> bossDropItems = adminBossDropItemService.getBossDropItemsByBoss(bossId);
            return ResponseEntity.ok(ApiResponse.success(bossDropItems));
        } catch (Exception e) {
            log.error("보스별 드랍 아이템 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스별 드랍 아이템 조회 중 오류가 발생했습니다."));
        }
    }

    // 특정 보스 드랍 아이템 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminBossDropItemResponse>> getBossDropItem(@PathVariable Long id) {
        try {
            AdminBossDropItemResponse bossDropItem = adminBossDropItemService.getBossDropItem(id);
            return ResponseEntity.ok(ApiResponse.success(bossDropItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("보스 드랍 아이템 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 드랍 아이템 조회 중 오류가 발생했습니다."));
        }
    }

    // 보스 드랍 아이템 생성
    @PostMapping
    public ResponseEntity<ApiResponse<AdminBossDropItemResponse>> createBossDropItem(@RequestBody AdminBossDropItemCreateRequest request) {
        try {
            AdminBossDropItemResponse bossDropItem = adminBossDropItemService.createBossDropItem(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(bossDropItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("보스 드랍 아이템 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 드랍 아이템 생성 중 오류가 발생했습니다."));
        }
    }

    // 보스 드랍 아이템 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminBossDropItemResponse>> updateBossDropItem(@PathVariable Long id, @RequestBody AdminBossDropItemUpdateRequest request) {
        try {
            AdminBossDropItemResponse bossDropItem = adminBossDropItemService.updateBossDropItem(id, request);
            return ResponseEntity.ok(ApiResponse.success(bossDropItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("보스 드랍 아이템 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 드랍 아이템 수정 중 오류가 발생했습니다."));
        }
    }

    // 보스 드랍 아이템 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBossDropItem(@PathVariable Long id) {
        try {
            adminBossDropItemService.deleteBossDropItem(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("보스 드랍 아이템 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 드랍 아이템 삭제 중 오류가 발생했습니다."));
        }
    }
} 