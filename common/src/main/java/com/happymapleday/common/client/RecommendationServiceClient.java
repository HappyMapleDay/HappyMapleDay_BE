package com.happymapleday.common.client;

import com.happymapleday.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "recommendation-service", url = "${services.recommendation-service.url:http://localhost:8085}")
public interface RecommendationServiceClient {
    
    @PostMapping("/api/recommendation/optimize")
    ApiResponse<Object> getOptimizedRecommendation(@RequestBody Object optimizeRequest);
    
    @GetMapping("/api/recommendation/{userId}/latest")
    ApiResponse<Object> getLatestRecommendation(@PathVariable Long userId);
    
    @GetMapping("/api/recommendation/{userId}/history")
    ApiResponse<Object> getRecommendationHistory(@PathVariable Long userId);
} 