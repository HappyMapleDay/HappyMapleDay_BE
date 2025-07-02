package com.happymapleday.boss.service;

import com.happymapleday.boss.dto.RandomBoxItemDto;
import com.happymapleday.boss.entity.DesireItem;
import com.happymapleday.boss.entity.RandomBoxItem;
import com.happymapleday.boss.repository.DesireItemRepository;
import com.happymapleday.boss.repository.RandomBoxItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RandomBoxItemService {

    private final RandomBoxItemRepository randomBoxItemRepository;
    private final DesireItemRepository desireItemRepository;

    // 특정 물욕템의 모든 랜덤박스 아이템 조회
    public List<RandomBoxItemDto.Response> getRandomBoxItemsByDesireItemId(Long desireItemId) {
        return randomBoxItemRepository.findByDesireItemIdOrderByDropItemName(desireItemId)
                .stream()
                .map(RandomBoxItemDto.Response::from)
                .toList();
    }

    // ID로 랜덤박스 아이템 상세 조회
    public RandomBoxItemDto.Response getRandomBoxItemById(Long id) {
        RandomBoxItem randomBoxItem = randomBoxItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 랜덤박스 아이템을 찾을 수 없습니다: " + id));
        return RandomBoxItemDto.Response.from(randomBoxItem);
    }

    // 드랍 아이템명으로 검색
    public List<RandomBoxItemDto.Response> searchRandomBoxItemsByName(String dropItemName) {
        return randomBoxItemRepository.findByDropItemNameContainingIgnoreCaseOrderByDropItemName(dropItemName)
                .stream()
                .map(RandomBoxItemDto.Response::from)
                .toList();
    }

    // 레벨이 있는 아이템만 조회
    public List<RandomBoxItemDto.Response> getRandomBoxItemsWithLevel(Long desireItemId) {
        DesireItem desireItem = desireItemRepository.findById(desireItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 물욕템을 찾을 수 없습니다: " + desireItemId));

        return randomBoxItemRepository.findByDesireItemAndDropItemLevelIsNotNullOrderByDropItemLevelDesc(desireItem)
                .stream()
                .map(RandomBoxItemDto.Response::from)
                .toList();
    }

    // 레벨이 없는 아이템만 조회
    public List<RandomBoxItemDto.Response> getRandomBoxItemsWithoutLevel(Long desireItemId) {
        DesireItem desireItem = desireItemRepository.findById(desireItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 물욕템을 찾을 수 없습니다: " + desireItemId));

        return randomBoxItemRepository.findByDesireItemAndDropItemLevelIsNullOrderByDropItemName(desireItem)
                .stream()
                .map(RandomBoxItemDto.Response::from)
                .toList();
    }

    // 보스별 모든 랜덤박스 아이템 조회
    public List<RandomBoxItemDto.Response> getAllRandomBoxItemsByBossId(Long bossId) {
        return randomBoxItemRepository.findAllByBossId(bossId)
                .stream()
                .map(RandomBoxItemDto.Response::from)
                .toList();
    }

    // 특정 물욕템의 랜덤박스 아이템 개수 조회
    public long countRandomBoxItemsByDesireItemId(Long desireItemId) {
        DesireItem desireItem = desireItemRepository.findById(desireItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 물욕템을 찾을 수 없습니다: " + desireItemId));
        return randomBoxItemRepository.countByDesireItem(desireItem);
    }

    // 랜덤박스 아이템 생성
    @Transactional
    public RandomBoxItemDto.Response createRandomBoxItem(RandomBoxItemDto.CreateRequest createRequest) {
        DesireItem desireItem = desireItemRepository.findById(createRequest.getDesireItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 물욕템을 찾을 수 없습니다: " + createRequest.getDesireItemId()));

        // 물욕템이 랜덤박스가 아닌 경우 에러
        if (!desireItem.getIsRandomBox()) {
            throw new IllegalArgumentException("랜덤박스가 아닌 물욕템에는 드랍 아이템을 추가할 수 없습니다.");
        }

        // 중복 체크
        if (randomBoxItemRepository.findByDesireItemAndDropItemNameAndDropItemLevel(
                desireItem, createRequest.getDropItemName(), createRequest.getDropItemLevel()).isPresent()) {
            throw new IllegalArgumentException("이미 동일한 드랍 아이템이 존재합니다: " + 
                    createRequest.getDropItemName() + 
                    (createRequest.getDropItemLevel() != null ? " (Lv." + createRequest.getDropItemLevel() + ")" : ""));
        }

        RandomBoxItem randomBoxItem = RandomBoxItem.builder()
                .desireItem(desireItem)
                .dropItemName(createRequest.getDropItemName())
                .dropItemLevel(createRequest.getDropItemLevel())
                .build();

        RandomBoxItem savedRandomBoxItem = randomBoxItemRepository.save(randomBoxItem);
        log.info("새로운 랜덤박스 아이템이 생성되었습니다: {} (물욕템: {})", 
                savedRandomBoxItem.getFullDropItemName(), desireItem.getItemName());

        return RandomBoxItemDto.Response.from(savedRandomBoxItem);
    }

    // 랜덤박스 아이템 수정
    @Transactional
    public RandomBoxItemDto.Response updateRandomBoxItem(Long id, RandomBoxItemDto.UpdateRequest updateRequest) {
        RandomBoxItem randomBoxItem = randomBoxItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 랜덤박스 아이템을 찾을 수 없습니다: " + id));

        // 드랍 아이템명이나 레벨이 변경되는 경우 중복 체크
        String newDropItemName = updateRequest.getDropItemName() != null ? 
                updateRequest.getDropItemName() : randomBoxItem.getDropItemName();
        Integer newDropItemLevel = updateRequest.getDropItemLevel() != null ? 
                updateRequest.getDropItemLevel() : randomBoxItem.getDropItemLevel();

        if (!newDropItemName.equals(randomBoxItem.getDropItemName()) || 
            !java.util.Objects.equals(newDropItemLevel, randomBoxItem.getDropItemLevel())) {
            
            if (randomBoxItemRepository.findByDesireItemAndDropItemNameAndDropItemLevel(
                    randomBoxItem.getDesireItem(), newDropItemName, newDropItemLevel).isPresent()) {
                throw new IllegalArgumentException("이미 동일한 드랍 아이템이 존재합니다: " + 
                        newDropItemName + 
                        (newDropItemLevel != null ? " (Lv." + newDropItemLevel + ")" : ""));
            }
        }

        // 필드 업데이트 (엔티티에 업데이트 메서드가 있다면 사용, 없다면 새로운 객체 생성)
        RandomBoxItem updatedRandomBoxItem = RandomBoxItem.builder()
                .desireItem(randomBoxItem.getDesireItem())
                .dropItemName(newDropItemName)
                .dropItemLevel(newDropItemLevel)
                .build();

        // ID 설정
        try {
            java.lang.reflect.Field idField = RandomBoxItem.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(updatedRandomBoxItem, randomBoxItem.getId());
        } catch (Exception e) {
            log.error("ID 설정 중 오류 발생", e);
        }

        RandomBoxItem savedRandomBoxItem = randomBoxItemRepository.save(updatedRandomBoxItem);
        log.info("랜덤박스 아이템이 수정되었습니다: {}", savedRandomBoxItem.getFullDropItemName());

        return RandomBoxItemDto.Response.from(savedRandomBoxItem);
    }

    // 랜덤박스 아이템 삭제
    @Transactional
    public void deleteRandomBoxItem(Long id) {
        RandomBoxItem randomBoxItem = randomBoxItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 랜덤박스 아이템을 찾을 수 없습니다: " + id));

        randomBoxItemRepository.delete(randomBoxItem);
        log.info("랜덤박스 아이템이 삭제되었습니다: {} (물욕템: {})", 
                randomBoxItem.getFullDropItemName(), randomBoxItem.getDesireItem().getItemName());
    }

    // 특정 물욕템의 모든 랜덤박스 아이템 삭제
    @Transactional
    public void deleteRandomBoxItemsByDesireItemId(Long desireItemId) {
        DesireItem desireItem = desireItemRepository.findById(desireItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 물욕템을 찾을 수 없습니다: " + desireItemId));

        List<RandomBoxItem> randomBoxItems = randomBoxItemRepository.findByDesireItemOrderByDropItemName(desireItem);
        randomBoxItemRepository.deleteAll(randomBoxItems);
        
        log.info("물욕템 '{}'의 모든 랜덤박스 아이템이 삭제되었습니다. ({}개)", 
                desireItem.getItemName(), randomBoxItems.size());
    }

    // 랜덤박스 아이템 일괄 생성
    @Transactional
    public List<RandomBoxItemDto.Response> createRandomBoxItemsBatch(List<RandomBoxItemDto.CreateRequest> createRequests) {
        return createRequests.stream()
                .map(this::createRandomBoxItem)
                .toList();
    }
} 