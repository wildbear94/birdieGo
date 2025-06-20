package com.yeoni.birdilegoapi.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@Builder
public class EventEntity {
    private Long eventId;
    private Long creatorId;
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
}
