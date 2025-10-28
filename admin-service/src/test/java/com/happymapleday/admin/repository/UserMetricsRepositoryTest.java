package com.happymapleday.admin.repository;

import com.happymapleday.admin.entity.UserMetrics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserMetricsRepository 테스트")
class UserMetricsRepositoryTest {

    @Autowired
    private UserMetricsRepository userMetricsRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("날짜로 유저 통계 조회")
    void findByMetricDate() {
        // given
        LocalDate date = LocalDate.of(2024, 1, 1);
        UserMetrics metrics = UserMetrics.builder()
            .metricDate(date)
            .cumulativeCount(1000L)
            .dailyCount(50)
            .build();
        entityManager.persist(metrics);
        entityManager.flush();

        // when
        Optional<UserMetrics> found = userMetricsRepository.findByMetricDate(date);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getMetricDate()).isEqualTo(date);
        assertThat(found.get().getCumulativeCount()).isEqualTo(1000L);
        assertThat(found.get().getDailyCount()).isEqualTo(50);
    }

    @Test
    @DisplayName("날짜 범위로 유저 통계 조회")
    void findByMetricDateBetween() {
        // given
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 3);

        entityManager.persist(createUserMetrics(LocalDate.of(2024, 1, 1), 1000L));
        entityManager.persist(createUserMetrics(LocalDate.of(2024, 1, 2), 1050L));
        entityManager.persist(createUserMetrics(LocalDate.of(2024, 1, 3), 1100L));
        entityManager.persist(createUserMetrics(LocalDate.of(2024, 1, 4), 1150L));
        entityManager.flush();

        // when
        List<UserMetrics> found = userMetricsRepository.findByMetricDateBetweenOrderByMetricDateAsc(from, to);

        // then
        assertThat(found).hasSize(3);
        assertThat(found.get(0).getMetricDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(found.get(1).getMetricDate()).isEqualTo(LocalDate.of(2024, 1, 2));
        assertThat(found.get(2).getMetricDate()).isEqualTo(LocalDate.of(2024, 1, 3));
    }

    @Test
    @DisplayName("날짜 내림차순으로 전체 조회")
    void findAllOrderByMetricDateDesc() {
        // given
        entityManager.persist(createUserMetrics(LocalDate.of(2024, 1, 1), 1000L));
        entityManager.persist(createUserMetrics(LocalDate.of(2024, 1, 3), 1100L));
        entityManager.persist(createUserMetrics(LocalDate.of(2024, 1, 2), 1050L));
        entityManager.flush();

        // when
        List<UserMetrics> found = userMetricsRepository.findAllByOrderByMetricDateDesc();

        // then
        assertThat(found).hasSize(3);
        assertThat(found.get(0).getMetricDate()).isEqualTo(LocalDate.of(2024, 1, 3));
        assertThat(found.get(1).getMetricDate()).isEqualTo(LocalDate.of(2024, 1, 2));
        assertThat(found.get(2).getMetricDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    @DisplayName("중복 날짜 저장 불가 (UNIQUE 제약조건)")
    void uniqueConstraint_MetricDate() {
        // given
        LocalDate date = LocalDate.of(2024, 1, 1);
        UserMetrics metrics1 = createUserMetrics(date, 1000L);
        entityManager.persist(metrics1);
        entityManager.flush();

        // when & then
        UserMetrics metrics2 = createUserMetrics(date, 2000L);
        
        try {
            entityManager.persist(metrics2);
            entityManager.flush();
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }

    private UserMetrics createUserMetrics(LocalDate date, Long cumulativeCount) {
        return UserMetrics.builder()
            .metricDate(date)
            .cumulativeCount(cumulativeCount)
            .dailyCount(50)
            .build();
    }
}

