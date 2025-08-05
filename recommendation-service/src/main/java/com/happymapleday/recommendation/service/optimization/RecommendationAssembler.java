package com.happymapleday.recommendation.service.optimization;

import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RecommendationAssembler {
    
    // CharacterRecommendation 객체들 생성
    public List<CharacterRecommendation> createCharacterRecommendations(
            List<CharacterBossSelection> characterBossSelections,
            Map<Long, List<BossRecommendation>> characterRecommendations,
            Map<Long, Long> characterHighestDifficultySoloBossIds) {
        
        List<CharacterRecommendation> results = new ArrayList<>();
        
        for (CharacterBossSelection selection : characterBossSelections) {
            List<BossRecommendation> recommendations = characterRecommendations.getOrDefault(
                    selection.getCharacterId(), new ArrayList<>());
            
            BigInteger totalIncome = calculateTotalIncome(recommendations);
            List<Long> partyBossIds = extractPartyBossIds(recommendations);
            
            // 해당 캐릭터의 가장 어려운 솔로 보스 ID 사용
            Long characterHighestDifficultySoloBossId = characterHighestDifficultySoloBossIds.get(selection.getCharacterId());
            
            CharacterRecommendation characterRec = CharacterRecommendation.builder()
                    .characterId(selection.getCharacterId())
                    .characterName(selection.getCharacterName())
                    .characterLevel(selection.getCharacterLevel())
                    .crystalCount(recommendations.size())
                    .expectedIncome(totalIncome)
                    .bossRecommendations(recommendations)
                    .highestDifficultySoloBossId(characterHighestDifficultySoloBossId)
                    .partyBossIds(partyBossIds)
                    .build();
            
            results.add(characterRec);
        }
        
        return results;
    }
    
    // 총 수익 계산
    private BigInteger calculateTotalIncome(List<BossRecommendation> recommendations) {
        return recommendations.stream()
                .map(BossRecommendation::getExpectedIncome)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }
    
    // 파티 보스 ID 목록 추출
    private List<Long> extractPartyBossIds(List<BossRecommendation> recommendations) {
        return recommendations.stream()
                .filter(BossRecommendation::isPartyBoss)
                .map(BossRecommendation::getBossId)
                .collect(Collectors.toList());
    }
} 