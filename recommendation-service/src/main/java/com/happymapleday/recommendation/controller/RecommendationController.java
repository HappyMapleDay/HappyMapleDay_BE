package com.happymapleday.recommendation.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.recommendation.dto.request.RecommendationRequest;
import com.happymapleday.recommendation.dto.response.RecommendationResponse;
import com.happymapleday.recommendation.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }
    
    // 캐릭터별 보스 선택 기반 최적화 추천
    @PostMapping("/optimize")
    public ResponseEntity<ApiResponse<RecommendationResponse>> generateOptimizedRecommendation(
            @Valid @RequestBody RecommendationRequest request) {
        
        RecommendationResponse response = recommendationService.generateRecommendation(request);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    // 헬스 체크용 엔드포인트
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Recommendation service is running"));
    }
} 