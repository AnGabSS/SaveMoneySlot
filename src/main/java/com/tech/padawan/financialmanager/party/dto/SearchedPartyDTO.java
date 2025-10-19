package com.tech.padawan.financialmanager.party.dto;

import com.tech.padawan.financialmanager.champion.model.Champion;
import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.model.PartyLevel;

import java.time.LocalDateTime;
import java.util.List;

public record SearchedPartyDTO(
        Long id,
        String name,
        Integer points,
        String level,
        LocalDateTime createdAt,
        List<String> champions
) {
    public static SearchedPartyDTO from(Party party, List<PartyLevel> levels){
      String correspondingLevel = "Beginner";
      List<PartyLevel> filteredList = levels.stream().filter((level) -> {
          return level.pointsIsEnough(party.getPoints());
      }).toList();

      if(!filteredList.isEmpty()){
          correspondingLevel = filteredList.get(0).getTitle();
      }



      List<String> championsNames = party.getChampions().stream().map(Champion::getNickname).toList();

      return new SearchedPartyDTO(
              party.getId(),
              party.getName(),
              party.getPoints(),
              correspondingLevel,
              party.getCreatedAt(),
              championsNames
      );
    };

    public static SearchedPartyDTO from(Party party){

      List<String> championsNames = party.getChampions().stream().map(Champion::getNickname).toList();

      return new SearchedPartyDTO(
              party.getId(),
              party.getName(),
              party.getPoints(),
              "Beginner",
              party.getCreatedAt(),
              championsNames
      );
    };
}
