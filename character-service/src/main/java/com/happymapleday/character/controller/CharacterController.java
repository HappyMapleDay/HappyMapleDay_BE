package com.happymapleday.character.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.character.dto.response.CharacterResponse;
import com.happymapleday.character.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {
    
    private final CharacterService characterService;
    
    /**
     * 2.1 서버별 캐릭터 목록 조회
     */
    @GetMapping("/server/{serverName}")
    public ResponseEntity<ApiResponse<List<CharacterResponse>>> getCharactersByServer(
            @PathVariable String serverName,
            @RequestParam Long userId) {
        try {
            List<CharacterResponse> characters = characterService.getCharactersByServer(userId, serverName);
            return ResponseEntity.ok(ApiResponse.success(characters));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("서버별 캐릭터 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 2.2 전체 캐릭터 조회 (유저별)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<CharacterResponse>>> getAllCharactersByUserId(
            @PathVariable Long userId) {
        try {
            List<CharacterResponse> characters = characterService.getAllCharactersByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(characters));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("전체 캐릭터 조회 중 오류가 발생했습니다."));
        }
    }
} 