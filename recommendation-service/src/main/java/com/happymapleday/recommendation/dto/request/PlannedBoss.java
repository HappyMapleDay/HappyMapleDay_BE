package com.happymapleday.recommendation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannedBoss {
    private Long bossId;
    private Integer partySize; // 1~6, 2 이상이면 반드시 포함
    private Boolean alreadyCleared; // 이미 클리어한 경우 반드시 포함
}


