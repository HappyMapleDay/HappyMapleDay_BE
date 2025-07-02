package com.happymapleday.boss.controller;

import com.happymapleday.boss.dto.response.DesireItemResponse;
import com.happymapleday.boss.service.DesireItemService;
import com.happymapleday.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boss/desire-items")
@RequiredArgsConstructor
@Slf4j
public class DesireItemController {

    private final DesireItemService desireItemService;
    // 보스별 물욕템 목록 조회 API
    @GetMapping("/boss/{bossId}")
    public ResponseEntity<ApiResponse<List<DesireItemResponse>>> getDesireItemsByBossId(@PathVariable Long bossId) {
        try {
            List<DesireItemResponse> desireItems = desireItemService.getDesireItemsByBossId(bossId);
            return ResponseEntity.ok(ApiResponse.success(desireItems));
        } catch (Exception e) {
            log.error("물욕템 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("물욕템 목록 조회 중 오류가 발생했습니다."));
        }
    }
} 