package com.yeoni.birdilegoapi.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Builder
public class ParticipantEntity {

    private Long participantId;
    private String participantName;
    private String phone;
    private String email;
    private String participantRole;
    private String clubName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String gender;
    private Date birthDate;
    private String skillLevel;
    private String emergencyContact;
    private String ageRange;

}
