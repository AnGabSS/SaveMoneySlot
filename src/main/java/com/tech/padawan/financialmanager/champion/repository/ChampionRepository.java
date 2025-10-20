package com.tech.padawan.financialmanager.champion.repository;

import com.tech.padawan.financialmanager.champion.model.Champion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChampionRepository extends JpaRepository<Champion, Long> {
    Optional<Champion> getByNickname(String nickname);
    Optional<Champion> getByUserId(Long userId);
}
