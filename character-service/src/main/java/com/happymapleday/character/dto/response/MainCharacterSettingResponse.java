package com.happymapleday.character.dto.response;

import com.happymapleday.character.entity.Character;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainCharacterSettingResponse {
    
    private Long characterId;
    private String characterName;
    private Boolean isMain;
    private PreviousMainCharacter previousMainCharacter;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreviousMainCharacter {
        private Long characterId;
        private String characterName;
    }
    
    public static MainCharacterSettingResponse from(Character newMainCharacter, Character previousMainCharacter) {
        return MainCharacterSettingResponse.builder()
                .characterId(newMainCharacter.getId())
                .characterName(newMainCharacter.getCharacterName())
                .isMain(true)
                .previousMainCharacter(previousMainCharacter != null ? 
                        PreviousMainCharacter.builder()
                                .characterId(previousMainCharacter.getId())
                                .characterName(previousMainCharacter.getCharacterName())
                                .build() : null)
                .build();
    }
} 