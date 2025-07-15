package com.happymapleday.character.service;

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
     * 서버별 캐릭터 목록 조회
     */
    public List<CharacterResponse> getCharactersByServer(Long userId, String serverName) {
        List<Character> characters = characterRepository.findByUserIdAndServerNameOrderByCreatedAtDesc(userId, serverName);
        
        if (characters.isEmpty()) {
            throw new IllegalArgumentException("해당 서버에 등록된 캐릭터가 없습니다.");
        }
        
        return characters.stream()
                .map(CharacterResponse::from)
                .collect(Collectors.toList());
    }
    
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
} 