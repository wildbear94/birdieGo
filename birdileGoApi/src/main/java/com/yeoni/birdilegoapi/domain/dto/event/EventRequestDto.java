package com.yeoni.birdilegoapi.domain.dto.event;

import com.yeoni.birdilegoapi.domain.entity.AddressEntity;
import com.yeoni.birdilegoapi.domain.entity.SponsorEntity;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class EventRequestDto {

    private String eventName;
    private String region;
    private String venue;
    private Long addressId;
    private Date startDate;
    private Date endDate;
    private Date registrationStart;
    private Date registrationEnd;
    private String contactPhone;
    private String imageUrl;
    private String eventStatus;
    private String prizeInfo;
    private BigDecimal entryFee;
    private Integer maxParticipants;
    private String eventDescription;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String eventKind;

    private AddressEntity addressEntity;

    private List<SponsorEntity> sponsorEntities;
}
