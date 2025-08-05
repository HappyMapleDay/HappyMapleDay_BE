package com.happymapleday.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class RecommendationApplicationTest {

    @Test
    @DisplayName("애플리케이션 컨텍스트 로드 테스트")
    void contextLoads() {
        // Spring Boot 애플리케이션이 정상적으로 시작되는지 확인
        // 별도의 assertion 없이 컨텍스트가 로드되면 성공
    }
} 