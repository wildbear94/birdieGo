package com.yeoni.birdilegoapi.domain.dto.event;

import com.yeoni.birdilegoapi.domain.entity.AddressEntity;
import com.yeoni.birdilegoapi.domain.entity.EventEntity;
import com.yeoni.birdilegoapi.domain.entity.SponsorEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EventDetailDto {
    // EventEntity 기본 정보
    private EventEntity eventEntity;
    // 연관된 AddressEntity 정보
    private AddressEntity addressEntity;
    // 연관된 SponsorEntity 목록
    private List<SponsorEntity> sponsorEntities;

}
