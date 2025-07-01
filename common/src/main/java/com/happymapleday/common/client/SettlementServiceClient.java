package com.happymapleday.common.client;

import com.happymapleday.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "settlement-service", url = "${services.settlement-service.url:http://localhost:8084}")
public interface SettlementServiceClient {
    
    @GetMapping("/api/settlement/{userId}/weekly")
    ApiResponse<List<Object>> getWeeklySettlement(@PathVariable Long userId);
    
    @PostMapping("/api/settlement/complete")
    ApiResponse<Object> completeSettlement(@RequestBody Object settlementRequest);
    
    @GetMapping("/api/settlement/{userId}/summary")
    ApiResponse<Object> getSettlementSummary(@PathVariable Long userId);
} 