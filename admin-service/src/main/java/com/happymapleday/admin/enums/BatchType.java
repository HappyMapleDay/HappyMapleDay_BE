package com.happymapleday.admin.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BatchType {
    USER_METRICS("가입 유저 수 집계"),
    BOSS_KILLS("보스 격파 횟수 집계"),
    COMBAT_POWER("전투력 평균 집계"),
    ITEM_DROPS("아이템 드롭 집계"),
    ITEM_SALES("아이템 판매가 집계");

    private final String description;
}

