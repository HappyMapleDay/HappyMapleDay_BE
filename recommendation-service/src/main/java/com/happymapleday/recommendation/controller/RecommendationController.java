package com.happymapleday.recommendation.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.recommendation.dto.request.OptimizeRecommendationRequest;
import com.happymapleday.recommendation.dto.response.OptimizedRecommendationResponse;
import com.happymapleday.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/optimize")
    public ApiResponse<OptimizedRecommendationResponse> optimize(@RequestBody OptimizeRecommendationRequest request) {
        OptimizedRecommendationResponse response = recommendationService.optimize(request);
        return ApiResponse.success(response);
    }
}