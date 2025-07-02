package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.DesireItemDto;
import com.happymapleday.boss.entity.Boss;
import com.happymapleday.boss.entity.DesireItem;
import com.happymapleday.boss.repository.BossRepository;
import com.happymapleday.boss.repository.DesireItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DesireItemService {

    private final DesireItemRepository desireItemRepository;
    private final BossRepository bossRepository;

    // 특정 보스의 모든 물욕템 조회
    public List<DesireItemDto.Response> getDesireItemsByBossId(Long bossId) {
        return desireItemRepository.findByBossIdOrderByItemName(bossId)
                .stream()
                .map(DesireItemDto.Response::from)
                .toList();
    }

    // 특정 보스의 물욕템 조회 (랜덤박스 아이템 포함)
    public List<DesireItemDto.Response> getDesireItemsWithRandomBoxByBossId(Long bossId) {
        return desireItemRepository.findByBossIdOrderByItemName(bossId)
                .stream()
                .map(DesireItemDto.Response::fromWithRandomBoxItems)
                .toList();
    }

    // ID로 물욕템 상세 조회
    public DesireItemDto.Response getDesireItemById(Long id) {
        DesireItem desireItem = desireItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 물욕템을 찾을 수 없습니다: " + id));
        return DesireItemDto.Response.fromWithRandomBoxItems(desireItem);
    }

    // 랜덤박스 아이템만 조회
    public List<DesireItemDto.Response> getRandomBoxItemsByBossId(Long bossId) {
        return desireItemRepository.findRandomBoxItemsByBossId(bossId)
                .stream()
                .map(DesireItemDto.Response::fromWithRandomBoxItems)
                .toList();
    }

    // 일반 물욕템만 조회 (랜덤박스가 아닌)
    public List<DesireItemDto.Response> getNormalDesireItemsByBossId(Long bossId) {
        return desireItemRepository.findNormalDesireItemsByBossId(bossId)
                .stream()
                .map(DesireItemDto.Response::from)
                .toList();
    }

    // 아이템명으로 검색
    public List<DesireItemDto.Response> searchDesireItemsByName(String itemName) {
        return desireItemRepository.findByItemNameContainingIgnoreCaseOrderByItemName(itemName)
                .stream()
                .map(DesireItemDto.Response::from)
                .toList();
    }

    // 특정 보스의 물욕템 개수 조회
    public long countDesireItemsByBossId(Long bossId) {
        Boss boss = bossRepository.findById(bossId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 보스를 찾을 수 없습니다: " + bossId));
        return desireItemRepository.countByBoss(boss);
    }

    // 물욕템 생성
    @Transactional
    public DesireItemDto.Response createDesireItem(DesireItemDto.CreateRequest createRequest) {
        Boss boss = bossRepository.findById(createRequest.getBossId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 보스를 찾을 수 없습니다: " + createRequest.getBossId()));

        // 중복 체크
        if (desireItemRepository.findByBossAndItemName(boss, createRequest.getItemName()).isPresent()) {
            throw new IllegalArgumentException("해당 보스에 이미 동일한 이름의 물욕템이 존재합니다: " + createRequest.getItemName());
        }

        DesireItem desireItem = DesireItem.builder()
                .boss(boss)
                .itemName(createRequest.getItemName())
                .isRandomBox(createRequest.getIsRandomBox())
                .build();

        DesireItem savedDesireItem = desireItemRepository.save(desireItem);
        log.info("새로운 물욕템이 생성되었습니다: {} (보스: {})", 
                savedDesireItem.getItemName(), boss.getFullName());

        return DesireItemDto.Response.from(savedDesireItem);
    }

    // 물욕템 수정
    @Transactional
    public DesireItemDto.Response updateDesireItem(Long id, DesireItemDto.UpdateRequest updateRequest) {
        DesireItem desireItem = desireItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 물욕템을 찾을 수 없습니다: " + id));

        // 아이템명이 변경되는 경우 중복 체크
        if (updateRequest.getItemName() != null && 
            !updateRequest.getItemName().equals(desireItem.getItemName())) {
            if (desireItemRepository.findByBossAndItemName(desireItem.getBoss(), updateRequest.getItemName()).isPresent()) {
                throw new IllegalArgumentException("해당 보스에 이미 동일한 이름의 물욕템이 존재합니다: " + updateRequest.getItemName());
            }
        }

        // 필드 업데이트 (엔티티에 업데이트 메서드가 있다면 사용, 없다면 리플렉션 또는 직접 수정)
        // 현재는 엔티티에 업데이트 메서드가 없으므로 새로운 객체를 생성
        DesireItem updatedDesireItem = DesireItem.builder()
                .boss(desireItem.getBoss())
                .itemName(updateRequest.getItemName() != null ? updateRequest.getItemName() : desireItem.getItemName())
                .isRandomBox(updateRequest.getIsRandomBox() != null ? updateRequest.getIsRandomBox() : desireItem.getIsRandomBox())
                .build();

        // ID 설정 (필요시 리플렉션 사용)
        try {
            java.lang.reflect.Field idField = DesireItem.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(updatedDesireItem, desireItem.getId());
        } catch (Exception e) {
            log.error("ID 설정 중 오류 발생", e);
        }

        DesireItem savedDesireItem = desireItemRepository.save(updatedDesireItem);
        log.info("물욕템이 수정되었습니다: {}", savedDesireItem.getItemName());

        return DesireItemDto.Response.from(savedDesireItem);
    }

    // 물욕템 삭제
    @Transactional
    public void deleteDesireItem(Long id) {
        DesireItem desireItem = desireItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 물욕템을 찾을 수 없습니다: " + id));

        desireItemRepository.delete(desireItem);
        log.info("물욕템이 삭제되었습니다: {} (보스: {})", 
                desireItem.getItemName(), desireItem.getBoss().getFullName());
    }

    // 보스별 물욕템 일괄 삭제
    @Transactional
    public void deleteDesireItemsByBossId(Long bossId) {
        Boss boss = bossRepository.findById(bossId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 보스를 찾을 수 없습니다: " + bossId));

        List<DesireItem> desireItems = desireItemRepository.findByBossOrderByItemName(boss);
        desireItemRepository.deleteAll(desireItems);
        
        log.info("보스 '{}'의 모든 물욕템이 삭제되었습니다. ({}개)", boss.getFullName(), desireItems.size());
    }

    // 특정 보스의 특정 랜덤박스/일반 아이템만 조회
    public List<DesireItemDto.Response> getDesireItemsByBossAndType(Long bossId, Boolean isRandomBox) {
        Boss boss = bossRepository.findById(bossId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 보스를 찾을 수 없습니다: " + bossId));

        return desireItemRepository.findByBossAndIsRandomBoxOrderByItemName(boss, isRandomBox)
                .stream()
                .map(isRandomBox ? DesireItemDto.Response::fromWithRandomBoxItems : DesireItemDto.Response::from)
                .toList();
    }
} 