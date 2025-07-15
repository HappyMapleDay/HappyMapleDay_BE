package com.happymapleday.character.service;

import com.happymapleday.character.dto.request.CharacterCreateRequest;
import com.happymapleday.character.dto.request.CharacterBulkCreateRequest;
import com.happymapleday.character.dto.response.CharacterResponse;
import com.happymapleday.character.dto.response.CharacterBulkCreateResponse;
import com.happymapleday.character.dto.response.MainCharacterSettingResponse;
import com.happymapleday.character.dto.response.MainCharacterResponse;
import com.happymapleday.character.entity.Character;
import com.happymapleday.character.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

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
        
        // 중복 캐릭터 검증 (OCID 기준 - 전역)
        if (characterRepository.findByOcid(request.getOcid()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 캐릭터입니다.");
        }
        
        // 캐릭터 생성
        Character character = new Character(
                request.getUserId(),
                request.getCharacterName(),
                request.getOcid(),
                request.getIsMain()
        );
        
        Character savedCharacter = characterRepository.save(character);
        return CharacterResponse.from(savedCharacter);
    }
    
    /**
     * 캐릭터 삭제
     */
    @Transactional
    public void deleteCharacter(Long characterId) {
        // 캐릭터 존재 여부 확인
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));
        
        // 본캐 삭제 방지
        if (character.getIsMain()) {
            throw new IllegalArgumentException("본캐는 삭제할 수 없습니다.");
        }
        
        // 캐릭터 삭제
        characterRepository.delete(character);
    }
    
    /**
     * 본캐 설정
     */
    @Transactional
    public MainCharacterSettingResponse setMainCharacter(Long characterId) {
        // 캐릭터 존재 여부 확인
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));
        
        // 현재 본캐 조회
        Character previousMainCharacter = characterRepository.findByUserIdAndIsMainTrue(character.getUserId()).orElse(null);
        
        // 기존 본캐가 있다면 해제
        if (previousMainCharacter != null && !previousMainCharacter.getId().equals(characterId)) {
            previousMainCharacter.unsetAsMainCharacter();
            characterRepository.save(previousMainCharacter);
        }
        
        // 새로운 본캐 설정
        character.setAsMainCharacter();
        Character savedCharacter = characterRepository.save(character);
        
        return MainCharacterSettingResponse.from(savedCharacter, previousMainCharacter);
    }

    /**
     * 2.7 본캐 조회
     */
    @Transactional(readOnly = true)
    public MainCharacterResponse getMainCharacter(Long userId) {
        Character mainCharacter = characterRepository.findByUserIdAndIsMainTrue(userId)
                .orElseThrow(() -> new IllegalArgumentException("본캐가 설정되지 않았습니다."));
        return MainCharacterResponse.from(mainCharacter);
    }

    /**
     * 2.8 여러 캐릭터 저장 (회원가입용)
     */
    @Transactional
    public CharacterBulkCreateResponse createCharactersBulk(CharacterBulkCreateRequest request) {
        List<CharacterResponse> savedCharacters = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        // 필수 정보 검증
        if (request.getUserId() == null || request.getCharacters() == null || request.getCharacters().isEmpty()) {
            throw new IllegalArgumentException("필수 정보가 누락되었습니다.");
        }
        
        int totalCount = request.getCharacters().size();
        int successCount = 0;
        int failureCount = 0;
        
        // 각 캐릭터를 순차적으로 저장
        for (CharacterCreateRequest characterRequest : request.getCharacters()) {
            try {
                // userId 설정
                characterRequest.setUserId(request.getUserId());
                
                // 단일 캐릭터 저장
                CharacterResponse savedCharacter = createCharacter(characterRequest);
                savedCharacters.add(savedCharacter);
                successCount++;
                
            } catch (Exception e) {
                failureCount++;
                errors.add(String.format("캐릭터 '%s' 저장 실패: %s", 
                    characterRequest.getCharacterName(), e.getMessage()));
            }
        }
        
        return CharacterBulkCreateResponse.builder()
                .savedCharacters(savedCharacters)
                .totalCount(totalCount)
                .successCount(successCount)
                .failureCount(failureCount)
                .errors(errors)
                .build();
    }
} 