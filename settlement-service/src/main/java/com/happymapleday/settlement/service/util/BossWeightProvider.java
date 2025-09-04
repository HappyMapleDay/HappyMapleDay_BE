package com.happymapleday.settlement.service.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class BossWeightProvider {

    private final Map<Long, BigDecimal> bossIdToWeight;

    public BossWeightProvider() {
        this.bossIdToWeight = loadWeightsFromClasspath("boss-weights.json");
    }

    public BigDecimal getWeightOrDefault(Long bossId, BigDecimal defaultWeight) {
        if (bossId == null) {
            return defaultWeight;
        }
        return bossIdToWeight.getOrDefault(bossId, defaultWeight);
    }

    private Map<Long, BigDecimal> loadWeightsFromClasspath(String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, BigDecimal> raw = objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, BigDecimal>>(){});
            Map<Long, BigDecimal> converted = new HashMap<>();
            for (Map.Entry<String, BigDecimal> e : raw.entrySet()) {
                converted.put(Long.valueOf(e.getKey()), e.getValue());
            }
            return Collections.unmodifiableMap(converted);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }
}


