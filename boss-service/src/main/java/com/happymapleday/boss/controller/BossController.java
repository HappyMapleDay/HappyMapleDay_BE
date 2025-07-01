package com.happymapleday.boss.controller;

import com.happymapleday.boss.dto.BossDto;
import com.happymapleday.boss.entity.ForceType;
import com.happymapleday.boss.service.BossService;
import com.happymapleday.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bosses")
@RequiredArgsConstructor
@Slf4j
public class BossController {

    private final BossService bossService;

    // 모든 활성화된 보스 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<BossDto.Response>>> getAllActiveBosses() {
        try {
            List<BossDto.Response> bosses = bossService.getAllActiveBosses();
            return ResponseEntity.ok(ApiResponse.success("보스 목록 조회가 완료되었습니다.", bosses));
        } catch (Exception e) {
            log.error("보스 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // 페이징된 보스 목록 조회
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<Page<BossDto.Response>>> getBossesWithPaging(
            @PageableDefault(size = 20, sort = "crystalPrice", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<BossDto.Response> bosses = bossService.getBossesWithPaging(pageable);
            return ResponseEntity.ok(ApiResponse.success("페이징된 보스 목록 조회가 완료되었습니다.", bosses));
        } catch (Exception e) {
            log.error("페이징된 보스 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("페이징된 보스 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // ID로 보스 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BossDto.Response>> getBossById(@PathVariable Long id) {
        try {
            BossDto.Response boss = bossService.getBossById(id);
            return ResponseEntity.ok(ApiResponse.success("보스 상세 조회가 완료되었습니다.", boss));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("보스 상세 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 상세 조회 중 오류가 발생했습니다."));
        }
    }

    // 보스명으로 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BossDto.Response>>> searchBossesByName(
            @RequestParam String bossName) {
        try {
            List<BossDto.Response> bosses = bossService.searchBossesByName(bossName);
            return ResponseEntity.ok(ApiResponse.success("보스 검색이 완료되었습니다.", bosses));
        } catch (Exception e) {
            log.error("보스 검색 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 검색 중 오류가 발생했습니다."));
        }
    }

    // 난이도별 보스 조회
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<ApiResponse<List<BossDto.Response>>> getBossesByDifficulty(
            @PathVariable String difficulty) {
        try {
            List<BossDto.Response> bosses = bossService.getBossesByDifficulty(difficulty);
            return ResponseEntity.ok(ApiResponse.success("난이도별 보스 조회가 완료되었습니다.", bosses));
        } catch (Exception e) {
            log.error("난이도별 보스 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("난이도별 보스 조회 중 오류가 발생했습니다."));
        }
    }

    // 주간/월간 보스 구분 조회
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<List<BossDto.Response>>> getBossesByMonthly(
            @RequestParam Boolean isMonthly) {
        try {
            List<BossDto.Response> bosses = bossService.getBossesByMonthly(isMonthly);
            String message = isMonthly ? "월간 보스 조회가 완료되었습니다." : "주간 보스 조회가 완료되었습니다.";
            return ResponseEntity.ok(ApiResponse.success(message, bosses));
        } catch (Exception e) {
            log.error("주간/월간 보스 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("주간/월간 보스 조회 중 오류가 발생했습니다."));
        }
    }

    // 포스 타입별 보스 조회
    @GetMapping("/force/{forceType}")
    public ResponseEntity<ApiResponse<List<BossDto.Response>>> getBossesByForceType(
            @PathVariable ForceType forceType) {
        try {
            List<BossDto.Response> bosses = bossService.getBossesByForceType(forceType);
            return ResponseEntity.ok(ApiResponse.success("포스 타입별 보스 조회가 완료되었습니다.", bosses));
        } catch (Exception e) {
            log.error("포스 타입별 보스 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("포스 타입별 보스 조회 중 오류가 발생했습니다."));
        }
    }

    // 결정석 가격 범위로 보스 조회
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<BossDto.Response>>> getBossesByPriceRange(
            @RequestParam Long minPrice,
            @RequestParam Long maxPrice) {
        try {
            List<BossDto.Response> bosses = bossService.getBossesByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(ApiResponse.success("가격 범위별 보스 조회가 완료되었습니다.", bosses));
        } catch (Exception e) {
            log.error("가격 범위별 보스 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("가격 범위별 보스 조회 중 오류가 발생했습니다."));
        }
    }

    // 캐릭터 레벨에 맞는 보스 조회
    @GetMapping("/character-level/{characterLevel}")
    public ResponseEntity<ApiResponse<List<BossDto.Response>>> getBossesForCharacterLevel(
            @PathVariable Integer characterLevel) {
        try {
            List<BossDto.Response> bosses = bossService.getBossesForCharacterLevel(characterLevel);
            return ResponseEntity.ok(ApiResponse.success("캐릭터 레벨에 맞는 보스 조회가 완료되었습니다.", bosses));
        } catch (Exception e) {
            log.error("캐릭터 레벨별 보스 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("캐릭터 레벨별 보스 조회 중 오류가 발생했습니다."));
        }
    }

    // 포스 조건에 맞는 보스 조회
    @GetMapping("/force-condition")
    public ResponseEntity<ApiResponse<List<BossDto.Response>>> getBossesForForceCondition(
            @RequestParam(required = false) Integer arcaneForce,
            @RequestParam(required = false) Integer authenticForce) {
        try {
            List<BossDto.Response> bosses = bossService.getBossesForForceCondition(arcaneForce, authenticForce);
            return ResponseEntity.ok(ApiResponse.success("포스 조건에 맞는 보스 조회가 완료되었습니다.", bosses));
        } catch (Exception e) {
            log.error("포스 조건별 보스 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("포스 조건별 보스 조회 중 오류가 발생했습니다."));
        }
    }

    // 복합 검색 조건으로 보스 조회
    @PostMapping("/search/advanced")
    public ResponseEntity<ApiResponse<List<BossDto.Response>>> searchBosses(
            @RequestBody BossDto.SearchRequest searchRequest) {
        try {
            List<BossDto.Response> bosses = bossService.searchBosses(searchRequest);
            return ResponseEntity.ok(ApiResponse.success("복합 조건 보스 검색이 완료되었습니다.", bosses));
        } catch (Exception e) {
            log.error("복합 조건 보스 검색 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("복합 조건 보스 검색 중 오류가 발생했습니다."));
        }
    }

    // 보스 생성
    @PostMapping
    public ResponseEntity<ApiResponse<BossDto.Response>> createBoss(
            @RequestBody BossDto.CreateRequest createRequest) {
        try {
            BossDto.Response boss = bossService.createBoss(createRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("보스가 성공적으로 생성되었습니다.", boss));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("보스 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 생성 중 오류가 발생했습니다."));
        }
    }

    // 보스 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BossDto.Response>> updateBoss(
            @PathVariable Long id,
            @RequestBody BossDto.UpdateRequest updateRequest) {
        try {
            BossDto.Response boss = bossService.updateBoss(id, updateRequest);
            return ResponseEntity.ok(ApiResponse.success("보스가 성공적으로 수정되었습니다.", boss));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("보스 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 수정 중 오류가 발생했습니다."));
        }
    }

    // 보스 삭제 (비활성화)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoss(@PathVariable Long id) {
        try {
            bossService.deleteBoss(id);
            return ResponseEntity.ok(ApiResponse.success("보스가 성공적으로 비활성화되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("보스 비활성화 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 비활성화 중 오류가 발생했습니다."));
        }
    }

    // 보스 활성화
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<BossDto.Response>> activateBoss(@PathVariable Long id) {
        try {
            BossDto.Response boss = bossService.activateBoss(id);
            return ResponseEntity.ok(ApiResponse.success("보스가 성공적으로 활성화되었습니다.", boss));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("보스 활성화 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 활성화 중 오류가 발생했습니다."));
        }
    }
} 