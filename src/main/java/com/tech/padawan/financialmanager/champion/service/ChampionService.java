package com.tech.padawan.financialmanager.champion.service;

import com.tech.padawan.financialmanager.champion.model.Champion;
import com.tech.padawan.financialmanager.champion.repository.ChampionRepository;
import com.tech.padawan.financialmanager.champion.service.exception.ChampionNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ChampionService implements IChampionService{

    private final ChampionRepository repository;

    private ChampionService(
            ChampionRepository repository
    ){
        this.repository = repository;
    }

    @Override
    public Champion getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ChampionNotFoundException("Champion with id " + id + " not found"));
    }

    @Override
    public Champion getByUserId(Long id) {
        return repository.getByUserId(id).orElseThrow(() -> new ChampionNotFoundException("Champion of user with id " + id + " not found"));
    }

    @Override
    public Champion getByNickname(String nickname) {
        return repository.getByNickname(nickname).orElseThrow(() -> new ChampionNotFoundException("Champion " + nickname + " not found"));
    }
}
