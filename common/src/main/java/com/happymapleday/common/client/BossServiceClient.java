package com.happymapleday.common.client;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "boss-service", url = "${services.boss-service.url:http://localhost:8081}")
public interface BossServiceClient {
    
    @GetMapping("/api/boss/list")
    ApiResponse<List<BossResponse>> getBossList();
    
    @GetMapping("/api/boss/{id}")
    ApiResponse<BossResponse> getBoss(@PathVariable Long id);
    
    @GetMapping("/api/boss/preset/{characterId}")
    ApiResponse<List<Object>> getBossPreset(@PathVariable Long characterId);
} 