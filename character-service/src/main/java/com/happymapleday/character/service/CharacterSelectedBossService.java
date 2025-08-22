package com.happymapleday.character.service;

import com.happymapleday.character.dto.response.CharacterSelectedBossesResponse;
import com.happymapleday.character.entity.Character;
import com.happymapleday.character.entity.CharacterSelectedBoss;
import com.happymapleday.character.repository.CharacterRepository;
import com.happymapleday.character.repository.CharacterSelectedBossRepository;
import com.happymapleday.common.client.BossServiceClient;
import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.common.dto.BossResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharacterSelectedBossService {

    private final CharacterRepository characterRepository;
    private final CharacterSelectedBossRepository characterSelectedBossRepository;
    private final BossServiceClient bossServiceClient;

    public List<CharacterSelectedBossesResponse> getSelectedBossDetailsByCharacterIds(List<Long> characterIds) {
        if (characterIds == null || characterIds.isEmpty()) {
            return List.of();
        }

        // 캐릭터 조회
        Map<Long, Character> characterMap = characterRepository.findAllById(characterIds)
                .stream()
                .collect(Collectors.toMap(Character::getId, c -> c));

        if (characterMap.isEmpty()) {
            return List.of();
        }

        // 매핑 테이블에서 선택 보스 ID 수집
        List<CharacterSelectedBoss> mappings = characterSelectedBossRepository.findByCharacterIdIn(characterIds);
        Map<Long, List<Long>> characterIdToBossIds = new HashMap<>();
        for (CharacterSelectedBoss mapping : mappings) {
            characterIdToBossIds.computeIfAbsent(mapping.getCharacterId(), k -> new ArrayList<>())
                    .add(mapping.getBossId());
        }

        // 모든 보스 ID를 합쳐 한 번에 조회
        List<Long> allBossIds = characterIdToBossIds.values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, BossResponse> bossIdToDetail = Collections.emptyMap();
        if (!allBossIds.isEmpty()) {
            ApiResponse<List<BossResponse>> bossApiResponse = bossServiceClient.getBossesByIds(allBossIds);
            List<BossResponse> bossDetails = Optional.ofNullable(bossApiResponse.getData()).orElse(List.of());
            bossIdToDetail = bossDetails.stream()
                    .collect(Collectors.toMap(BossResponse::getBossId, b -> b, (a, b) -> a));
        }

        // 캐릭터별 응답 구성
        List<CharacterSelectedBossesResponse> result = new ArrayList<>();
        for (Long characterId : characterIds) {
            Character character = characterMap.get(characterId);
            if (character == null) {
                continue;
            }
            List<BossResponse> selectedBosses = characterIdToBossIds.getOrDefault(characterId, List.of())
                    .stream()
                    .map(bossIdToDetail::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            result.add(CharacterSelectedBossesResponse.builder()
                    .characterId(character.getId())
                    .characterName(character.getCharacterName())
                    .selectedBosses(selectedBosses)
                    .build());
        }

        return result;
    }
}


