package com.happymapleday.character.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterBulkCreateResponse {
    private List<CharacterResponse> savedCharacters;
    private int totalCount;
    private int successCount;
    private int failureCount;
    private List<String> errors;
} 