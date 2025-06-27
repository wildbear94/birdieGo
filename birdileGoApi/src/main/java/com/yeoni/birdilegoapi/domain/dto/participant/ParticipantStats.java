package com.yeoni.birdilegoapi.domain.dto.participant;

import lombok.Data;

@Data
public class ParticipantStats {

    private String eventType;
    private String ageGroup;
    private String skillLevel;
    private int teamCount;

}
