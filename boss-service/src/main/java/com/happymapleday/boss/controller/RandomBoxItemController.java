package com.happymapleday.boss.controller;

import com.happymapleday.boss.dto.RandomBoxItemDto;
import com.happymapleday.boss.service.RandomBoxItemService;
import com.happymapleday.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/random-box-items")
@RequiredArgsConstructor
@Slf4j
public class RandomBoxItemController {

    private final RandomBoxItemService randomBoxItemService;

    // 특정 물욕템의 모든 랜덤박스 아이템 조회
    @GetMapping("/desire-item/{desireItemId}")
    public ResponseEntity<ApiResponse<List<RandomBoxItemDto.Response>>> getRandomBoxItemsByDesireItemId(@PathVariable Long desireItemId) {
        try {
            List<RandomBoxItemDto.Response> randomBoxItems = randomBoxItemService.getRandomBoxItemsByDesireItemId(desireItemId);
            return ResponseEntity.ok(ApiResponse.success("랜덤박스 아이템 목록 조회가 완료되었습니다.", randomBoxItems));
        } catch (Exception e) {
            log.error("랜덤박스 아이템 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("랜덤박스 아이템 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // ID로 랜덤박스 아이템 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RandomBoxItemDto.Response>> getRandomBoxItemById(@PathVariable Long id) {
        try {
            RandomBoxItemDto.Response randomBoxItem = randomBoxItemService.getRandomBoxItemById(id);
            return ResponseEntity.ok(ApiResponse.success("랜덤박스 아이템 상세 조회가 완료되었습니다.", randomBoxItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("랜덤박스 아이템 상세 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("랜덤박스 아이템 상세 조회 중 오류가 발생했습니다."));
        }
    }

    // 드랍 아이템명으로 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RandomBoxItemDto.Response>>> searchRandomBoxItemsByName(@RequestParam String dropItemName) {
        try {
            List<RandomBoxItemDto.Response> randomBoxItems = randomBoxItemService.searchRandomBoxItemsByName(dropItemName);
            return ResponseEntity.ok(ApiResponse.success("랜덤박스 아이템 검색이 완료되었습니다.", randomBoxItems));
        } catch (Exception e) {
            log.error("랜덤박스 아이템 검색 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("랜덤박스 아이템 검색 중 오류가 발생했습니다."));
        }
    }

    // 레벨이 있는 아이템만 조회
    @GetMapping("/desire-item/{desireItemId}/with-level")
    public ResponseEntity<ApiResponse<List<RandomBoxItemDto.Response>>> getRandomBoxItemsWithLevel(@PathVariable Long desireItemId) {
        try {
            List<RandomBoxItemDto.Response> randomBoxItems = randomBoxItemService.getRandomBoxItemsWithLevel(desireItemId);
            return ResponseEntity.ok(ApiResponse.success("레벨 포함 랜덤박스 아이템 조회가 완료되었습니다.", randomBoxItems));
        } catch (Exception e) {
            log.error("레벨 포함 랜덤박스 아이템 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("레벨 포함 랜덤박스 아이템 조회 중 오류가 발생했습니다."));
        }
    }

    // 레벨이 없는 아이템만 조회
    @GetMapping("/desire-item/{desireItemId}/without-level")
    public ResponseEntity<ApiResponse<List<RandomBoxItemDto.Response>>> getRandomBoxItemsWithoutLevel(@PathVariable Long desireItemId) {
        try {
            List<RandomBoxItemDto.Response> randomBoxItems = randomBoxItemService.getRandomBoxItemsWithoutLevel(desireItemId);
            return ResponseEntity.ok(ApiResponse.success("레벨 미포함 랜덤박스 아이템 조회가 완료되었습니다.", randomBoxItems));
        } catch (Exception e) {
            log.error("레벨 미포함 랜덤박스 아이템 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("레벨 미포함 랜덤박스 아이템 조회 중 오류가 발생했습니다."));
        }
    }

    // 보스별 모든 랜덤박스 아이템 조회
    @GetMapping("/boss/{bossId}")
    public ResponseEntity<ApiResponse<List<RandomBoxItemDto.Response>>> getAllRandomBoxItemsByBossId(@PathVariable Long bossId) {
        try {
            List<RandomBoxItemDto.Response> randomBoxItems = randomBoxItemService.getAllRandomBoxItemsByBossId(bossId);
            return ResponseEntity.ok(ApiResponse.success("보스별 랜덤박스 아이템 조회가 완료되었습니다.", randomBoxItems));
        } catch (Exception e) {
            log.error("보스별 랜덤박스 아이템 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스별 랜덤박스 아이템 조회 중 오류가 발생했습니다."));
        }
    }

    // 특정 물욕템의 랜덤박스 아이템 개수 조회
    @GetMapping("/desire-item/{desireItemId}/count")
    public ResponseEntity<ApiResponse<Long>> countRandomBoxItemsByDesireItemId(@PathVariable Long desireItemId) {
        try {
            long count = randomBoxItemService.countRandomBoxItemsByDesireItemId(desireItemId);
            return ResponseEntity.ok(ApiResponse.success("랜덤박스 아이템 개수 조회가 완료되었습니다.", count));
        } catch (Exception e) {
            log.error("랜덤박스 아이템 개수 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("랜덤박스 아이템 개수 조회 중 오류가 발생했습니다."));
        }
    }

    // 랜덤박스 아이템 생성
    @PostMapping
    public ResponseEntity<ApiResponse<RandomBoxItemDto.Response>> createRandomBoxItem(@RequestBody RandomBoxItemDto.CreateRequest createRequest) {
        try {
            RandomBoxItemDto.Response randomBoxItem = randomBoxItemService.createRandomBoxItem(createRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("랜덤박스 아이템이 성공적으로 생성되었습니다.", randomBoxItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("랜덤박스 아이템 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("랜덤박스 아이템 생성 중 오류가 발생했습니다."));
        }
    }

    // 랜덤박스 아이템 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RandomBoxItemDto.Response>> updateRandomBoxItem(
            @PathVariable Long id,
            @RequestBody RandomBoxItemDto.UpdateRequest updateRequest) {
        try {
            RandomBoxItemDto.Response randomBoxItem = randomBoxItemService.updateRandomBoxItem(id, updateRequest);
            return ResponseEntity.ok(ApiResponse.success("랜덤박스 아이템이 성공적으로 수정되었습니다.", randomBoxItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("랜덤박스 아이템 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("랜덤박스 아이템 수정 중 오류가 발생했습니다."));
        }
    }

    // 랜덤박스 아이템 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRandomBoxItem(@PathVariable Long id) {
        try {
            randomBoxItemService.deleteRandomBoxItem(id);
            return ResponseEntity.ok(ApiResponse.success("랜덤박스 아이템이 성공적으로 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("랜덤박스 아이템 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("랜덤박스 아이템 삭제 중 오류가 발생했습니다."));
        }
    }

    // 특정 물욕템의 모든 랜덤박스 아이템 삭제
    @DeleteMapping("/desire-item/{desireItemId}")
    public ResponseEntity<ApiResponse<Void>> deleteRandomBoxItemsByDesireItemId(@PathVariable Long desireItemId) {
        try {
            randomBoxItemService.deleteRandomBoxItemsByDesireItemId(desireItemId);
            return ResponseEntity.ok(ApiResponse.success("물욕템의 모든 랜덤박스 아이템이 성공적으로 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("물욕템별 랜덤박스 아이템 일괄 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("물욕템별 랜덤박스 아이템 일괄 삭제 중 오류가 발생했습니다."));
        }
    }

    // 랜덤박스 아이템 일괄 생성
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<RandomBoxItemDto.Response>>> createRandomBoxItemsBatch(
            @RequestBody List<RandomBoxItemDto.CreateRequest> createRequests) {
        try {
            List<RandomBoxItemDto.Response> randomBoxItems = randomBoxItemService.createRandomBoxItemsBatch(createRequests);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("랜덤박스 아이템이 일괄 생성되었습니다.", randomBoxItems));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("랜덤박스 아이템 일괄 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("랜덤박스 아이템 일괄 생성 중 오류가 발생했습니다."));
        }
    }
}