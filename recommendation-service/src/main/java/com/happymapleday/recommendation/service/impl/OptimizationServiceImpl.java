package com.happymapleday.recommendation.service.impl;

import com.happymapleday.recommendation.dto.request.BossSelection;
import com.happymapleday.recommendation.dto.request.CharacterBossSelection;
import com.happymapleday.recommendation.dto.response.BossRecommendation;
import com.happymapleday.recommendation.dto.response.CharacterRecommendation;
import com.happymapleday.recommendation.dto.response.OptimizationSummary;
import com.happymapleday.recommendation.service.OptimizationService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OptimizationServiceImpl implements OptimizationService {
    
    // 제한 상수
    private static final int CHARACTER_CRYSTAL_LIMIT = 12;
    private static final int WORLD_CRYSTAL_LIMIT = 90;
    
    // 캐릭터별 보스 선택을 기반으로 최적화된 추천 결과를 생성
    @Override
    public List<CharacterRecommendation> optimizeRecommendations(List<CharacterBossSelection> characterBossSelections) {
        List<CharacterRecommendation> recommendations = new ArrayList<>();
        int totalCrystalCount = 0;
        
        // 1. 각 캐릭터별로 파티 보스 우선 포함
        for (CharacterBossSelection selection : characterBossSelections) {
            CharacterRecommendation recommendation = createCharacterRecommendation(selection, totalCrystalCount);
            recommendations.add(recommendation);
            totalCrystalCount += recommendation.getCrystalCount();
        }
        
        // 2. 전체 90개 제한 내에서 추가 최적화
        optimizeWithinGlobalLimit(recommendations, totalCrystalCount);
        
        return recommendations;
    }
    
    // 캐릭터별 추천 생성
    private CharacterRecommendation createCharacterRecommendation(CharacterBossSelection selection, int currentGlobalCrystalCount) {
        List<BossSelection> bossSelections = selection.getBossSelections();
        
        // 같은 보스 이름에서 가장 높은 수익을 가진 난이도만 선택
        Map<String, BossSelection> uniqueBossMap = bossSelections.stream()
                .collect(Collectors.toMap(
                        BossSelection::getBossName,
                        boss -> boss,
                        (existing, replacement) -> existing.getCrystalPrice() >= replacement.getCrystalPrice() ? existing : replacement
                ));
        
        List<BossSelection> filteredBossSelections = new ArrayList<>(uniqueBossMap.values());
        
        // 파티 보스 찾기 (반드시 포함)
        List<BossSelection> partyBosses = filteredBossSelections.stream()
                .filter(BossSelection::isPartyBoss)
                .collect(Collectors.toList());
        
        // 솔로 보스 찾기 (난이도 기준 정렬)
        List<BossSelection> soloBosses = filteredBossSelections.stream()
                .filter(BossSelection::isSoloBoss)
                .sorted(Comparator.comparingLong(BossSelection::getCrystalPrice).reversed())
                .collect(Collectors.toList());
        
        // 최고 난이도 솔로 보스 찾기
        Long highestDifficultySoloBossId = soloBosses.isEmpty() ? null : soloBosses.get(0).getBossId();
        
        List<BossRecommendation> recommendations = new ArrayList<>();
        BigInteger totalIncome = BigInteger.ZERO;
        int crystalCount = 0;
        
        // 1. 파티 보스 우선 포함
        for (BossSelection boss : partyBosses) {
            if (crystalCount >= CHARACTER_CRYSTAL_LIMIT || currentGlobalCrystalCount >= WORLD_CRYSTAL_LIMIT) {
                break;
            }
            
            BossRecommendation recommendation = createBossRecommendation(boss, true, false, true);
            recommendations.add(recommendation);
            totalIncome = totalIncome.add(BigInteger.valueOf(boss.getCrystalPrice()));
            crystalCount++;
            currentGlobalCrystalCount++;
        }
        
        // 2. 솔로 보스 수익 기준 최적화
        for (BossSelection boss : soloBosses) {
            if (crystalCount >= CHARACTER_CRYSTAL_LIMIT || currentGlobalCrystalCount >= WORLD_CRYSTAL_LIMIT) {
                break;
            }
            
            boolean isHighestDifficulty = boss.getBossId().equals(highestDifficultySoloBossId);
            BossRecommendation recommendation = createBossRecommendation(boss, false, isHighestDifficulty, true);
            recommendations.add(recommendation);
            totalIncome = totalIncome.add(BigInteger.valueOf(boss.getCrystalPrice()));
            crystalCount++;
            currentGlobalCrystalCount++;
        }
        
        // 파티 보스 ID 목록 생성
        List<Long> partyBossIds = partyBosses.stream()
                .map(BossSelection::getBossId)
                .collect(Collectors.toList());
        
        return CharacterRecommendation.builder()
                .characterId(selection.getCharacterId())
                .characterName(selection.getCharacterName())
                .characterLevel(selection.getCharacterLevel())
                .crystalCount(crystalCount)
                .expectedIncome(totalIncome)
                .bossRecommendations(recommendations)
                .highestDifficultySoloBossId(highestDifficultySoloBossId)
                .partyBossIds(partyBossIds)
                .build();
    }
    
    // 보스 추천 생성
    private BossRecommendation createBossRecommendation(BossSelection boss, boolean isPartyBoss, 
                                                        boolean isHighestDifficultySolo, boolean isIncluded) {
        return BossRecommendation.builder()
                .bossId(boss.getBossId())
                .bossName(boss.getBossName())
                .difficulty(boss.getDifficulty())
                .crystalPrice(boss.getCrystalPrice())
                .partySize(boss.getPartySize())
                .expectedIncome(BigInteger.valueOf(boss.getCrystalPrice()))
                .isPartyBoss(isPartyBoss)
                .isHighestDifficultySolo(isHighestDifficultySolo)
                .isIncludedInOptimization(isIncluded)
                .build();
    }
    
    // 전체 제한 내에서 추가 최적화
    private void optimizeWithinGlobalLimit(List<CharacterRecommendation> recommendations, int totalCrystalCount) {
        if (totalCrystalCount >= WORLD_CRYSTAL_LIMIT) {
            // 90개 제한 초과 시 수익 기준으로 재조정
            adjustForGlobalLimit(recommendations);
        }
    }
    
    // 전체 제한 초과 시 재조정
    private void adjustForGlobalLimit(List<CharacterRecommendation> recommendations) {
        List<BossRecommendation> allBosses = new ArrayList<>();
        Map<BossRecommendation, Long> bossToCharacterMap = new HashMap<>();
        
        for (CharacterRecommendation charRec : recommendations) {
            for (BossRecommendation bossRec : charRec.getBossRecommendations()) {
                allBosses.add(bossRec);
                bossToCharacterMap.put(bossRec, charRec.getCharacterId());
            }
        }
        
        // 파티 보스 우선, 그 다음 수익 순으로 정렬
        allBosses.sort((a, b) -> {
            if (a.isPartyBoss() && !b.isPartyBoss()) return -1;
            if (!a.isPartyBoss() && b.isPartyBoss()) return 1;
            return Long.compare(b.getCrystalPrice(), a.getCrystalPrice());
        });
        
        // 상위 90개만 유지
        Set<BossRecommendation> selectedBosses = new HashSet<>(allBosses.subList(0, Math.min(WORLD_CRYSTAL_LIMIT, allBosses.size())));
        
        // 각 캐릭터별로 선택된 보스만 유지
        for (CharacterRecommendation charRec : recommendations) {
            List<BossRecommendation> filteredBosses = charRec.getBossRecommendations().stream()
                    .filter(selectedBosses::contains)
                    .collect(Collectors.toList());
            
            CharacterRecommendation updatedRec = CharacterRecommendation.builder()
                    .characterId(charRec.getCharacterId())
                    .characterName(charRec.getCharacterName())
                    .characterLevel(charRec.getCharacterLevel())
                    .crystalCount(filteredBosses.size())
                    .expectedIncome(filteredBosses.stream()
                            .map(BossRecommendation::getExpectedIncome)
                            .reduce(BigInteger.ZERO, BigInteger::add))
                    .bossRecommendations(filteredBosses)
                    .highestDifficultySoloBossId(charRec.getHighestDifficultySoloBossId())
                    .partyBossIds(charRec.getPartyBossIds())
                    .build();
            
            // 원본 리스트 업데이트
            int index = recommendations.indexOf(charRec);
            recommendations.set(index, updatedRec);
        }
    }
    
    // 최적화 요약 생성
    @Override
    public OptimizationSummary createOptimizationSummary(List<CharacterRecommendation> recommendations) {
        int totalPartyBossCount = 0;
        int totalSoloBossCount = 0;
        int charactersWithMaxCrystal = 0;
        int totalCrystalCount = 0;
        
        for (CharacterRecommendation rec : recommendations) {
            totalCrystalCount += rec.getCrystalCount();
            
            if (rec.getCrystalCount() >= CHARACTER_CRYSTAL_LIMIT) {
                charactersWithMaxCrystal++;
            }
            
            for (BossRecommendation bossRec : rec.getBossRecommendations()) {
                if (bossRec.isPartyBoss()) {
                    totalPartyBossCount++;
                } else {
                    totalSoloBossCount++;
                }
            }
        }
        
        boolean isWorldCrystalLimitReached = totalCrystalCount >= WORLD_CRYSTAL_LIMIT;
        String message = isWorldCrystalLimitReached ? 
                "90개 결정석 제한에 도달했습니다." : 
                "최적화가 완료되었습니다.";
        
        return OptimizationSummary.builder()
                .isOptimized(true)
                .optimizationMessage(message)
                .totalPartyBossCount(totalPartyBossCount)
                .totalSoloBossCount(totalSoloBossCount)
                .charactersWithMaxCrystal(charactersWithMaxCrystal)
                .isWorldCrystalLimitReached(isWorldCrystalLimitReached)
                .build();
    }
} 