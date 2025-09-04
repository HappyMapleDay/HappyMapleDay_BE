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
public class MainCharacterResponse {
    private Long id;
    private String ocid;
    private String characterName;
    private String serverName;
    private Boolean isMain;
    private LocalDateTime createdAt;

    public static MainCharacterResponse from(Character character) {
        return MainCharacterResponse.builder()
                .id(character.getId())
                .ocid(character.getOcid())
                .characterName(character.getCharacterName())
                .serverName(null) // DB에 serverName이 없으므로 null 처리
                .isMain(character.getIsMain())
                .createdAt(character.getCreatedAt())
                .build();
    }
} 