package com.happymapleday.character.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.character.dto.request.CharacterCreateRequest;
import com.happymapleday.character.dto.request.CharacterBulkCreateRequest;
import com.happymapleday.character.dto.response.CharacterResponse;
import com.happymapleday.character.dto.response.CharacterBulkCreateResponse;
import com.happymapleday.character.dto.response.MainCharacterSettingResponse;
import com.happymapleday.character.dto.response.MainCharacterResponse;
import com.happymapleday.character.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/character")
@RequiredArgsConstructor
public class CharacterController {
    
    private final CharacterService characterService;
    
    /**
     * 2.2 전체 캐릭터 조회 (유저별)
     */
    @GetMapping("/{userId}")
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
    
    /**
     * 2.3 캐릭터 추가
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CharacterResponse>> createCharacter(
            @RequestBody CharacterCreateRequest request) {
        try {
            CharacterResponse character = characterService.createCharacter(request);
            return ResponseEntity.status(201)
                    .body(ApiResponse.success(character));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("필수 정보가 누락")) {
                return ResponseEntity.status(400)
                        .body(ApiResponse.error(e.getMessage()));
            } else if (e.getMessage().contains("이미 등록된 캐릭터")) {
                return ResponseEntity.status(409)
                        .body(ApiResponse.error(e.getMessage()));
            } else {
                return ResponseEntity.status(400)
                        .body(ApiResponse.error(e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("캐릭터 추가 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 2.4 캐릭터 삭제
     */
    @DeleteMapping("/{characterId}")
    public ResponseEntity<ApiResponse<String>> deleteCharacter(
            @PathVariable Long characterId) {
        try {
            characterService.deleteCharacter(characterId);
            return ResponseEntity.ok(ApiResponse.success("캐릭터가 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("본캐는 삭제할 수 없습니다")) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error(e.getMessage()));
            } else if (e.getMessage().contains("캐릭터를 찾을 수 없습니다")) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error(e.getMessage()));
            } else {
                return ResponseEntity.status(400)
                        .body(ApiResponse.error(e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("캐릭터 삭제 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 2.6 본캐 설정
     */
    @PutMapping("/{characterId}/main")
    public ResponseEntity<ApiResponse<MainCharacterSettingResponse>> setMainCharacter(
            @PathVariable Long characterId) {
        try {
            MainCharacterSettingResponse response = characterService.setMainCharacter(characterId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("캐릭터를 찾을 수 없습니다")) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error(e.getMessage()));
            } else {
                return ResponseEntity.status(400)
                        .body(ApiResponse.error(e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("본캐 설정 중 오류가 발생했습니다."));
        }
    }

    /**
     * 2.7 본캐 조회
     */
    @GetMapping("/{userId}/main")
    public ResponseEntity<ApiResponse<MainCharacterResponse>> getMainCharacter(@PathVariable Long userId) {
        try {
            MainCharacterResponse response = characterService.getMainCharacter(userId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("본캐가 설정되지 않았습니다")) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error(e.getMessage()));
            } else {
                return ResponseEntity.status(400)
                        .body(ApiResponse.error(e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("본캐 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 2.8 여러 캐릭터 저장 (회원가입용)
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CharacterBulkCreateResponse>> createCharactersBulk(
            @RequestBody CharacterBulkCreateRequest request) {
        try {
            CharacterBulkCreateResponse response = characterService.createCharactersBulk(request);
            return ResponseEntity.status(201)
                    .body(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("필수 정보가 누락")) {
                return ResponseEntity.status(400)
                        .body(ApiResponse.error(e.getMessage()));
            } else {
                return ResponseEntity.status(400)
                        .body(ApiResponse.error(e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("캐릭터 저장 중 오류가 발생했습니다."));
        }
    }
} 