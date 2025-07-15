package com.happymapleday.character.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happymapleday.character.dto.response.CharacterResponse;
import com.happymapleday.character.entity.Character;
import com.happymapleday.character.service.CharacterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CharacterControllerTest {

    @Mock
    private CharacterService characterService;

    @InjectMocks
    private CharacterController characterController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Character testCharacter1;
    private Character testCharacter2;
    private List<CharacterResponse> characterResponseList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(characterController).build();
        objectMapper = new ObjectMapper();

        // 테스트 데이터 설정
        testCharacter1 = new Character(1L, "테스트캐릭터1", "test-ocid-1", false);
        testCharacter1.setId(1L);
        testCharacter1.setAsMainCharacter();

        testCharacter2 = new Character(1L, "테스트캐릭터2", "test-ocid-2", false);
        testCharacter2.setId(2L);

        CharacterResponse response1 = CharacterResponse.from(testCharacter1);
        CharacterResponse response2 = CharacterResponse.from(testCharacter2);
        characterResponseList = Arrays.asList(response1, response2);
    }

    // ==================== 2.2 전체 캐릭터 조회 테스트 ====================

    @Test
    @DisplayName("2.2 전체 캐릭터 조회 성공")
    void getAllCharactersByUserId_Success() throws Exception {
        // given
        Long userId = 1L;
        given(characterService.getAllCharactersByUserId(userId))
                .willReturn(characterResponseList);

        // when & then
        mockMvc.perform(get("/api/characters/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("2.2 전체 캐릭터 조회 실패 - 등록된 캐릭터 없음")
    void getAllCharactersByUserId_EmptyList_Returns404() throws Exception {
        // given
        Long userId = 1L;
        given(characterService.getAllCharactersByUserId(userId))
                .willThrow(new IllegalArgumentException("등록된 캐릭터가 없습니다."));

        // when & then
        mockMvc.perform(get("/api/characters/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("2.2 전체 캐릭터 조회 - 단일 캐릭터")
    void getAllCharactersByUserId_SingleCharacter() throws Exception {
        // given
        Long userId = 1L;
        List<CharacterResponse> singleResponseList = Collections.singletonList(CharacterResponse.from(testCharacter1));
        given(characterService.getAllCharactersByUserId(userId))
                .willReturn(singleResponseList);

        // when & then
        mockMvc.perform(get("/api/characters/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("2.2 전체 캐릭터 조회 - 서비스 예외 발생")
    void getAllCharactersByUserId_ServiceException_Returns500() throws Exception {
        // given
        Long userId = 1L;
        given(characterService.getAllCharactersByUserId(userId))
                .willThrow(new RuntimeException("데이터베이스 연결 실패"));

        // when & then
        mockMvc.perform(get("/api/characters/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
} 