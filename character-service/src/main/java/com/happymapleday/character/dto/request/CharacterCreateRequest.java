package com.happymapleday.character.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CharacterCreateRequest {
    private Long userId;
    private String characterName;
    private String ocid;
} 