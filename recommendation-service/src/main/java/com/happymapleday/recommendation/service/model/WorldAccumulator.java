package com.happymapleday.recommendation.service.model;

public class WorldAccumulator {
    private int selectedCount;
    private long crystal;

    public int getSelectedCount() {
        return selectedCount;
    }

    public void incrementSelected() {
        this.selectedCount++;
    }

    public void addCrystal(long value) {
        this.crystal += value;
    }

    public long getCrystal() {
        return crystal;
    }
}


