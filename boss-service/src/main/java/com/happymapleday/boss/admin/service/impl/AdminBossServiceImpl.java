package com.happymapleday.boss.admin.service.impl;

import com.happymapleday.boss.admin.dto.request.AdminBossCreateRequest;
import com.happymapleday.boss.admin.dto.request.AdminBossUpdateRequest;
import com.happymapleday.boss.admin.dto.response.AdminBossResponse;
import com.happymapleday.boss.admin.service.AdminBossService;
import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.repository.BossRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminBossServiceImpl implements AdminBossService {
    
    private final BossRepository bossRepository;
    
    // 모든 보스 조회 (관리자용 - 비활성화된 것도 포함)
    @Override
    public List<AdminBossResponse> getAllBosses() {
        return bossRepository.findAll().stream()
                .map(AdminBossResponse::new)
                .collect(Collectors.toList());
    }
    
    // 페이징된 모든 보스 조회
    @Override
    public Page<AdminBossResponse> getAllBosses(Pageable pageable) {
        return bossRepository.findAll(pageable)
                .map(AdminBossResponse::new);
    }
    
    // 특정 보스 조회
    @Override
    public AdminBossResponse getBoss(Long id) {
        Boss boss = bossRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스입니다. ID: " + id));
        return new AdminBossResponse(boss);
    }
    
    // 보스 생성
    @Override
    @Transactional
    public AdminBossResponse createBoss(AdminBossCreateRequest request) {
        // 중복 체크
        if (bossRepository.existsByBossNameAndDifficultyAndIsActiveTrue(request.getBossName(), request.getDifficulty())) {
            throw new IllegalArgumentException("이미 존재하는 보스입니다: " + request.getBossName() + " (" + request.getDifficulty() + ")");
        }
        
        Boss boss = Boss.builder()
                .bossName(request.getBossName())
                .difficulty(request.getDifficulty())
                .crystalPrice(request.getCrystalPrice())
                .maxPartySize(request.getMaxPartySize())
                .isMonthly(request.getIsMonthly())
                .isActive(request.getIsActive())
                .minEntryLevel(request.getMinEntryLevel())
                .bossLevel(request.getBossLevel())
                .requiredForceType(request.getRequiredForceType())
                .requiredForceAmount(request.getRequiredForceAmount())
                .build();
        
        Boss savedBoss = bossRepository.save(boss);
        return new AdminBossResponse(savedBoss);
    }
    
    // 보스 수정
    @Override
    @Transactional
    public AdminBossResponse updateBoss(Long id, AdminBossUpdateRequest request) {
        Boss boss = bossRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스입니다. ID: " + id));
        
        // 다른 보스와 중복 체크 (자기 자신은 제외)
        bossRepository.findByBossNameAndDifficultyAndIsActiveTrue(request.getBossName(), request.getDifficulty())
                .ifPresent(existingBoss -> {
                    if (!existingBoss.getId().equals(id)) {
                        throw new IllegalArgumentException("이미 존재하는 보스입니다: " + request.getBossName() + " (" + request.getDifficulty() + ")");
                    }
                });
        
        // 기존 보스 정보 업데이트 (새로운 빌더로 교체)
        Boss updatedBoss = Boss.builder()
                .bossName(request.getBossName())
                .difficulty(request.getDifficulty())
                .crystalPrice(request.getCrystalPrice())
                .maxPartySize(request.getMaxPartySize())
                .isMonthly(request.getIsMonthly())
                .isActive(request.getIsActive())
                .minEntryLevel(request.getMinEntryLevel())
                .bossLevel(request.getBossLevel())
                .requiredForceType(request.getRequiredForceType())
                .requiredForceAmount(request.getRequiredForceAmount())
                .build();
        
        bossRepository.delete(boss);
        Boss savedBoss = bossRepository.save(updatedBoss);
        return new AdminBossResponse(savedBoss);
    }
    
    // 보스 삭제 (소프트 삭제 - 비활성화)
    @Override
    @Transactional
    public void deleteBoss(Long id) {
        Boss boss = bossRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스입니다. ID: " + id));
        
        boss.deactivate();
        bossRepository.save(boss);
    }
    
    // 보스 완전 삭제 (하드 삭제)
    @Override
    @Transactional
    public void deleteBossCompletely(Long id) {
        Boss boss = bossRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스입니다. ID: " + id));
        
        bossRepository.delete(boss);
    }
    
    // 보스 활성화
    @Override
    @Transactional
    public AdminBossResponse activateBoss(Long id) {
        Boss boss = bossRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보스입니다. ID: " + id));
        
        boss.activate();
        Boss savedBoss = bossRepository.save(boss);
        return new AdminBossResponse(savedBoss);
    }
} 