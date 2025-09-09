package com.happymapleday.character.service;

import com.happymapleday.character.dto.request.CharacterCreateRequest;
import com.happymapleday.character.dto.request.CharacterBulkCreateRequest;
import com.happymapleday.character.dto.response.CharacterResponse;
import com.happymapleday.character.dto.response.CharacterBulkCreateResponse;
import com.happymapleday.character.dto.response.MainCharacterSettingResponse;
import com.happymapleday.character.dto.response.MainCharacterResponse;
import com.happymapleday.character.entity.Character;
import com.happymapleday.character.repository.CharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        // 테스트 데이터 설정
        testCharacter1 = new Character(1L, "테스트캐릭터1", "test-ocid-1", false);
        testCharacter1.setId(1L);
        testCharacter1.setAsMainCharacter();

        testCharacter2 = new Character(1L, "테스트캐릭터2", "test-ocid-2", false);
        testCharacter2.setId(2L);

        characterList = Arrays.asList(testCharacter1, testCharacter2);
    }

    // ==================== 2.2 전체 캐릭터 조회 테스트 ====================

    @Test
    @DisplayName("2.2 전체 캐릭터 조회 성공")
    void getAllCharactersByUserId_Success() {
        // given
        Long userId = 1L;
        given(characterRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId))
                .willReturn(characterList);

        // when
        List<CharacterResponse> result = characterService.getAllCharactersByUserId(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCharacterName()).isEqualTo("테스트캐릭터1");
        assertThat(result.get(0).getIsMain()).isTrue();
        assertThat(result.get(1).getCharacterName()).isEqualTo("테스트캐릭터2");
        assertThat(result.get(1).getIsMain()).isFalse();
        verify(characterRepository).findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("2.2 전체 캐릭터 조회 실패 - 등록된 캐릭터 없음")
    void getAllCharactersByUserId_EmptyList_ThrowsException() {
        // given
        Long userId = 1L;
        given(characterRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId))
                .willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> characterService.getAllCharactersByUserId(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("등록된 캐릭터가 없습니다.");
        verify(characterRepository).findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("2.2 전체 캐릭터 조회 - 단일 캐릭터")
    void getAllCharactersByUserId_SingleCharacter() {
        // given
        Long userId = 1L;
        List<Character> singleCharacterList = Collections.singletonList(testCharacter1);
        given(characterRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId))
                .willReturn(singleCharacterList);

        // when
        List<CharacterResponse> result = characterService.getAllCharactersByUserId(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCharacterName()).isEqualTo("테스트캐릭터1");
        assertThat(result.get(0).getIsMain()).isTrue();
        verify(characterRepository).findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("2.2 전체 캐릭터 조회 - 여러 캐릭터")
    void getAllCharactersByUserId_MultipleCharacters() {
        // given
        Long userId = 1L;
        Character character3 = new Character(1L, "테스트캐릭터3", "test-ocid-3", false);
        character3.setId(3L);
        List<Character> multipleCharacterList = Arrays.asList(testCharacter1, testCharacter2, character3);
        given(characterRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId))
                .willReturn(multipleCharacterList);

        // when
        List<CharacterResponse> result = characterService.getAllCharactersByUserId(userId);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getCharacterName()).isEqualTo("테스트캐릭터1");
        assertThat(result.get(1).getCharacterName()).isEqualTo("테스트캐릭터2");
        assertThat(result.get(2).getCharacterName()).isEqualTo("테스트캐릭터3");
        verify(characterRepository).findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
    }

    // ==================== 2.3 캐릭터 추가 테스트 ====================

    @Test
    @DisplayName("2.3 캐릭터 추가 성공")
    void createCharacter_Success() {
        // given
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setUserId(1L);
        request.setCharacterName("새캐릭터");
        request.setOcid("new-ocid-123");
        request.setIsMain(false);

        Character character = new Character(1L, "새캐릭터", "new-ocid-123", false);
        character.setId(3L);

        given(characterRepository.findByOcidAndIsDeletedFalse(request.getOcid()))
                .willReturn(java.util.Optional.empty());
        given(characterRepository.save(org.mockito.ArgumentMatchers.any(Character.class))).willReturn(character);

        // when
        CharacterResponse result = characterService.createCharacter(request);

        // then
        assertThat(result.getCharacterName()).isEqualTo("새캐릭터");
        assertThat(result.getOcid()).isEqualTo("new-ocid-123");
        assertThat(result.getIsMain()).isFalse();
        verify(characterRepository).findByOcidAndIsDeletedFalse(request.getOcid());
        verify(characterRepository).save(org.mockito.ArgumentMatchers.any(Character.class));
    }

    @Test
    @DisplayName("2.3 캐릭터 추가 성공 - 본캐로 설정")
    void createCharacter_Success_AsMainCharacter() {
        // given
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setUserId(1L);
        request.setCharacterName("본캐릭터");
        request.setOcid("main-ocid-456");
        request.setIsMain(true);

        Character character = new Character(1L, "본캐릭터", "main-ocid-456", true);
        character.setId(4L);

        given(characterRepository.findByOcidAndIsDeletedFalse(request.getOcid()))
                .willReturn(java.util.Optional.empty());
        given(characterRepository.save(org.mockito.ArgumentMatchers.any(Character.class))).willReturn(character);

        // when
        CharacterResponse result = characterService.createCharacter(request);

        // then
        assertThat(result.getCharacterName()).isEqualTo("본캐릭터");
        assertThat(result.getOcid()).isEqualTo("main-ocid-456");
        assertThat(result.getIsMain()).isTrue();
        verify(characterRepository).findByOcidAndIsDeletedFalse(request.getOcid());
        verify(characterRepository).save(org.mockito.ArgumentMatchers.any(Character.class));
    }

    @Test
    @DisplayName("2.3 캐릭터 추가 실패 - 필수 정보 누락")
    void createCharacter_MissingRequiredInfo_ThrowsException() {
        // given
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setUserId(1L);
        // characterName과 ocid가 null

        // when & then
        assertThatThrownBy(() -> characterService.createCharacter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("필수 정보가 누락되었습니다.");
    }

    @Test
    @DisplayName("2.3 캐릭터 추가 실패 - 중복 캐릭터")
    void createCharacter_DuplicateCharacter_ThrowsException() {
        // given
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setUserId(1L);
        request.setCharacterName("중복캐릭터");
        request.setOcid("duplicate-ocid");
        request.setIsMain(false);

        Character existingCharacter = new Character(1L, "중복캐릭터", "duplicate-ocid", false);
        existingCharacter.setId(5L);

        given(characterRepository.findByOcidAndIsDeletedFalse(request.getOcid()))
                .willReturn(java.util.Optional.of(existingCharacter));

        // when & then
        assertThatThrownBy(() -> characterService.createCharacter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 등록된 캐릭터입니다.");
        verify(characterRepository).findByOcidAndIsDeletedFalse(request.getOcid());
    }

    // ==================== 2.4 캐릭터 삭제 테스트 ====================

    @Test
    @DisplayName("2.4 캐릭터 삭제 성공 (Soft Delete)")
    void deleteCharacter_Success() {
        // given
        Long characterId = 2L;
        Character character = testCharacter2; // isMain = false인 캐릭터
        given(characterRepository.findByIdAndIsDeletedFalse(characterId)).willReturn(java.util.Optional.of(character));

        // when
        characterService.deleteCharacter(characterId);

        // then
        verify(characterRepository).findByIdAndIsDeletedFalse(characterId);
        verify(characterRepository).save(character);
        assertThat(character.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("2.4 캐릭터 삭제 실패 - 본캐 삭제 시도")
    void deleteCharacter_MainCharacter_ThrowsException() {
        // given
        Long characterId = 1L;
        Character character = testCharacter1; // isMain = true인 캐릭터
        given(characterRepository.findByIdAndIsDeletedFalse(characterId)).willReturn(java.util.Optional.of(character));

        // when & then
        assertThatThrownBy(() -> characterService.deleteCharacter(characterId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("본캐는 삭제할 수 없습니다.");
        verify(characterRepository).findByIdAndIsDeletedFalse(characterId);
    }

    @Test
    @DisplayName("2.4 캐릭터 삭제 실패 - 캐릭터를 찾을 수 없음")
    void deleteCharacter_CharacterNotFound_ThrowsException() {
        // given
        Long characterId = 999L;
        given(characterRepository.findByIdAndIsDeletedFalse(characterId)).willReturn(java.util.Optional.empty());

        // when & then
        assertThatThrownBy(() -> characterService.deleteCharacter(characterId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("캐릭터를 찾을 수 없습니다.");
        verify(characterRepository).findByIdAndIsDeletedFalse(characterId);
    }

    // ==================== 2.6 본캐 설정 테스트 ====================

    @Test
    @DisplayName("2.6 본캐 설정 성공 - 첫 번째 본캐 설정")
    void setMainCharacter_Success_FirstMainCharacter() {
        // given
        Long characterId = 2L;
        Character character = testCharacter2; // isMain = false인 캐릭터
        given(characterRepository.findByIdAndIsDeletedFalse(characterId)).willReturn(java.util.Optional.of(character));
        given(characterRepository.findByUserIdAndIsMainTrueAndIsDeletedFalse(character.getUserId())).willReturn(java.util.Optional.empty());
        given(characterRepository.save(character)).willReturn(character);

        // when
        MainCharacterSettingResponse result = characterService.setMainCharacter(characterId);

        // then
        assertThat(result.getCharacterId()).isEqualTo(2L);
        assertThat(result.getCharacterName()).isEqualTo("테스트캐릭터2");
        assertThat(result.getIsMain()).isTrue();
        assertThat(result.getPreviousMainCharacter()).isNull();
        verify(characterRepository).findByIdAndIsDeletedFalse(characterId);
        verify(characterRepository).findByUserIdAndIsMainTrueAndIsDeletedFalse(character.getUserId());
        verify(characterRepository).save(character);
    }

    @Test
    @DisplayName("2.6 본캐 설정 성공 - 본캐 변경")
    void setMainCharacter_Success_ChangeMainCharacter() {
        // given
        Long characterId = 2L;
        Character newMainCharacter = testCharacter2; // isMain = false인 캐릭터
        Character previousMainCharacter = testCharacter1; // 기존 본캐
        
        given(characterRepository.findByIdAndIsDeletedFalse(characterId)).willReturn(java.util.Optional.of(newMainCharacter));
        given(characterRepository.findByUserIdAndIsMainTrueAndIsDeletedFalse(newMainCharacter.getUserId())).willReturn(java.util.Optional.of(previousMainCharacter));
        given(characterRepository.save(previousMainCharacter)).willReturn(previousMainCharacter);
        given(characterRepository.save(newMainCharacter)).willReturn(newMainCharacter);

        // when
        MainCharacterSettingResponse result = characterService.setMainCharacter(characterId);

        // then
        assertThat(result.getCharacterId()).isEqualTo(2L);
        assertThat(result.getCharacterName()).isEqualTo("테스트캐릭터2");
        assertThat(result.getIsMain()).isTrue();
        assertThat(result.getPreviousMainCharacter()).isNotNull();
        assertThat(result.getPreviousMainCharacter().getCharacterId()).isEqualTo(1L);
        assertThat(result.getPreviousMainCharacter().getCharacterName()).isEqualTo("테스트캐릭터1");
        verify(characterRepository).findByIdAndIsDeletedFalse(characterId);
        verify(characterRepository).findByUserIdAndIsMainTrueAndIsDeletedFalse(newMainCharacter.getUserId());
        verify(characterRepository).save(previousMainCharacter);
        verify(characterRepository).save(newMainCharacter);
    }

    @Test
    @DisplayName("2.6 본캐 설정 실패 - 캐릭터를 찾을 수 없음")
    void setMainCharacter_CharacterNotFound_ThrowsException() {
        // given
        Long characterId = 999L;
        given(characterRepository.findByIdAndIsDeletedFalse(characterId)).willReturn(java.util.Optional.empty());

        // when & then
        assertThatThrownBy(() -> characterService.setMainCharacter(characterId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("캐릭터를 찾을 수 없습니다.");
        verify(characterRepository).findByIdAndIsDeletedFalse(characterId);
    }

    // ==================== 2.7 본캐 조회 테스트 ====================

    @Test
    @DisplayName("2.7 본캐 조회 성공")
    void getMainCharacter_Success() {
        // given
        Long userId = 1L;
        given(characterRepository.findByUserIdAndIsMainTrueAndIsDeletedFalse(userId)).willReturn(java.util.Optional.of(testCharacter1));

        // when
        var result = characterService.getMainCharacter(userId);

        // then
        assertThat(result.getId()).isEqualTo(testCharacter1.getId());
        assertThat(result.getCharacterName()).isEqualTo(testCharacter1.getCharacterName());
        assertThat(result.getIsMain()).isTrue();
        verify(characterRepository).findByUserIdAndIsMainTrueAndIsDeletedFalse(userId);
    }

    @Test
    @DisplayName("2.7 본캐 조회 실패 - 본캐가 설정되지 않음")
    void getMainCharacter_NotSet_ThrowsException() {
        // given
        Long userId = 1L;
        given(characterRepository.findByUserIdAndIsMainTrueAndIsDeletedFalse(userId)).willReturn(java.util.Optional.empty());

        // when & then
        assertThatThrownBy(() -> characterService.getMainCharacter(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("본캐가 설정되지 않았습니다.");
        verify(characterRepository).findByUserIdAndIsMainTrueAndIsDeletedFalse(userId);
    }

    // ==================== 2.8 여러 캐릭터 저장 서비스 테스트 ====================

    @Test
    @DisplayName("2.8 여러 캐릭터 저장 성공")
    void createCharactersBulk_Success() {
        // given
        CharacterBulkCreateRequest request = new CharacterBulkCreateRequest();
        request.setUserId(1L);
        
        List<CharacterCreateRequest> characters = Arrays.asList(
                createCharacterRequest("캐릭터1", "ocid-1", false),
                createCharacterRequest("캐릭터2", "ocid-2", false),
                createCharacterRequest("캐릭터3", "ocid-3", true)
        );
        request.setCharacters(characters);

        Character character1 = new Character(1L, "캐릭터1", "ocid-1", false);
        character1.setId(1L);
        Character character2 = new Character(1L, "캐릭터2", "ocid-2", false);
        character2.setId(2L);
        Character character3 = new Character(1L, "캐릭터3", "ocid-3", true);
        character3.setId(3L);

        given(characterRepository.findByOcidAndIsDeletedFalse("ocid-1")).willReturn(java.util.Optional.empty());
        given(characterRepository.findByOcidAndIsDeletedFalse("ocid-2")).willReturn(java.util.Optional.empty());
        given(characterRepository.findByOcidAndIsDeletedFalse("ocid-3")).willReturn(java.util.Optional.empty());
        given(characterRepository.save(org.mockito.ArgumentMatchers.any(Character.class))).willReturn(character1, character2, character3);

        // when
        CharacterBulkCreateResponse result = characterService.createCharactersBulk(request);

        // then
        assertThat(result.getSavedCharacters()).hasSize(3);
        assertThat(result.getTotalCount()).isEqualTo(3);
        assertThat(result.getSuccessCount()).isEqualTo(3);
        assertThat(result.getFailureCount()).isEqualTo(0);
        assertThat(result.getErrors()).isEmpty();
    }

    @Test
    @DisplayName("2.8 여러 캐릭터 저장 - 일부 실패")
    void createCharactersBulk_PartialFailure() {
        // given
        CharacterBulkCreateRequest request = new CharacterBulkCreateRequest();
        request.setUserId(1L);
        
        List<CharacterCreateRequest> characters = Arrays.asList(
                createCharacterRequest("캐릭터1", "ocid-1", false),
                createCharacterRequest("캐릭터2", "ocid-2", false)
        );
        request.setCharacters(characters);

        Character character1 = new Character(1L, "캐릭터1", "ocid-1", false);
        character1.setId(1L);

        given(characterRepository.findByOcidAndIsDeletedFalse("ocid-1")).willReturn(java.util.Optional.empty());
        given(characterRepository.findByOcidAndIsDeletedFalse("ocid-2")).willReturn(java.util.Optional.of(character1)); // 중복된 OCID
        given(characterRepository.save(org.mockito.ArgumentMatchers.any(Character.class))).willReturn(character1);

        // when
        CharacterBulkCreateResponse result = characterService.createCharactersBulk(request);

        // then
        assertThat(result.getSavedCharacters()).hasSize(1);
        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getSuccessCount()).isEqualTo(1);
        assertThat(result.getFailureCount()).isEqualTo(1);
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0)).contains("캐릭터 '캐릭터2' 저장 실패");
    }

    private CharacterCreateRequest createCharacterRequest(String characterName, String ocid, Boolean isMain) {
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setCharacterName(characterName);
        request.setOcid(ocid);
        request.setIsMain(isMain);
        return request;
    }
} 