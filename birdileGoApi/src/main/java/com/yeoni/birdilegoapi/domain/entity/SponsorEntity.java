package com.yeoni.birdilegoapi.domain.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SponsorEntity {

    private Long eventId;
    private String sponsorName;
    private String sponsorType;
    private String sponsorUrl;

}
