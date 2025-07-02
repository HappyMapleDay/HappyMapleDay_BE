package com.happymapleday.boss.controller;

import com.happymapleday.boss.dto.response.BossPresetResponse;
import com.happymapleday.boss.dto.request.BossPresetApplyRequest;
import com.happymapleday.boss.dto.response.BossPresetApplyResponse;
import com.happymapleday.boss.service.BossPresetService;
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
public class BossPresetController {

    private final BossPresetService bossPresetService;

    // 3. 보스 프리셋 조회 API
    @GetMapping("/presets")
    public ResponseEntity<ApiResponse<List<BossPresetResponse>>> getAllPresets() {
        try {
            List<BossPresetResponse> presets = bossPresetService.getAllPresetsWithBosses();
            return ResponseEntity.ok(ApiResponse.success(presets));
        } catch (Exception e) {
            log.error("프리셋 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("프리셋 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // 4. 보스 프리셋 적용 API
    @PostMapping("/preset/apply")
    public ResponseEntity<ApiResponse<BossPresetApplyResponse>> applyPresetApi(
            @RequestBody BossPresetApplyRequest request) {
        try {
            BossPresetApplyResponse response = bossPresetService.applyPreset(request.getPresetId(), request.getCharacterId());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("보스 프리셋 적용 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스 프리셋 적용 중 오류가 발생했습니다."));
        }
    }
} 