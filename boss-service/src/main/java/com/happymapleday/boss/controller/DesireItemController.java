package com.happymapleday.boss.controller;

import com.happymapleday.boss.dto.DesireItemDto;
import com.happymapleday.boss.service.DesireItemService;
import com.happymapleday.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/desire-items")
@RequiredArgsConstructor
@Slf4j
public class DesireItemController {

    private final DesireItemService desireItemService;

    // 특정 보스의 모든 물욕템 조회
    @GetMapping("/boss/{bossId}")
    public ResponseEntity<ApiResponse<List<DesireItemDto.Response>>> getDesireItemsByBossId(@PathVariable Long bossId) {
        try {
            List<DesireItemDto.Response> desireItems = desireItemService.getDesireItemsByBossId(bossId);
            return ResponseEntity.ok(ApiResponse.success("물욕템 목록 조회가 완료되었습니다.", desireItems));
        } catch (Exception e) {
            log.error("물욕템 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("물욕템 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // 특정 보스의 물욕템 조회 (랜덤박스 아이템 포함)
    @GetMapping("/boss/{bossId}/with-random-box")
    public ResponseEntity<ApiResponse<List<DesireItemDto.Response>>> getDesireItemsWithRandomBoxByBossId(@PathVariable Long bossId) {
        try {
            List<DesireItemDto.Response> desireItems = desireItemService.getDesireItemsWithRandomBoxByBossId(bossId);
            return ResponseEntity.ok(ApiResponse.success("물욕템 목록(랜덤박스 포함) 조회가 완료되었습니다.", desireItems));
        } catch (Exception e) {
            log.error("물욕템 목록(랜덤박스 포함) 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("물욕템 목록(랜덤박스 포함) 조회 중 오류가 발생했습니다."));
        }
    }

    // ID로 물욕템 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DesireItemDto.Response>> getDesireItemById(@PathVariable Long id) {
        try {
            DesireItemDto.Response desireItem = desireItemService.getDesireItemById(id);
            return ResponseEntity.ok(ApiResponse.success("물욕템 상세 조회가 완료되었습니다.", desireItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("물욕템 상세 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("물욕템 상세 조회 중 오류가 발생했습니다."));
        }
    }

    // 랜덤박스 아이템만 조회
    @GetMapping("/boss/{bossId}/random-box")
    public ResponseEntity<ApiResponse<List<DesireItemDto.Response>>> getRandomBoxItemsByBossId(@PathVariable Long bossId) {
        try {
            List<DesireItemDto.Response> desireItems = desireItemService.getRandomBoxItemsByBossId(bossId);
            return ResponseEntity.ok(ApiResponse.success("랜덤박스 아이템 조회가 완료되었습니다.", desireItems));
        } catch (Exception e) {
            log.error("랜덤박스 아이템 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("랜덤박스 아이템 조회 중 오류가 발생했습니다."));
        }
    }

    // 일반 물욕템만 조회
    @GetMapping("/boss/{bossId}/normal")
    public ResponseEntity<ApiResponse<List<DesireItemDto.Response>>> getNormalDesireItemsByBossId(@PathVariable Long bossId) {
        try {
            List<DesireItemDto.Response> desireItems = desireItemService.getNormalDesireItemsByBossId(bossId);
            return ResponseEntity.ok(ApiResponse.success("일반 물욕템 조회가 완료되었습니다.", desireItems));
        } catch (Exception e) {
            log.error("일반 물욕템 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("일반 물욕템 조회 중 오류가 발생했습니다."));
        }
    }

    // 아이템명으로 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DesireItemDto.Response>>> searchDesireItemsByName(@RequestParam String itemName) {
        try {
            List<DesireItemDto.Response> desireItems = desireItemService.searchDesireItemsByName(itemName);
            return ResponseEntity.ok(ApiResponse.success("물욕템 검색이 완료되었습니다.", desireItems));
        } catch (Exception e) {
            log.error("물욕템 검색 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("물욕템 검색 중 오류가 발생했습니다."));
        }
    }

    // 특정 보스의 물욕템 개수 조회
    @GetMapping("/boss/{bossId}/count")
    public ResponseEntity<ApiResponse<Long>> countDesireItemsByBossId(@PathVariable Long bossId) {
        try {
            long count = desireItemService.countDesireItemsByBossId(bossId);
            return ResponseEntity.ok(ApiResponse.success("물욕템 개수 조회가 완료되었습니다.", count));
        } catch (Exception e) {
            log.error("물욕템 개수 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("물욕템 개수 조회 중 오류가 발생했습니다."));
        }
    }

    // 물욕템 생성
    @PostMapping
    public ResponseEntity<ApiResponse<DesireItemDto.Response>> createDesireItem(@RequestBody DesireItemDto.CreateRequest createRequest) {
        try {
            DesireItemDto.Response desireItem = desireItemService.createDesireItem(createRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("물욕템이 성공적으로 생성되었습니다.", desireItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("물욕템 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("물욕템 생성 중 오류가 발생했습니다."));
        }
    }

    // 물욕템 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DesireItemDto.Response>> updateDesireItem(
            @PathVariable Long id,
            @RequestBody DesireItemDto.UpdateRequest updateRequest) {
        try {
            DesireItemDto.Response desireItem = desireItemService.updateDesireItem(id, updateRequest);
            return ResponseEntity.ok(ApiResponse.success("물욕템이 성공적으로 수정되었습니다.", desireItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("물욕템 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("물욕템 수정 중 오류가 발생했습니다."));
        }
    }

    // 물욕템 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDesireItem(@PathVariable Long id) {
        try {
            desireItemService.deleteDesireItem(id);
            return ResponseEntity.ok(ApiResponse.success("물욕템이 성공적으로 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("물욕템 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("물욕템 삭제 중 오류가 발생했습니다."));
        }
    }

    // 보스별 물욕템 일괄 삭제
    @DeleteMapping("/boss/{bossId}")
    public ResponseEntity<ApiResponse<Void>> deleteDesireItemsByBossId(@PathVariable Long bossId) {
        try {
            desireItemService.deleteDesireItemsByBossId(bossId);
            return ResponseEntity.ok(ApiResponse.success("보스의 모든 물욕템이 성공적으로 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("보스별 물욕템 일괄 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스별 물욕템 일괄 삭제 중 오류가 발생했습니다."));
        }
    }

    // 특정 보스의 특정 타입 아이템만 조회
    @GetMapping("/boss/{bossId}/type")
    public ResponseEntity<ApiResponse<List<DesireItemDto.Response>>> getDesireItemsByBossAndType(
            @PathVariable Long bossId,
            @RequestParam Boolean isRandomBox) {
        try {
            List<DesireItemDto.Response> desireItems = desireItemService.getDesireItemsByBossAndType(bossId, isRandomBox);
            String type = isRandomBox ? "랜덤박스" : "일반";
            return ResponseEntity.ok(ApiResponse.success(type + " 물욕템 조회가 완료되었습니다.", desireItems));
        } catch (Exception e) {
            log.error("타입별 물욕템 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("타입별 물욕템 조회 중 오류가 발생했습니다."));
        }
    }
}