package com.yeoni.birdilegoapi.domain.dto.group;

import lombok.Data;

import java.util.List;

@Data
public class GroupGenerationRequestDto {

    private String eventType;
    private List<String> ageGroups;
    private List<String> skillLevels;
    private LeagueType leagueType;

}
