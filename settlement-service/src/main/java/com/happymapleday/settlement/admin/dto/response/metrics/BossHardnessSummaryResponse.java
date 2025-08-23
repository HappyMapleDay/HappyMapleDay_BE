package com.happymapleday.settlement.admin.dto.response.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BossHardnessSummaryResponse {

    private Long totalCount;
    private List<BossHardnessByJobResponse> byJob;
}


