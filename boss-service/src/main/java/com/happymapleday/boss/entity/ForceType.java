package com.happymapleday.boss.entity;

// 보스가 요구하는 포스의 타입

public enum ForceType {
    NONE("포스 불필요"),
    ARCANE("아케인 포스"),
    AUTHENTIC("어센틱 포스");

    private final String description;

    ForceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isForceRequired() {
        return this != NONE;
    }
} 