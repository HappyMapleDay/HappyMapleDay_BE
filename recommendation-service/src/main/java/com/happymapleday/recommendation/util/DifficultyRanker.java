package com.happymapleday.recommendation.util;

import java.util.Locale;

public final class DifficultyRanker {

    private DifficultyRanker() {}

    public static int rank(String difficulty) {
        if (difficulty == null) return 0;
        String d = difficulty.trim().toLowerCase(Locale.ROOT);
        return switch (d) {
            case "easy", "이지" -> 1;
            case "normal", "노말" -> 2;
            case "hard", "하드" -> 3;
            case "chaos", "카오스" -> 4;
            case "extreme", "익스트림" -> 5;
            default -> 0;
        };
    }
}


