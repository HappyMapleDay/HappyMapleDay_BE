package com.happymapleday.recommendation.service;

import com.happymapleday.recommendation.dto.request.RecommendationRequest;
import com.happymapleday.recommendation.dto.response.RecommendationResponse;

public interface RecommendationService {
    
    // 캐릭터별 보스 선택을 기반으로 최적화된 추천 결과를 생성
    RecommendationResponse generateRecommendation(RecommendationRequest request);
} 