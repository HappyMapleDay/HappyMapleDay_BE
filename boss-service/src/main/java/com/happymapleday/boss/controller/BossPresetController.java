package com.happymapleday.boss.controller;

import com.happymapleday.boss.dto.BossPresetDto;
import com.happymapleday.boss.service.BossPresetService;
import com.happymapleday.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boss-presets")
@RequiredArgsConstructor
@Slf4j
public class BossPresetController {

    private final BossPresetService bossPresetService;

    // 모든 프리셋 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<BossPresetDto.SimpleResponse>>> getAllPresets() {
        try {
            List<BossPresetDto.SimpleResponse> presets = bossPresetService.getAllPresets();
            return ResponseEntity.ok(ApiResponse.success("프리셋 목록 조회가 완료되었습니다.", presets));
        } catch (Exception e) {
            log.error("프리셋 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("프리셋 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // ID로 프리셋 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BossPresetDto.Response>> getPresetById(@PathVariable Long id) {
        try {
            BossPresetDto.Response preset = bossPresetService.getPresetById(id);
            return ResponseEntity.ok(ApiResponse.success("프리셋 상세 조회가 완료되었습니다.", preset));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("프리셋 상세 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("프리셋 상세 조회 중 오류가 발생했습니다."));
        }
    }

    // 프리셋명으로 조회
    @GetMapping("/name/{presetName}")
    public ResponseEntity<ApiResponse<BossPresetDto.Response>> getPresetByName(@PathVariable String presetName) {
        try {
            BossPresetDto.Response preset = bossPresetService.getPresetByName(presetName);
            return ResponseEntity.ok(ApiResponse.success("프리셋 조회가 완료되었습니다.", preset));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("프리셋 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("프리셋 조회 중 오류가 발생했습니다."));
        }
    }

    // 프리셋명으로 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BossPresetDto.SimpleResponse>>> searchPresetsByName(
            @RequestParam String presetName) {
        try {
            List<BossPresetDto.SimpleResponse> presets = bossPresetService.searchPresetsByName(presetName);
            return ResponseEntity.ok(ApiResponse.success("프리셋 검색이 완료되었습니다.", presets));
        } catch (Exception e) {
            log.error("프리셋 검색 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("프리셋 검색 중 오류가 발생했습니다."));
        }
    }

    // 특정 보스를 포함하는 프리셋 조회
    @GetMapping("/containing-boss/{bossId}")
    public ResponseEntity<ApiResponse<List<BossPresetDto.SimpleResponse>>> getPresetsContainingBoss(
            @PathVariable Long bossId) {
        try {
            List<BossPresetDto.SimpleResponse> presets = bossPresetService.getPresetsContainingBoss(bossId);
            return ResponseEntity.ok(ApiResponse.success("보스를 포함하는 프리셋 조회가 완료되었습니다.", presets));
        } catch (Exception e) {
            log.error("보스를 포함하는 프리셋 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("보스를 포함하는 프리셋 조회 중 오류가 발생했습니다."));
        }
    }

    // 프리셋 생성
    @PostMapping
    public ResponseEntity<ApiResponse<BossPresetDto.Response>> createPreset(
            @RequestBody BossPresetDto.CreateRequest createRequest) {
        try {
            BossPresetDto.Response preset = bossPresetService.createPreset(createRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("프리셋이 성공적으로 생성되었습니다.", preset));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("프리셋 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("프리셋 생성 중 오류가 발생했습니다."));
        }
    }

    // 프리셋 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BossPresetDto.Response>> updatePreset(
            @PathVariable Long id,
            @RequestBody BossPresetDto.UpdateRequest updateRequest) {
        try {
            BossPresetDto.Response preset = bossPresetService.updatePreset(id, updateRequest);
            return ResponseEntity.ok(ApiResponse.success("프리셋이 성공적으로 수정되었습니다.", preset));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("프리셋 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("프리셋 수정 중 오류가 발생했습니다."));
        }
    }

    // 프리셋 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePreset(@PathVariable Long id) {
        try {
            bossPresetService.deletePreset(id);
            return ResponseEntity.ok(ApiResponse.success("프리셋이 성공적으로 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("프리셋 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("프리셋 삭제 중 오류가 발생했습니다."));
        }
    }

    // 프리셋에 보스 추가
    @PostMapping("/{presetId}/bosses/{bossId}")
    public ResponseEntity<ApiResponse<BossPresetDto.Response>> addBossToPreset(
            @PathVariable Long presetId,
            @PathVariable Long bossId) {
        try {
            BossPresetDto.Response preset = bossPresetService.addBossToPreset(presetId, bossId);
            return ResponseEntity.ok(ApiResponse.success("프리셋에 보스가 성공적으로 추가되었습니다.", preset));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("프리셋에 보스 추가 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("프리셋에 보스 추가 중 오류가 발생했습니다."));
        }
    }

    // 프리셋에서 보스 제거
    @DeleteMapping("/{presetId}/bosses/{bossId}")
    public ResponseEntity<ApiResponse<BossPresetDto.Response>> removeBossFromPreset(
            @PathVariable Long presetId,
            @PathVariable Long bossId) {
        try {
            BossPresetDto.Response preset = bossPresetService.removeBossFromPreset(presetId, bossId);
            return ResponseEntity.ok(ApiResponse.success("프리셋에서 보스가 성공적으로 제거되었습니다.", preset));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("프리셋에서 보스 제거 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("프리셋에서 보스 제거 중 오류가 발생했습니다."));
        }
    }
} 