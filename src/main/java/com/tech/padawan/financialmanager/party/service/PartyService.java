package com.tech.padawan.financialmanager.party.service;

import com.tech.padawan.financialmanager.champion.model.Champion;
import com.tech.padawan.financialmanager.champion.service.ChampionService;
import com.tech.padawan.financialmanager.party.dto.ChangeChampionInPartyDTO;
import com.tech.padawan.financialmanager.party.dto.CreateUpdatePartyDTO;
import com.tech.padawan.financialmanager.party.dto.SearchedPartyDTO;
import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.model.PartyLevel;
import com.tech.padawan.financialmanager.party.repository.PartyLevelRepository;
import com.tech.padawan.financialmanager.party.repository.PartyRepository;
import com.tech.padawan.financialmanager.party.service.exception.ChampionAlreadyInThePartyException;
import com.tech.padawan.financialmanager.party.service.exception.PartyNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartyService implements IPartyService {

    private final PartyRepository repository;
    private final ChampionService championService;
    private final List<PartyLevel> levels;

    public PartyService(
            PartyRepository repository,
            ChampionService championService,
            PartyLevelRepository levelRepository
    ){
        this.repository = repository;
        this.championService = championService;
        this.levels = levelRepository.findAll();
    }

    @Override
    public Page<SearchedPartyDTO> findAllByUserId(Long id, String search, int page, int size, String orderBy, String direction) {
        Champion champion = championService.getByUserId(id);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.Direction.valueOf(direction), orderBy);
        Page<Party> partyPage = repository.findAllByChampionsIdAndNameContainingIgnoreCase(pageRequest, champion.getId(), search);
        return partyPage.map(party -> {
            return SearchedPartyDTO.from(party, levels);
        });
    }

    @Override
    public Party getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new PartyNotFoundException("Party with id " + id + " not found"));
    }

    @Override
    public SearchedPartyDTO getByIdFormatted(Long id) {
        Party party = this.getById(id);
        return SearchedPartyDTO.from(party, levels);
    }

    @Override
    public Party create(Long championId, CreateUpdatePartyDTO partyDTO) {
        Champion champion = championService.getById(championId);
        Party party = Party.builder()
                .name(partyDTO.name())
                .champions(List.of(champion))
                .createdAt(LocalDateTime.now())
                .build();
        return repository.save(party);
    }

    @Override
    public SearchedPartyDTO update(Long id, CreateUpdatePartyDTO partyDTO) {
        Party party = this.getById(id);
        party.setName(partyDTO.name());
        repository.save(party);
        return SearchedPartyDTO.from(party, levels);
    }

    @Override
    public Party updateCompleted(Party party) {
        return repository.save(party);
    }

    @Override
    public SearchedPartyDTO addChampion(Long id, ChangeChampionInPartyDTO dto) {
        Party party = this.getById(id);
        Champion champion = championService.getByNickname(dto.nickname());
        if(party.getChampions().contains(champion)){
            throw new ChampionAlreadyInThePartyException("Champion " + dto.nickname() + " already in this party");
        }
        List<Champion> partyChampions = party.getChampions();
        partyChampions.add(champion);

        party.setChampions(partyChampions);

        repository.save(party);

        return SearchedPartyDTO.from(party, levels);
    }

    @Override
    public SearchedPartyDTO removeChampion(Long id, ChangeChampionInPartyDTO dto) {
        Party party = this.getById(id);
        Champion champion = championService.getByNickname(dto.nickname());

        List<Champion> partyChampions = party.getChampions();
        partyChampions.remove(champion);

        party.setChampions(partyChampions);

        repository.save(party);

        return SearchedPartyDTO.from(party, levels);
    }

    @Override
    public String delete(Long id) {
        this.getById(id);
        repository.deleteById(id);
        return "Party deleted";

    }

}
