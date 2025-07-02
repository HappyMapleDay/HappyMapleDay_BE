package com.happymapleday.boss.controller;

import com.happymapleday.boss.dto.response.BossResponse;
import com.happymapleday.boss.service.BossService;
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
}