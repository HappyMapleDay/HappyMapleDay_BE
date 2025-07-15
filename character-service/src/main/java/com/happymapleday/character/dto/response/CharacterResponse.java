package com.happymapleday.character.dto.response;

import com.happymapleday.character.entity.Character;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CharacterResponse {
    private Long id;
    private String ocid;
    private String characterName;
    private Boolean isMain;
    private LocalDateTime createdAt;
    
    public static CharacterResponse from(Character character) {
        CharacterResponse response = new CharacterResponse();
        response.setId(character.getId());
        response.setOcid(character.getOcid());
        response.setCharacterName(character.getCharacterName());
        response.setIsMain(character.getIsMain());
        response.setCreatedAt(character.getCreatedAt());
        return response;
    }
} 