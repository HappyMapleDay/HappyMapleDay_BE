package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.BossDto;
import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.ForceType;
import com.happymapleday.boss.repository.BossRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("BossService 테스트")
class BossServiceTest {

    @Mock
    private BossRepository bossRepository;

    @InjectMocks
    private BossService bossService;

    private Boss testBoss;
    private BossDto.CreateRequest createRequest;
    private BossDto.UpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testBoss = Boss.builder()
                .bossName("자쿰")
                .difficulty("이지")
                .crystalPrice(12960000L)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(50)
                .requiredForceType(ForceType.NONE)
                .requiredForceAmount(0)
                .build();

        createRequest = BossDto.CreateRequest.builder()
                .bossName("루시드")
                .difficulty("이지")
                .crystalPrice(81000000L)
                .isMonthly(false)
                .minEntryLevel(200)
                .requiredForceType(ForceType.ARCANE)
                .requiredForceAmount(320)
                .build();

        updateRequest = BossDto.UpdateRequest.builder()
                .crystalPrice(15000000L)
                .maxPartySize(8)
                .build();
    }

    @Test
    @DisplayName("모든 활성화된 보스 조회")
    void getAllActiveBosses() {
        // given
        List<Boss> bosses = Arrays.asList(testBoss);
        given(bossRepository.findByIsActiveTrueOrderByCrystalPriceDesc()).willReturn(bosses);

        // when
        List<BossDto.Response> result = bossService.getAllActiveBosses();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBossName()).isEqualTo("자쿰");
        assertThat(result.get(0).getDifficulty()).isEqualTo("이지");
    }

    @Test
    @DisplayName("페이징된 보스 조회")
    void getBossesWithPaging() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Boss> bossPage = new PageImpl<>(Arrays.asList(testBoss), pageable, 1);
        given(bossRepository.findByIsActiveTrueOrderByCrystalPriceDesc(pageable)).willReturn(bossPage);

        // when
        Page<BossDto.Response> result = bossService.getBossesWithPaging(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getBossName()).isEqualTo("자쿰");
    }

    @Test
    @DisplayName("ID로 보스 조회 - 성공")
    void getBossById_Success() {
        // given
        given(bossRepository.findById(1L)).willReturn(Optional.of(testBoss));

        // when
        BossDto.Response result = bossService.getBossById(1L);

        // then
        assertThat(result.getBossName()).isEqualTo("자쿰");
        assertThat(result.getDifficulty()).isEqualTo("이지");
    }

    @Test
    @DisplayName("ID로 보스 조회 - 실패 (존재하지 않음)")
    void getBossById_NotFound() {
        // given
        given(bossRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bossService.getBossById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 ID의 보스를 찾을 수 없습니다: 999");
    }

    @Test
    @DisplayName("보스명으로 검색")
    void searchBossesByName() {
        // given
        List<Boss> bosses = Arrays.asList(testBoss);
        given(bossRepository.findByBossNameContainingIgnoreCaseAndIsActiveTrue("자쿰")).willReturn(bosses);

        // when
        List<BossDto.Response> result = bossService.searchBossesByName("자쿰");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBossName()).isEqualTo("자쿰");
    }

    @Test
    @DisplayName("난이도별 보스 조회")
    void getBossesByDifficulty() {
        // given
        List<Boss> bosses = Arrays.asList(testBoss);
        given(bossRepository.findByDifficultyAndIsActiveTrueOrderByCrystalPriceDesc("이지")).willReturn(bosses);

        // when
        List<BossDto.Response> result = bossService.getBossesByDifficulty("이지");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDifficulty()).isEqualTo("이지");
    }

    @Test
    @DisplayName("포스 타입별 보스 조회")
    void getBossesByForceType() {
        // given
        List<Boss> bosses = Arrays.asList(testBoss);
        given(bossRepository.findByRequiredForceTypeAndIsActiveTrueOrderByCrystalPriceDesc(ForceType.NONE)).willReturn(bosses);

        // when
        List<BossDto.Response> result = bossService.getBossesByForceType(ForceType.NONE);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequiredForceType()).isEqualTo(ForceType.NONE);
    }

    @Test
    @DisplayName("결정석 가격 범위로 보스 조회")
    void getBossesByPriceRange() {
        // given
        List<Boss> bosses = Arrays.asList(testBoss);
        given(bossRepository.findByPriceRange(10000000L, 20000000L)).willReturn(bosses);

        // when
        List<BossDto.Response> result = bossService.getBossesByPriceRange(10000000L, 20000000L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCrystalPrice()).isEqualTo(12960000L);
    }

    @Test
    @DisplayName("캐릭터 레벨에 맞는 보스 조회")
    void getBossesForCharacterLevel() {
        // given
        List<Boss> bosses = Arrays.asList(testBoss);
        given(bossRepository.findBossesForCharacterLevel(100)).willReturn(bosses);

        // when
        List<BossDto.Response> result = bossService.getBossesForCharacterLevel(100);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMinEntryLevel()).isEqualTo(50);
    }

    @Test
    @DisplayName("보스 생성 - 성공")
    void createBoss_Success() {
        // given
        given(bossRepository.existsByBossNameAndDifficultyAndIsActiveTrue("루시드", "이지")).willReturn(false);
        given(bossRepository.save(any(Boss.class))).willReturn(testBoss);

        // when
        BossDto.Response result = bossService.createBoss(createRequest);

        // then
        assertThat(result.getBossName()).isEqualTo("자쿰");
        verify(bossRepository).save(any(Boss.class));
    }

    @Test
    @DisplayName("보스 생성 - 실패 (중복)")
    void createBoss_Duplicate() {
        // given
        given(bossRepository.existsByBossNameAndDifficultyAndIsActiveTrue("루시드", "이지")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> bossService.createBoss(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 보스입니다: 루시드 (이지)");
    }

    @Test
    @DisplayName("보스 수정 - 성공")
    void updateBoss_Success() {
        // given
        given(bossRepository.findById(1L)).willReturn(Optional.of(testBoss));
        given(bossRepository.save(any(Boss.class))).willReturn(testBoss);

        // when
        BossDto.Response result = bossService.updateBoss(1L, updateRequest);

        // then
        assertThat(result).isNotNull();
        verify(bossRepository).save(any(Boss.class));
    }

    @Test
    @DisplayName("보스 삭제 (비활성화)")
    void deleteBoss() {
        // given
        given(bossRepository.findById(1L)).willReturn(Optional.of(testBoss));

        // when
        bossService.deleteBoss(1L);

        // then
        verify(bossRepository).save(testBoss);
        assertThat(testBoss.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("보스 활성화")
    void activateBoss() {
        // given
        Boss inactiveBoss = Boss.builder()
                .bossName("비활성보스")
                .difficulty("하드")
                .crystalPrice(50000000L)
                .isActive(false)
                .build();
        
        given(bossRepository.findById(1L)).willReturn(Optional.of(inactiveBoss));
        given(bossRepository.save(any(Boss.class))).willReturn(inactiveBoss);

        // when
        BossDto.Response result = bossService.activateBoss(1L);

        // then
        assertThat(result).isNotNull();
        verify(bossRepository).save(inactiveBoss);
        assertThat(inactiveBoss.getIsActive()).isTrue();
    }
} 