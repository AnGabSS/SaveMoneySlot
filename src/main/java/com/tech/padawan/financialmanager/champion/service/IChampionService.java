package com.tech.padawan.financialmanager.champion.service;

import com.tech.padawan.financialmanager.champion.model.Champion;

public interface IChampionService {
    Champion getById(Long id);
    Champion getByUserId(Long id);
    Champion getByNickname(String nickname);
}
