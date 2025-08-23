package com.happymapleday.recommendation.service.weight;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BossWeightCache {

    private final ObjectMapper objectMapper;
    private final Map<Long, Double> weightByBossId = new ConcurrentHashMap<>();

    public BossWeightCache(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void load() {
        Map<Long, Double> loaded = readWeightsFromResource("boss-weights.json");
        weightByBossId.clear();
        weightByBossId.putAll(loaded);
    }

    public double getWeight(Long bossId) {
        if (bossId == null) return 1.0d;
        return weightByBossId.getOrDefault(bossId, 1.0d);
    }

    public Map<Long, Double> snapshot() {
        return Collections.unmodifiableMap(new HashMap<>(weightByBossId));
    }

    private Map<Long, Double> readWeightsFromResource(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            if (!resource.exists()) return Collections.emptyMap();
            try (InputStream is = resource.getInputStream()) {
                Map<String, Double> raw = objectMapper.readValue(is, new TypeReference<Map<String, Double>>(){});
                Map<Long, Double> converted = new HashMap<>();
                for (Map.Entry<String, Double> e : raw.entrySet()) {
                    try {
                        Long id = Long.valueOf(e.getKey());
                        Double w = e.getValue() == null ? 1.0d : e.getValue();
                        if (w <= 0) continue;
                        converted.put(id, w);
                    } catch (NumberFormatException ignored) {
                    }
                }
                return converted;
            }
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }
}


