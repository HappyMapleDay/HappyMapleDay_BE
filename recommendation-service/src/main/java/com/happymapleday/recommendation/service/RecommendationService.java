package com.happymapleday.recommendation.service;

import com.happymapleday.recommendation.dto.request.OptimizeRecommendationRequest;
import com.happymapleday.recommendation.dto.response.OptimizedRecommendationResponse;

public interface RecommendationService {
    OptimizedRecommendationResponse optimize(OptimizeRecommendationRequest request);
}


