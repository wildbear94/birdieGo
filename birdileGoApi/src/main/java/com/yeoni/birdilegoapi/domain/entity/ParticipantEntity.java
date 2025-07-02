package com.yeoni.birdilegoapi.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Builder
public class ParticipantEntity {

    private Long participantId;
    private Long eventId;
    private Long uploaderId;
    private String eventType;
    private String ageGroup;
    private String skillLevel;
    private String teamName;
    private String participant1Name;
    private String participant1Birth;
    private String participant1Phone;
    private String participant2Name;
    private String participant2Birth;
    private String participant2Phone;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String userYn;

}
