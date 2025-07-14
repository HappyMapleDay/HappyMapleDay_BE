package com.happymapleday.settlement.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.settlement.dto.request.SettlementRequest;
import com.happymapleday.settlement.dto.response.SettlementCompleteResponse;
import com.happymapleday.settlement.dto.response.CurrentWeekStatusResponse;
import com.happymapleday.settlement.dto.response.SettlementStatusResponse;
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



    // 정산 삭제
    @DeleteMapping("/{settlementId}")
    public ResponseEntity<ApiResponse<String>> deleteSettlement(
            @PathVariable Long settlementId,
            @RequestParam Long userId) {
        settlementService.deleteSettlement(settlementId, userId);
        return ResponseEntity.ok(ApiResponse.success("정산이 성공적으로 삭제되었습니다."));
    }

    // 정산 현황 조회
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<SettlementStatusResponse>> getSettlementStatus(
            @RequestParam Long userId,
            @RequestParam String weekStartDate) {
        LocalDate startDate = LocalDate.parse(weekStartDate);
        SettlementStatusResponse response = settlementService.getSettlementStatus(userId, startDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 현재 주차 정산 현황 조회
    @GetMapping("/current-week")
    public ResponseEntity<ApiResponse<CurrentWeekStatusResponse>> getCurrentWeekStatus(
            @RequestParam Long userId) {
        CurrentWeekStatusResponse response = settlementService.getCurrentWeekStatus(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
} 