package com.happymapleday.settlement.admin.service.util;

import com.happymapleday.settlement.service.util.WeekCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class MetricsQueryHelper {

    private final WeekCalculator weekCalculator;

    public LocalDate normalizeTo(LocalDate to) {
        LocalDate base = to != null ? to : LocalDate.now();
        return weekCalculator.getWeekStartDate(base);
    }

    public LocalDate normalizeFrom(LocalDate from, LocalDate normalizedTo, String range) {
        if (from != null) {
            return weekCalculator.getWeekStartDate(from);
        }
        if (range == null || range.isEmpty()) {
            return normalizedTo.minusWeeks(3);
        }
        String r = range.toLowerCase();
        if (r.equals("all")) {
            return null;
        }
        if (r.endsWith("w")) {
            int weeks = parseNumber(r.substring(0, r.length() - 1), 4);
            int back = Math.max(1, weeks);
            return normalizedTo.minusWeeks(back - 1);
        }
        if (r.endsWith("m")) {
            int months = parseNumber(r.substring(0, r.length() - 1), 3);
            LocalDate candidate = normalizedTo.minusMonths(Math.max(1, months));
            return weekCalculator.getWeekStartDate(candidate);
        }
        return normalizedTo.minusWeeks(3);
    }


    private int parseNumber(String s, int defaultVal) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return defaultVal;
        }
    }
}


