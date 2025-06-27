package com.yeoni.birdilegoapi.service;

import com.yeoni.birdilegoapi.domain.dto.event.EventDetailDto;
import com.yeoni.birdilegoapi.domain.dto.event.EventRequestDto;
import com.yeoni.birdilegoapi.domain.entity.AddressEntity;
import com.yeoni.birdilegoapi.domain.entity.EventEntity;
import com.yeoni.birdilegoapi.domain.entity.SponsorEntity;
import com.yeoni.birdilegoapi.exception.CustomException;
import com.yeoni.birdilegoapi.exception.ErrorCode;
import com.yeoni.birdilegoapi.mapper.AddressMapper;
import com.yeoni.birdilegoapi.mapper.EventMapper;
import com.yeoni.birdilegoapi.mapper.SponsorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final AddressMapper addressMapper;
    private final SponsorMapper sponsorMapper;


    @Transactional
    public EventDetailDto createEvent(EventRequestDto requestDto, Long creatorId) {

        // 1. 주소 정보 저장
        AddressEntity address = requestDto.getAddressEntity();
        address.setUserId(creatorId);
        addressMapper.save(address); // 이 호출 후 address 객체에 addressId가 채워짐

        // 2. 이벤트 정보 저장
        EventEntity event  = EventEntity.builder()
            .creatorId(creatorId)
            .eventName(requestDto.getEventName())
            .region(requestDto.getRegion())
            .venue(requestDto.getVenue())
            .startDate(requestDto.getStartDate())
            .endDate(requestDto.getEndDate())
            .addressId(address.getAddressId())
            .registrationStart(requestDto.getRegistrationStart())
            .registrationEnd(requestDto.getRegistrationEnd())
            .contactPhone(requestDto.getContactPhone())
            .imageUrl(requestDto.getImageUrl())
            .eventStatus(requestDto.getEventStatus())
            .prizeInfo(requestDto.getPrizeInfo())
            .entryFee(requestDto.getEntryFee())
            .maxParticipants(requestDto.getMaxParticipants())
            .eventDescription(requestDto.getEventDescription())
            .build();

        eventMapper.save(event);

        // 3. 스폰서 정보 저장
        List<SponsorEntity> sponsors = requestDto.getSponsorEntities();
        if (sponsors != null && !sponsors.isEmpty()) {
            for (SponsorEntity sponsor : sponsors) {
                sponsor.setEventId(event.getEventId()); // 저장된 이벤트의 ID를 설정
                sponsorMapper.save(sponsor);
            }
        }

        return EventDetailDto.builder()
            .eventEntity(event)
            .addressEntity(address)
            .sponsorEntities(sponsors)
            .build();
    }

    public Optional<EventEntity> getEventById(Long eventId) {
        return eventMapper.findById(eventId);
    }

    public List<EventEntity> getAllEvents(Long creatorId) {
        return eventMapper.findAll(creatorId);
    }

    @Transactional
    public EventEntity updateEvent(Long eventId, EventEntity eventEntityDetails, Long currentUserId) {
        EventEntity existingEventEntity = eventMapper.findById(eventId)
            .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        // 수정 권한 확인 (이벤트를 생성한 사용자인지 확인)
        if (!existingEventEntity.getCreatorId().equals(currentUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 업데이트할 필드만 설정
        eventEntityDetails.setEventId(eventId);
        eventMapper.update(eventEntityDetails);

        return eventMapper.findById(eventId)
            .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
    }

    @Transactional
    public void deleteEvent(Long eventId, Long currentUserId) {
        EventEntity existingEventEntity = eventMapper.findById(eventId)
            .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        // 삭제 권한 확인
        if (!existingEventEntity.getCreatorId().equals(currentUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        eventMapper.deleteById(eventId);
    }

    public Optional<EventDetailDto> getEventDetailById(Long eventId) {
        // 단 한 번의 쿼리로 모든 정보를 가져옴
        return eventMapper.findDetailById(eventId);
    }
}
