package com.tech.padawan.financialmanager.party.service;

import com.tech.padawan.financialmanager.party.dto.ChangeChampionInPartyDTO;
import com.tech.padawan.financialmanager.party.dto.CreateUpdatePartyDTO;
import com.tech.padawan.financialmanager.party.dto.SearchedPartyDTO;
import com.tech.padawan.financialmanager.party.model.Party;
import org.springframework.data.domain.Page;

public interface IPartyService {
    Page<SearchedPartyDTO> findAllByUserId(Long id, String search, int page, int size, String orderBy, String direction);
    Party getById(Long id);
    SearchedPartyDTO getByIdFormatted(Long id);
    Party create(Long championId, CreateUpdatePartyDTO partyDTO);
    SearchedPartyDTO update(Long id, CreateUpdatePartyDTO partyDTO);
    Party updateCompleted(Party party);
    SearchedPartyDTO addChampion(Long id, ChangeChampionInPartyDTO dto);
    SearchedPartyDTO removeChampion(Long id, ChangeChampionInPartyDTO dto);
    String delete(Long id);
}
