package com.happymapleday.recommendation.service.model;

import lombok.Getter;

@Getter
public class WorldAccumulator {
    private int selectedCount;
    private long crystal;

    public void incrementSelected() {
        this.selectedCount++;
    }

    public void addCrystal(long value) {
        this.crystal += value;
    }

}


