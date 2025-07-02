package com.happymapleday.boss.controller;

import com.happymapleday.boss.dto.response.BossResponse;
import com.happymapleday.boss.dto.response.DesireItemResponse;
import com.happymapleday.boss.dto.request.ValidateLimitsRequest;
import com.happymapleday.boss.dto.response.ValidateLimitsResponse;
import com.happymapleday.boss.service.BossService;
import com.happymapleday.boss.service.BossPresetService;
import com.happymapleday.boss.service.DesireItemService;
import com.happymapleday.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boss")
@RequiredArgsConstructor
@Slf4j
public class BossController {

    private final BossService bossService;
    private final DesireItemService desireItemService;
    private final BossPresetService bossPresetService;

    // 보스 목록 조회 API
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<BossResponse>>> getAllActiveBosses() {
        try {
            List<BossResponse> bosses = bossService.getAllActiveBosses();
            return ResponseEntity.ok(ApiResponse.success(bosses));
        } catch (Exception e) {
            log.error("보스 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // 물욕템 목록 조회 API
    @GetMapping("/{bossId}/desire-items")
    public ResponseEntity<ApiResponse<List<DesireItemResponse>>> getDesireItemsApi(@PathVariable Long bossId) {
        try {
            List<DesireItemResponse> desireItems = desireItemService.getDesireItemsByBossId(bossId);
            return ResponseEntity.ok(ApiResponse.success(desireItems));
        } catch (Exception e) {
            log.error("물욕템 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("물욕템 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // 보스 제한 검증 API
    @PostMapping("/validate-limits")
    public ResponseEntity<ApiResponse<ValidateLimitsResponse>> validateLimitsApi(
            @RequestBody ValidateLimitsRequest request) {
        try {
            ValidateLimitsResponse response = bossPresetService.validateLimits(request);
            if (response.getIsValid()) {
                return ResponseEntity.ok(ApiResponse.success(response));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.success("보스 선택 제한을 초과했습니다.", response));
            }
        } catch (Exception e) {
            log.error("보스 제한 검증 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 제한 검증 중 오류가 발생했습니다."));
        }
    }
} 