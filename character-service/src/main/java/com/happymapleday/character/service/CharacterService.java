package com.happymapleday.character.service;

import com.happymapleday.character.dto.request.CharacterCreateRequest;
import com.happymapleday.character.dto.response.CharacterResponse;
import com.happymapleday.character.entity.Character;
import com.happymapleday.character.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharacterService {
    
    private final CharacterRepository characterRepository;
    
    /**
     * 유저별 전체 캐릭터 목록 조회
     */
    public List<CharacterResponse> getAllCharactersByUserId(Long userId) {
        List<Character> characters = characterRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        if (characters.isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
        }
        
        return characters.stream()
                .map(CharacterResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 캐릭터 추가
     */
    @Transactional
    public CharacterResponse createCharacter(CharacterCreateRequest request) {
        // 필수 정보 검증
        if (request.getUserId() == null || request.getCharacterName() == null || request.getOcid() == null) {
            throw new IllegalArgumentException("필수 정보가 누락되었습니다.");
        }
        
        // 중복 캐릭터 검증 (OCID 기준)
        if (characterRepository.findByUserIdAndOcid(request.getUserId(), request.getOcid()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 캐릭터입니다.");
        }
        
        // 캐릭터 생성
        Character character = new Character(
                request.getUserId(),
                request.getCharacterName(),
                request.getOcid()
        );
        
        Character savedCharacter = characterRepository.save(character);
        return CharacterResponse.from(savedCharacter);
    }
} 