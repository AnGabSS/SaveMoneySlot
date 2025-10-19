package com.tech.padawan.financialmanager.party.repository;

import com.tech.padawan.financialmanager.champion.model.Champion;
import com.tech.padawan.financialmanager.party.model.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, Long> {
    Page<Party> findAllByChampionsIdAndNameContainingIgnoreCase(Pageable pageable, Long championId, String nameSearch);
}
