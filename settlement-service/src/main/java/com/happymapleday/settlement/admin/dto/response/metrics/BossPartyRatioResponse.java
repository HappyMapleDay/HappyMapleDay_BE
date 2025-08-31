package com.happymapleday.settlement.admin.dto.response.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BossPartyRatioResponse {

    private Long bossId;
    private Long soloCount;
    private Long partyCount;
}


