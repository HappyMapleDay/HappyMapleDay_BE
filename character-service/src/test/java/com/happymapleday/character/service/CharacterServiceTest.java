package com.happymapleday.character.service;

import com.happymapleday.character.dto.response.CharacterResponse;
import com.happymapleday.character.entity.Character;
import com.happymapleday.character.repository.CharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @InjectMocks
    private CharacterService characterService;

    private Character testCharacter1;
    private Character testCharacter2;
    private List<Character> characterList;

    @BeforeEach
    void setUp() {
        testCharacter1 = new Character(1L, "테스트캐릭터1", "test-ocid-1", "스카니아");
        testCharacter1.setId(1L);
        testCharacter1.setAsMainCharacter();

        testCharacter2 = new Character(1L, "테스트캐릭터2", "test-ocid-2", "스카니아");
        testCharacter2.setId(2L);

        characterList = Arrays.asList(testCharacter1, testCharacter2);
    }

    // ==================== 2.1 서버별 캐릭터 목록 조회 테스트 ====================

    @Test
    @DisplayName("2.1 서버별 캐릭터 목록 조회 성공")
    void getCharactersByServer_Success() {
        // given
        Long userId = 1L;
        String serverName = "스카니아";
        given(characterRepository.findByUserIdAndServerNameOrderByCreatedAtDesc(userId, serverName))
                .willReturn(characterList);

        // when
        List<CharacterResponse> result = characterService.getCharactersByServer(userId, serverName);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCharacterName()).isEqualTo("테스트캐릭터1");
        assertThat(result.get(0).getServerName()).isEqualTo("스카니아");
        assertThat(result.get(0).getIsMain()).isTrue();
        assertThat(result.get(1).getCharacterName()).isEqualTo("테스트캐릭터2");
        assertThat(result.get(1).getServerName()).isEqualTo("스카니아");
        assertThat(result.get(1).getIsMain()).isFalse();

        verify(characterRepository).findByUserIdAndServerNameOrderByCreatedAtDesc(userId, serverName);
    }

    @Test
    @DisplayName("2.1 서버별 캐릭터 목록 조회 실패 - 해당 서버에 캐릭터 없음")
    void getCharactersByServer_EmptyList_ThrowsException() {
        // given
        Long userId = 1L;
        String serverName = "베라";
        given(characterRepository.findByUserIdAndServerNameOrderByCreatedAtDesc(userId, serverName))
                .willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> characterService.getCharactersByServer(userId, serverName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 서버에 등록된 캐릭터가 없습니다.");

        verify(characterRepository).findByUserIdAndServerNameOrderByCreatedAtDesc(userId, serverName);
    }

    @Test
    @DisplayName("2.1 서버별 캐릭터 목록 조회 - 단일 캐릭터")
    void getCharactersByServer_SingleCharacter() {
        // given
        Long userId = 1L;
        String serverName = "스카니아";
        List<Character> singleCharacterList = Collections.singletonList(testCharacter1);
        given(characterRepository.findByUserIdAndServerNameOrderByCreatedAtDesc(userId, serverName))
                .willReturn(singleCharacterList);

        // when
        List<CharacterResponse> result = characterService.getCharactersByServer(userId, serverName);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCharacterName()).isEqualTo("테스트캐릭터1");
        assertThat(result.get(0).getIsMain()).isTrue();

        verify(characterRepository).findByUserIdAndServerNameOrderByCreatedAtDesc(userId, serverName);
    }

    // ==================== 2.2 전체 캐릭터 조회 테스트 ====================

    @Test
    @DisplayName("2.2 전체 캐릭터 조회 성공")
    void getAllCharactersByUserId_Success() {
        // given
        Long userId = 1L;
        given(characterRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .willReturn(characterList);

        // when
        List<CharacterResponse> result = characterService.getAllCharactersByUserId(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCharacterName()).isEqualTo("테스트캐릭터1");
        assertThat(result.get(0).getServerName()).isEqualTo("스카니아");
        assertThat(result.get(0).getIsMain()).isTrue();
        assertThat(result.get(1).getCharacterName()).isEqualTo("테스트캐릭터2");
        assertThat(result.get(1).getServerName()).isEqualTo("스카니아");
        assertThat(result.get(1).getIsMain()).isFalse();

        verify(characterRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("2.2 전체 캐릭터 조회 실패 - 등록된 캐릭터 없음")
    void getAllCharactersByUserId_EmptyList_ThrowsException() {
        // given
        Long userId = 1L;
        given(characterRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> characterService.getAllCharactersByUserId(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("등록된 캐릭터가 없습니다.");

        verify(characterRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("2.2 전체 캐릭터 조회 - 여러 서버의 캐릭터")
    void getAllCharactersByUserId_MultipleServers() {
        // given
        Long userId = 1L;
        Character character3 = new Character(1L, "테스트캐릭터3", "test-ocid-3", "베라");
        character3.setId(3L);

        List<Character> multipleServerCharacters = Arrays.asList(testCharacter1, testCharacter2, character3);
        given(characterRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .willReturn(multipleServerCharacters);

        // when
        List<CharacterResponse> result = characterService.getAllCharactersByUserId(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getServerName()).isEqualTo("스카니아");
        assertThat(result.get(1).getServerName()).isEqualTo("스카니아");
        assertThat(result.get(2).getServerName()).isEqualTo("베라");

        verify(characterRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("2.2 전체 캐릭터 조회 - 단일 캐릭터")
    void getAllCharactersByUserId_SingleCharacter() {
        // given
        Long userId = 1L;
        List<Character> singleCharacterList = Collections.singletonList(testCharacter1);
        given(characterRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .willReturn(singleCharacterList);

        // when
        List<CharacterResponse> result = characterService.getAllCharactersByUserId(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCharacterName()).isEqualTo("테스트캐릭터1");
        assertThat(result.get(0).getIsMain()).isTrue();

        verify(characterRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }
} 