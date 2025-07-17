package com.happymapleday.recommendation.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterRecommendation {
    
    private Long characterId;
    private String characterName;  
    private Integer characterLevel;  
    private Integer crystalCount;  
    private BigInteger expectedIncome;  
    private List<BossRecommendation> bossRecommendations; 
    private Long highestDifficultySoloBossId; // 1인 플레이 기준 가장 난이도 높은 보스 
    private List<Long> partyBossIds; // 2명 이상으로 클리어하는 보스들
} 