package com.tech.padawan.financialmanager.champion.repository;

import com.tech.padawan.financialmanager.champion.model.Champion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChampionRepository extends JpaRepository<Champion, Long> {
}
