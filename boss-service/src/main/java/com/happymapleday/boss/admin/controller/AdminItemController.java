package com.happymapleday.boss.admin.controller;

import com.happymapleday.boss.admin.dto.request.AdminItemCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminItemUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminItemResponse;
import com.happymapleday.boss.admin.service.AdminItemService;
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
@RequestMapping("/api/boss/admin/items")
@RequiredArgsConstructor
@Slf4j
public class AdminItemController {

    private final AdminItemService adminItemService;

    // 모든 아이템 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminItemResponse>>> getAllItems() {
        try {
            List<AdminItemResponse> items = adminItemService.getAllItems();
            return ResponseEntity.ok(ApiResponse.success(items));
        } catch (Exception e) {
            log.error("아이템 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이템 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // 페이징된 모든 아이템 조회
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<AdminItemResponse>>> getAllItemsWithPaging(Pageable pageable) {
        try {
            Page<AdminItemResponse> items = adminItemService.getAllItems(pageable);
            return ResponseEntity.ok(ApiResponse.success(items));
        } catch (Exception e) {
            log.error("아이템 페이징 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이템 페이징 조회 중 오류가 발생했습니다."));
        }
    }

    // 특정 아이템 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminItemResponse>> getItem(@PathVariable Long id) {
        try {
            AdminItemResponse item = adminItemService.getItem(id);
            return ResponseEntity.ok(ApiResponse.success(item));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("아이템 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이템 조회 중 오류가 발생했습니다."));
        }
    }

    // 아이템 이름으로 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AdminItemResponse>>> searchItems(@RequestParam String itemName) {
        try {
            List<AdminItemResponse> items = adminItemService.searchItemsByName(itemName);
            return ResponseEntity.ok(ApiResponse.success(items));
        } catch (Exception e) {
            log.error("아이템 검색 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이템 검색 중 오류가 발생했습니다."));
        }
    }

    // 아이템 생성
    @PostMapping
    public ResponseEntity<ApiResponse<AdminItemResponse>> createItem(@RequestBody AdminItemCreateRequest request) {
        try {
            AdminItemResponse item = adminItemService.createItem(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(item));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("아이템 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이템 생성 중 오류가 발생했습니다."));
        }
    }

    // 아이템 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminItemResponse>> updateItem(@PathVariable Long id, @RequestBody AdminItemUpdateRequest request) {
        try {
            AdminItemResponse item = adminItemService.updateItem(id, request);
            return ResponseEntity.ok(ApiResponse.success(item));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("아이템 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이템 수정 중 오류가 발생했습니다."));
        }
    }

    // 아이템 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        try {
            adminItemService.deleteItem(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("아이템 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이템 삭제 중 오류가 발생했습니다."));
        }
    }
}