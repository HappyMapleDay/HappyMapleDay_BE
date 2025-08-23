package com.happymapleday.character.repository;

import com.happymapleday.character.entity.CharacterSelectedBoss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterSelectedBossRepository extends JpaRepository<CharacterSelectedBoss, Long> {

    @Query("SELECT csb FROM CharacterSelectedBoss csb WHERE csb.characterId IN :characterIds")
    List<CharacterSelectedBoss> findByCharacterIdIn(@Param("characterIds") List<Long> characterIds);
}


