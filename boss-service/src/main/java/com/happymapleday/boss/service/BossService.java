package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.BossDto;
import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.ForceType;
import com.happymapleday.boss.repository.BossRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BossService {

    private final BossRepository bossRepository;

    // 모든 활성화된 보스 조회
    public List<BossDto.Response> getAllActiveBosses() {
        return bossRepository.findByIsActiveTrueOrderByCrystalPriceDesc()
                .stream()
                .map(BossDto.Response::from)
                .toList();
    }

    // 페이징된 보스 목록 조회
    public Page<BossDto.Response> getBossesWithPaging(Pageable pageable) {
        return bossRepository.findByIsActiveTrueOrderByCrystalPriceDesc(pageable)
                .map(BossDto.Response::from);
    }

    // ID로 보스 상세 조회 (물욕템 포함)
    public BossDto.Response getBossById(Long id) {
        Boss boss = bossRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 보스를 찾을 수 없습니다: " + id));
        return BossDto.Response.fromWithDesireItems(boss);
    }

    // 보스명으로 검색
    public List<BossDto.Response> searchBossesByName(String bossName) {
        return bossRepository.findByBossNameContainingIgnoreCaseAndIsActiveTrue(bossName)
                .stream()
                .map(BossDto.Response::from)
                .toList();
    }

    // 난이도별 보스 조회
    public List<BossDto.Response> getBossesByDifficulty(String difficulty) {
        return bossRepository.findByDifficultyAndIsActiveTrueOrderByCrystalPriceDesc(difficulty)
                .stream()
                .map(BossDto.Response::from)
                .toList();
    }

    // 주간/월간 보스 구분 조회
    public List<BossDto.Response> getBossesByMonthly(Boolean isMonthly) {
        return bossRepository.findByIsMonthlyAndIsActiveTrueOrderByCrystalPriceDesc(isMonthly)
                .stream()
                .map(BossDto.Response::from)
                .toList();
    }

    // 포스 타입별 보스 조회
    public List<BossDto.Response> getBossesByForceType(ForceType forceType) {
        return bossRepository.findByRequiredForceTypeAndIsActiveTrueOrderByCrystalPriceDesc(forceType)
                .stream()
                .map(BossDto.Response::from)
                .toList();
    }

    // 결정석 가격 범위로 보스 조회
    public List<BossDto.Response> getBossesByPriceRange(Long minPrice, Long maxPrice) {
        return bossRepository.findByPriceRange(minPrice, maxPrice)
                .stream()
                .map(BossDto.Response::from)
                .toList();
    }

    // 캐릭터 레벨에 맞는 보스 조회
    public List<BossDto.Response> getBossesForCharacterLevel(Integer characterLevel) {
        return bossRepository.findBossesForCharacterLevel(characterLevel)
                .stream()
                .map(BossDto.Response::from)
                .toList();
    }

    // 포스 조건에 맞는 보스 조회
    public List<BossDto.Response> getBossesForForceCondition(Integer arcaneForce, Integer authenticForce) {
        return bossRepository.findBossesForForceCondition(arcaneForce, authenticForce)
                .stream()
                .map(BossDto.Response::from)
                .toList();
    }

    // 복합 검색 조건으로 보스 조회
    public List<BossDto.Response> searchBosses(BossDto.SearchRequest searchRequest) {
        List<Boss> bosses = bossRepository.findByIsActiveTrueOrderByCrystalPriceDesc();

        return bosses.stream()
                .filter(boss -> matchesSearchCriteria(boss, searchRequest))
                .map(BossDto.Response::from)
                .toList();
    }

    // 검색 조건 매칭 로직
    private boolean matchesSearchCriteria(Boss boss, BossDto.SearchRequest searchRequest) {
        // 보스명 필터
        if (searchRequest.getBossName() != null && !searchRequest.getBossName().isBlank()) {
            if (!boss.getBossName().toLowerCase().contains(searchRequest.getBossName().toLowerCase())) {
                return false;
            }
        }

        // 난이도 필터
        if (searchRequest.getDifficulty() != null && !searchRequest.getDifficulty().isBlank()) {
            if (!boss.getDifficulty().equalsIgnoreCase(searchRequest.getDifficulty())) {
                return false;
            }
        }

        // 월간 보스 필터
        if (searchRequest.getIsMonthly() != null) {
            if (!boss.getIsMonthly().equals(searchRequest.getIsMonthly())) {
                return false;
            }
        }

        // 포스 타입 필터
        if (searchRequest.getRequiredForceType() != null) {
            if (!boss.getRequiredForceType().equals(searchRequest.getRequiredForceType())) {
                return false;
            }
        }

        // 가격 범위 필터
        if (searchRequest.getMinPrice() != null && boss.getCrystalPrice() < searchRequest.getMinPrice()) {
            return false;
        }
        if (searchRequest.getMaxPrice() != null && boss.getCrystalPrice() > searchRequest.getMaxPrice()) {
            return false;
        }

        // 캐릭터 레벨 필터
        if (searchRequest.getCharacterLevel() != null) {
            if (!boss.canEnterWithLevel(searchRequest.getCharacterLevel())) {
                return false;
            }
        }

        // 포스 조건 필터
        if (searchRequest.getArcaneForce() != null || searchRequest.getAuthenticForce() != null) {
            if (!boss.canChallenge(
                    searchRequest.getCharacterLevel() != null ? searchRequest.getCharacterLevel() : 300,
                    searchRequest.getArcaneForce(),
                    searchRequest.getAuthenticForce())) {
                return false;
            }
        }

        return true;
    }

    // 보스 생성
    @Transactional
    public BossDto.Response createBoss(BossDto.CreateRequest createRequest) {
        // 중복 체크
        if (bossRepository.existsByBossNameAndDifficultyAndIsActiveTrue(
                createRequest.getBossName(), createRequest.getDifficulty())) {
            throw new IllegalArgumentException("이미 존재하는 보스입니다: " + 
                    createRequest.getBossName() + " (" + createRequest.getDifficulty() + ")");
        }

        Boss boss = createRequest.toEntity();
        Boss savedBoss = bossRepository.save(boss);
        
        log.info("새로운 보스가 생성되었습니다: {}", savedBoss.getFullName());
        return BossDto.Response.from(savedBoss);
    }

    // 보스 수정
    @Transactional
    public BossDto.Response updateBoss(Long id, BossDto.UpdateRequest updateRequest) {
        Boss boss = bossRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 보스를 찾을 수 없습니다: " + id));

        // 보스명과 난이도가 변경되는 경우 중복 체크
        if ((updateRequest.getBossName() != null && !updateRequest.getBossName().equals(boss.getBossName())) ||
            (updateRequest.getDifficulty() != null && !updateRequest.getDifficulty().equals(boss.getDifficulty()))) {
            
            String newBossName = updateRequest.getBossName() != null ? updateRequest.getBossName() : boss.getBossName();
            String newDifficulty = updateRequest.getDifficulty() != null ? updateRequest.getDifficulty() : boss.getDifficulty();
            
            if (bossRepository.existsByBossNameAndDifficultyAndIsActiveTrue(newBossName, newDifficulty)) {
                throw new IllegalArgumentException("이미 존재하는 보스입니다: " + newBossName + " (" + newDifficulty + ")");
            }
        }

        // 필드 업데이트
        updateBossFields(boss, updateRequest);
        
        Boss updatedBoss = bossRepository.save(boss);
        log.info("보스가 수정되었습니다: {}", updatedBoss.getFullName());
        
        return BossDto.Response.from(updatedBoss);
    }

    private void updateBossFields(Boss boss, BossDto.UpdateRequest updateRequest) {
        if (updateRequest.getCrystalPrice() != null) {
            boss.updateCrystalPrice(updateRequest.getCrystalPrice());
        }
        if (updateRequest.getIsActive() != null) {
            if (updateRequest.getIsActive()) {
                boss.activate();
            } else {
                boss.deactivate();
            }
        }
        // 다른 필드들도 필요에 따라 업데이트 로직 추가
    }

    // 보스 삭제 (비활성화)
    @Transactional
    public void deleteBoss(Long id) {
        Boss boss = bossRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 보스를 찾을 수 없습니다: " + id));

        boss.deactivate();
        bossRepository.save(boss);
        
        log.info("보스가 비활성화되었습니다: {}", boss.getFullName());
    }

    // 보스 활성화
    @Transactional
    public BossDto.Response activateBoss(Long id) {
        Boss boss = bossRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 보스를 찾을 수 없습니다: " + id));

        boss.activate();
        Boss activatedBoss = bossRepository.save(boss);
        
        log.info("보스가 활성화되었습니다: {}", activatedBoss.getFullName());
        return BossDto.Response.from(activatedBoss);
    }
} 