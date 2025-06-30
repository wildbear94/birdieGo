package com.yeoni.birdilegoapi.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class MatchGroupTeam {

    private Long groupTeamId;
    private Long groupId;
    private Long participantId;
    private String teamName;
    private Timestamp createdAt;

}
