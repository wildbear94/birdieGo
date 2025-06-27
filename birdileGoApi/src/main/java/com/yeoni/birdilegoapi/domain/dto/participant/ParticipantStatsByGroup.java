package com.yeoni.birdilegoapi.domain.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantStatsByGroup {

    private String ageGroup;
    private String skillLevel;
    private int teamCount;

}
