package com.yeoni.birdilegoapi.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class MatchGroup {

    private Long groupId;
    private Long eventId;
    private String groupName;
    private String eventType;
    private String ageGroup;
    private String skillLevel;
    private String leagueType;
    private int teamCount;
    private Timestamp createdAt;
}
