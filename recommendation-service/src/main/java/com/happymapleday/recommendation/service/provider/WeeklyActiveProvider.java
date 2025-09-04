package com.happymapleday.recommendation.service.provider;

import com.happymapleday.common.dto.BossResponse;
import com.happymapleday.recommendation.service.weight.BossWeightCache;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class WeeklyActiveProvider {

    private final BossWeightCache bossWeightCache;

    public WeeklyActiveProvider(BossWeightCache bossWeightCache) {
        this.bossWeightCache = bossWeightCache;
    }

    public List<BossResponse> provideSortedWeekly(List<BossResponse> allBosses) {
        return allBosses.stream()
                .filter(b -> !Boolean.TRUE.equals(b.getIsMonthly()))
                .sorted(Comparator.comparingDouble(this::weightedValue).reversed())
                .toList();
    }

    private double weightedValue(BossResponse b) {
        long price = b.getCrystalPrice() == null ? 0L : b.getCrystalPrice();
        double w = bossWeightCache.getWeight(b.getBossId());
        return price * w;
    }
}


