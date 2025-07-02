package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.response.BossResponse;
import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.BossDropItem;
import com.happymapleday.boss.entity.ForceType;
import com.happymapleday.boss.entity.Item;
import com.happymapleday.boss.repository.BossRepository;
import com.happymapleday.boss.service.impl.BossServiceImpl;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("BossService 테스트")
class BossServiceTest {

    @Mock
    private BossRepository bossRepository;

    @InjectMocks
    private BossServiceImpl bossService;

    private Boss testBoss1;
    private Boss testBoss2;
    private Boss testBoss3;

    @BeforeEach
    void setUp() {
        // 첫 번째 테스트 보스 - 자쿰 (카오스)
        testBoss1 = Boss.builder()
                .bossName("자쿰")
                .difficulty("카오스")
                .crystalPrice(8080000L)
                .maxPartySize(6)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(90)
                .bossLevel(90)
                .requiredForceType(ForceType.NONE)
                .requiredForceAmount(0)
                .build();

        // 두 번째 테스트 보스 - 루시드 (하드)
        testBoss2 = Boss.builder()
                .bossName("루시드")
                .difficulty("하드")
                .crystalPrice(94500000L)
                .maxPartySize(6)
                .isMonthly(false)
                .isActive(true)
                .minEntryLevel(220)
                .bossLevel(255)
                .requiredForceType(ForceType.ARCANE)
                .requiredForceAmount(360)
                .build();

        // 세 번째 테스트 보스 - 월간 보스
        testBoss3 = Boss.builder()
                .bossName("검은 마법사")
                .difficulty("하드")
                .crystalPrice(500000000L)
                .maxPartySize(6)
                .isMonthly(true)
                .isActive(true)
                .minEntryLevel(265)
                .bossLevel(280)
                .requiredForceType(ForceType.AUTHENTIC)
                .requiredForceAmount(500)
                .build();

        // 드랍 아이템 설정 (간단한 테스트용)
        Item item1 = Item.builder()
                .itemName("자쿰헬멧")
                .isRandomBox(false)
                .build();

        BossDropItem dropItem1 = BossDropItem.builder()
                .boss(testBoss1)
                .item(item1)
                .build();

        testBoss1.getBossDropItems().add(dropItem1);
    }

    @Test
    @DisplayName("모든 활성화된 보스 조회 - 성공")
    void getAllActiveBosses_Success() {
        // given
        List<Boss> bosses = Arrays.asList(testBoss3, testBoss2, testBoss1); // 결정석 가격 순으로 정렬
        given(bossRepository.findByIsActiveTrueWithBossDropItemsOrderByCrystalPriceDesc()).willReturn(bosses);

        // when
        List<BossResponse> result = bossService.getAllActiveBosses();

        // then
        assertThat(result).hasSize(3);
        
        // 첫 번째 보스 (가장 높은 결정석 가격)
        BossResponse firstBoss = result.get(0);
        assertThat(firstBoss.getBossName()).isEqualTo("검은 마법사");
        assertThat(firstBoss.getDifficulty()).isEqualTo("하드");
        assertThat(firstBoss.getCrystalPrice()).isEqualTo(500000000L);
        assertThat(firstBoss.getIsMonthly()).isTrue();
        assertThat(firstBoss.getRequiredForceType()).isEqualTo(ForceType.AUTHENTIC);
        assertThat(firstBoss.getRequiredForceAmount()).isEqualTo(500);
        
        // 두 번째 보스
        BossResponse secondBoss = result.get(1);
        assertThat(secondBoss.getBossName()).isEqualTo("루시드");
        assertThat(secondBoss.getDifficulty()).isEqualTo("하드");
        assertThat(secondBoss.getCrystalPrice()).isEqualTo(94500000L);
        assertThat(secondBoss.getIsMonthly()).isFalse();
        assertThat(secondBoss.getRequiredForceType()).isEqualTo(ForceType.ARCANE);
        
        // 세 번째 보스
        BossResponse thirdBoss = result.get(2);
        assertThat(thirdBoss.getBossName()).isEqualTo("자쿰");
        assertThat(thirdBoss.getDifficulty()).isEqualTo("카오스");
        assertThat(thirdBoss.getCrystalPrice()).isEqualTo(8080000L);
        assertThat(thirdBoss.getIsMonthly()).isFalse();
        assertThat(thirdBoss.getRequiredForceType()).isEqualTo(ForceType.NONE);
        
        // Repository 메서드 호출 검증
        verify(bossRepository).findByIsActiveTrueWithBossDropItemsOrderByCrystalPriceDesc();
    }

    @Test
    @DisplayName("활성화된 보스가 없을 때 빈 리스트 반환")
    void getAllActiveBosses_EmptyList() {
        // given
        given(bossRepository.findByIsActiveTrueWithBossDropItemsOrderByCrystalPriceDesc()).willReturn(Collections.emptyList());

        // when
        List<BossResponse> result = bossService.getAllActiveBosses();

        // then
        assertThat(result).isEmpty();
        verify(bossRepository).findByIsActiveTrueWithBossDropItemsOrderByCrystalPriceDesc();
    }

    @Test
    @DisplayName("드랍 아이템이 있는 보스의 경우 desireItems가 포함되어야 함")
    void getAllActiveBosses_WithDropItems() {
        // given
        List<Boss> bosses = Arrays.asList(testBoss1);
        given(bossRepository.findByIsActiveTrueWithBossDropItemsOrderByCrystalPriceDesc()).willReturn(bosses);

        // when
        List<BossResponse> result = bossService.getAllActiveBosses();

        // then
        assertThat(result).hasSize(1);
        BossResponse bossResponse = result.get(0);
        assertThat(bossResponse.getDesireItems()).isNotNull();
        assertThat(bossResponse.getDesireItems()).hasSize(1);
        
        // DesireItem 검증
        if (!bossResponse.getDesireItems().isEmpty()) {
            assertThat(bossResponse.getDesireItems().get(0).getItemName()).isEqualTo("자쿰헬멧");
            assertThat(bossResponse.getDesireItems().get(0).getIsRandomBox()).isFalse();
            assertThat(bossResponse.getDesireItems().get(0).getBossName()).isEqualTo("자쿰");
        }
    }

    @Test
    @DisplayName("보스 응답에 fullName이 올바르게 설정되어야 함")
    void getAllActiveBosses_WithFullName() {
        // given
        List<Boss> bosses = Arrays.asList(testBoss1);
        given(bossRepository.findByIsActiveTrueWithBossDropItemsOrderByCrystalPriceDesc()).willReturn(bosses);

        // when
        List<BossResponse> result = bossService.getAllActiveBosses();

        // then
        assertThat(result).hasSize(1);
        BossResponse bossResponse = result.get(0);
        assertThat(bossResponse.getFullName()).isEqualTo("자쿰 (카오스)");
    }

    @Test
    @DisplayName("포스 타입별 보스 정보가 올바르게 매핑되어야 함")
    void getAllActiveBosses_ForceTypeMapping() {
        // given
        List<Boss> bosses = Arrays.asList(testBoss1, testBoss2, testBoss3);
        given(bossRepository.findByIsActiveTrueWithBossDropItemsOrderByCrystalPriceDesc()).willReturn(bosses);

        // when
        List<BossResponse> result = bossService.getAllActiveBosses();

        // then
        assertThat(result).hasSize(3);
        
        // 각 보스의 포스 타입 검증
        BossResponse noneForce = result.stream()
                .filter(boss -> boss.getRequiredForceType() == ForceType.NONE)
                .findFirst()
                .orElse(null);
        assertThat(noneForce).isNotNull();
        assertThat(noneForce.getRequiredForceAmount()).isEqualTo(0);
        
        BossResponse arcaneForce = result.stream()
                .filter(boss -> boss.getRequiredForceType() == ForceType.ARCANE)
                .findFirst()
                .orElse(null);
        assertThat(arcaneForce).isNotNull();
        assertThat(arcaneForce.getRequiredForceAmount()).isEqualTo(360);
        
        BossResponse authenticForce = result.stream()
                .filter(boss -> boss.getRequiredForceType() == ForceType.AUTHENTIC)
                .findFirst()
                .orElse(null);
        assertThat(authenticForce).isNotNull();
        assertThat(authenticForce.getRequiredForceAmount()).isEqualTo(500);
    }
} 