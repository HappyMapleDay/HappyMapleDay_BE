package com.happymapleday.recommendation.service.provider;

import com.happymapleday.common.dto.BossResponse;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class WeeklyActiveProvider {
    public List<BossResponse> provideSortedWeekly(List<BossResponse> allBosses) {
        return allBosses.stream()
                .filter(b -> !Boolean.TRUE.equals(b.getIsMonthly()))
                .sorted(Comparator.comparingLong(BossResponse::getCrystalPrice).reversed())
                .toList();
    }
}


