package com.happymapleday.character.dto.response;

import com.happymapleday.character.entity.Character;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterResponse {
    
    private Long id;
    private String ocid;
    private String characterName;
    private String serverName;
    private Boolean isMain;
    private LocalDateTime createdAt;
    
    public static CharacterResponse from(Character character) {
        return CharacterResponse.builder()
                .id(character.getId())
                .ocid(character.getOcid())
                .characterName(character.getCharacterName())
                .serverName(character.getServerName())
                .isMain(character.getIsMainCharacter())
                .createdAt(character.getCreatedAt())
                .build();
    }
} 