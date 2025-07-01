package com.happymapleday.boss.repository;

import com.happymapleday.boss.entity.BossPreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BossPresetRepository extends JpaRepository<BossPreset, Long> {

    // 프리셋 이름으로 조회
    Optional<BossPreset> findByPresetName(String presetName);

    // 프리셋 이름 중복 체크
    boolean existsByPresetName(String presetName);

    // 생성일순으로 정렬된 모든 프리셋 조회
    List<BossPreset> findAllByOrderByCreatedAtDesc();

    // 프리셋 이름으로 검색 (부분 일치)
    List<BossPreset> findByPresetNameContainingIgnoreCaseOrderByCreatedAtDesc(String presetName);

    // 특정 보스를 포함하는 프리셋 조회
    @Query("SELECT bp FROM BossPreset bp WHERE JSON_CONTAINS(bp.bossIds, :bossIdJson)")
    List<BossPreset> findPresetsContainingBoss(@Param("bossIdJson") String bossIdJson);
} 