package com.happymapleday.common.client;

import com.happymapleday.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "character-service", url = "${services.character-service.url:http://localhost:8083}")
public interface CharacterServiceClient {
    
    @GetMapping("/api/character/{userId}")
    ApiResponse<List<Object>> getUserCharacters(@PathVariable Long userId);
    
    @GetMapping("/api/character/details/{characterId}")
    ApiResponse<Object> getCharacterDetails(@PathVariable Long characterId);
    
    @PostMapping("/api/character/sync")
    ApiResponse<Object> syncCharacter(@RequestBody Object syncRequest);
} 