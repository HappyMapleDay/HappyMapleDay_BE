package com.happymapleday.settlement.service;

import com.happymapleday.settlement.dto.request.SettlementCompleteRequest;
import com.happymapleday.settlement.dto.request.SettlementModifyRequest;
import com.happymapleday.settlement.dto.response.CurrentWeekStatusResponse;
import com.happymapleday.settlement.dto.response.SettlementCompleteResponse;
import com.happymapleday.settlement.dto.response.SettlementModifyResponse;
import com.happymapleday.settlement.dto.response.SettlementStatusResponse;

import java.time.LocalDate;

// Settlement Service 인터페이스 - 정산 완료, 수정, 삭제, 조회 등의 비즈니스 로직을 정의
public interface SettlementService {
    
    // 이번 주 보돌 완료 (정산 실행)
    SettlementCompleteResponse completeSettlement(SettlementCompleteRequest request);
    
    // 이번 주 정산 내용 수정
    SettlementModifyResponse modifySettlement(Long settlementId, SettlementModifyRequest request);
    
    // 정산 삭제 (완료 취소)
    void deleteSettlement(Long settlementId, Long userId);
    
    // 정산 상태 조회
    SettlementStatusResponse getSettlementStatus(Long userId, LocalDate weekStartDate);
    
    // 현재 주간 상태 확인
    CurrentWeekStatusResponse getCurrentWeekStatus(Long userId);
} 