package com.happymapleday.character.dto.response;

import com.happymapleday.common.dto.BossResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterSelectedBossesResponse {
    private Long characterId;
    private String characterName;
    private List<BossResponse> selectedBosses;
}


