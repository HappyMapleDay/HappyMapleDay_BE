package com.happymapleday.settlement.service.validator;

import com.happymapleday.settlement.entity.WeeklyBossRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CrystalLimitValidatorTest {
    
    private CrystalLimitValidator validator;
    private LocalDate weekStartDate;
    
    @BeforeEach
    void setUp() {
        validator = new CrystalLimitValidator();
        weekStartDate = LocalDate.of(2024, 1, 4);
    }
    
    @Test
    @DisplayName("결정석 제한 검증 - 정상 케이스")
    void validateCrystalLimits_Normal() {
        // given
        List<WeeklyBossRecord> bossRecords = createBossRecords(1L, 5); // 5개 보스
        
        // when & then
        assertThatCode(() -> validator.validateCrystalLimits(bossRecords))
                .doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("결정석 제한 검증 - 캐릭터 제한 초과")
    void validateCrystalLimits_CharacterLimitExceeded() {
        // given
        List<WeeklyBossRecord> bossRecords = createBossRecords(1L, 12); // 12개 보스 (제한 초과)
        
        // when & then
        assertThatThrownBy(() -> validator.validateCrystalLimits(bossRecords))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("캐릭터당 주간 결정석 판매 제한을 초과했습니다");
    }
    
    @Test
    @DisplayName("결정석 제한 검증 - 월드 제한 초과")
    void validateCrystalLimits_WorldLimitExceeded() {
        // given
        List<WeeklyBossRecord> bossRecords = createMultipleCharacterBossRecords(90); // 90개 보스 (제한 초과)
        
        // when & then
        assertThatThrownBy(() -> validator.validateCrystalLimits(bossRecords))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("월드 전체 주간 결정석 판매 제한을 초과했습니다");
    }
    
    @Test
    @DisplayName("결정석 제한 검증 - 캐릭터 제한 경계값 (11개)")
    void validateCrystalLimits_CharacterLimitBoundary() {
        // given
        List<WeeklyBossRecord> bossRecords = createBossRecords(1L, 11); // 11개 보스 (제한 내)
        
        // when & then
        assertThatCode(() -> validator.validateCrystalLimits(bossRecords))
                .doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("결정석 제한 검증 - 월드 제한 경계값 (89개)")
    void validateCrystalLimits_WorldLimitBoundary() {
        // given
        List<WeeklyBossRecord> bossRecords = createMultipleCharacterBossRecords(89); // 89개 보스 (제한 내)
        
        // when & then
        assertThatCode(() -> validator.validateCrystalLimits(bossRecords))
                .doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("결정석 제한 검증 - 여러 캐릭터 혼합")
    void validateCrystalLimits_MultipleCharactersValid() {
        // given
        List<WeeklyBossRecord> bossRecords = new ArrayList<>();
        bossRecords.addAll(createBossRecords(1L, 10)); // 캐릭터 1: 10개
        bossRecords.addAll(createBossRecords(2L, 8));  // 캐릭터 2: 8개
        bossRecords.addAll(createBossRecords(3L, 11)); // 캐릭터 3: 11개
        
        // when & then
        assertThatCode(() -> validator.validateCrystalLimits(bossRecords))
                .doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("결정석 제한 검증 - 여러 캐릭터 중 한 캐릭터 초과")
    void validateCrystalLimits_OneCharacterExceeded() {
        // given
        List<WeeklyBossRecord> bossRecords = new ArrayList<>();
        bossRecords.addAll(createBossRecords(1L, 10)); // 캐릭터 1: 10개
        bossRecords.addAll(createBossRecords(2L, 12)); // 캐릭터 2: 12개 (제한 초과)
        bossRecords.addAll(createBossRecords(3L, 8));  // 캐릭터 3: 8개
        
        // when & then
        assertThatThrownBy(() -> validator.validateCrystalLimits(bossRecords))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("캐릭터당 주간 결정석 판매 제한을 초과했습니다");
    }
    
    @Test
    @DisplayName("결정석 제한 검증 - 빈 리스트")
    void validateCrystalLimits_EmptyList() {
        // given
        List<WeeklyBossRecord> bossRecords = new ArrayList<>();
        
        // when & then
        assertThatCode(() -> validator.validateCrystalLimits(bossRecords))
                .doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("결정석 제한 검증 - null 리스트")
    void validateCrystalLimits_NullList() {
        // given
        List<WeeklyBossRecord> bossRecords = null;
        
        // when & then
        assertThatCode(() -> validator.validateCrystalLimits(bossRecords))
                .doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("결정석 제한 검증 - 캐릭터별 최대 제한 (11개씩 8캐릭터)")
    void validateCrystalLimits_MaxCharactersWithinLimit() {
        // given
        List<WeeklyBossRecord> bossRecords = new ArrayList<>();
        for (long characterId = 1L; characterId <= 8L; characterId++) {
            bossRecords.addAll(createBossRecords(characterId, 11)); // 각 캐릭터 11개씩
        }
        // 총 88개 (89개 미만이므로 월드 제한 내)
        
        // when & then
        assertThatCode(() -> validator.validateCrystalLimits(bossRecords))
                .doesNotThrowAnyException();
    }
    
    private List<WeeklyBossRecord> createBossRecords(Long characterId, int count) {
        List<WeeklyBossRecord> records = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            records.add(WeeklyBossRecord.builder()
                    .settlementId(1L)
                    .userId(1L)
                    .characterId(characterId)
                    .bossId((long) (i + 1))
                    .weekStartDate(weekStartDate)
                    .crystalIncome(BigInteger.valueOf(850))
                    .partySize(2)
                    .desireItemIncome(BigInteger.ZERO)
                    .totalIncome(BigInteger.valueOf(850))
                    .build());
        }
        return records;
    }
    
    private List<WeeklyBossRecord> createMultipleCharacterBossRecords(int totalCount) {
        List<WeeklyBossRecord> records = new ArrayList<>();
        long characterId = 1L;
        int bossId = 1;
        
        for (int i = 0; i < totalCount; i++) {
            // 캐릭터당 최대 11개까지만 할당하고 다음 캐릭터로 넘어감
            if (i > 0 && i % 11 == 0) {
                characterId++;
                bossId = 1;
            }
            
            records.add(WeeklyBossRecord.builder()
                    .settlementId(1L)
                    .userId(1L)
                    .characterId(characterId)
                    .bossId((long) bossId)
                    .weekStartDate(weekStartDate)
                    .crystalIncome(BigInteger.valueOf(850))
                    .partySize(2)
                    .desireItemIncome(BigInteger.ZERO)
                    .totalIncome(BigInteger.valueOf(850))
                    .build());
            
            bossId++;
        }
        return records;
    }
} 