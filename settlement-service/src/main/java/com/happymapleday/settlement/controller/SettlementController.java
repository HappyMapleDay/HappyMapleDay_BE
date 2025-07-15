package com.happymapleday.settlement.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.settlement.dto.request.SettlementRequest;
import com.happymapleday.settlement.dto.response.SettlementCompleteResponse;
import com.happymapleday.settlement.dto.response.CurrentWeekStatusResponse;
import com.happymapleday.settlement.dto.response.SettlementStatusResponse;
import com.happymapleday.settlement.dto.response.SettlementDetailResponse;
import com.happymapleday.settlement.service.SettlementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/settlement")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    // 특정 주차 정산 조회 (요약 정보)
    @GetMapping("/user/{userId}/week/{weekStartDate}")
    public ResponseEntity<ApiResponse<SettlementStatusResponse>> getSettlementByWeek(
            @PathVariable Long userId,
            @PathVariable String weekStartDate) {
        LocalDate weekStart = LocalDate.parse(weekStartDate);
        SettlementStatusResponse response = settlementService.getSettlementStatus(userId, weekStart);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    // 특정 주차 정산 상세 조회 (상세 정보)
    @GetMapping("/user/{userId}/week/{weekStartDate}/detail")
    public ResponseEntity<ApiResponse<SettlementDetailResponse>> getSettlementDetailByWeek(
            @PathVariable Long userId,
            @PathVariable String weekStartDate) {
        LocalDate weekStart = LocalDate.parse(weekStartDate);
        SettlementDetailResponse response = settlementService.getSettlementDetail(userId, weekStart);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 정산 데이터 생성 또는 수정
    @PutMapping("/user/{userId}/week/{weekStartDate}")
    public ResponseEntity<ApiResponse<SettlementCompleteResponse>> upsertSettlement(
            @PathVariable Long userId,
            @PathVariable String weekStartDate,
            @Valid @RequestBody SettlementRequest request) {
        LocalDate weekStart = LocalDate.parse(weekStartDate);
        SettlementCompleteResponse response = settlementService.upsertSettlement(userId, weekStart, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 자동 저장용 API (PENDING 상태로 저장)
    @PostMapping("/user/{userId}/week/{weekStartDate}/auto-save")
    public ResponseEntity<ApiResponse<SettlementCompleteResponse>> autoSaveSettlement(
            @PathVariable Long userId,
            @PathVariable String weekStartDate,
            @Valid @RequestBody SettlementRequest request) {
        LocalDate weekStart = LocalDate.parse(weekStartDate);
        SettlementCompleteResponse response = settlementService.autoSaveSettlement(userId, weekStart, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
 
    // 정산 삭제
    @DeleteMapping("/user/{userId}/settlement/{settlementId}")
    public ResponseEntity<ApiResponse<String>> deleteSettlement(
            @PathVariable Long userId,
            @PathVariable Long settlementId) {
        settlementService.deleteSettlement(settlementId, userId);
        return ResponseEntity.ok(ApiResponse.success("정산이 성공적으로 삭제되었습니다."));
    }

    // 현재 주차 정산 현황 조회
    @GetMapping("/user/{userId}/current-week")
    public ResponseEntity<ApiResponse<CurrentWeekStatusResponse>> getCurrentWeekStatus(
            @PathVariable Long userId) {
        CurrentWeekStatusResponse response = settlementService.getCurrentWeekStatus(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
} 