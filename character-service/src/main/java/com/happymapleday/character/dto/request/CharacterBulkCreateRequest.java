package com.happymapleday.character.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CharacterBulkCreateRequest {
    private Long userId;
    private List<CharacterCreateRequest> characters;
} 