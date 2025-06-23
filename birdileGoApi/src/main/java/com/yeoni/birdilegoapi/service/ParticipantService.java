package com.yeoni.birdilegoapi.service;

import com.yeoni.birdilegoapi.domain.entity.ParticipantEntity;
import com.yeoni.birdilegoapi.mapper.ParticipantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantMapper participantMapper;

    @Transactional
    public ParticipantEntity registerParticipant(Long eventId, ParticipantEntity participant) {
        participantMapper.save(participant);
        participantMapper.registerEventParticipant(eventId, participant.getParticipantId());
        return participant;
    }

    public Optional<ParticipantEntity> getParticipantById(Long participantId) {
        return participantMapper.findById(participantId);
    }

    public List<ParticipantEntity> getParticipantsByEvent(Long eventId) {
        return participantMapper.findByEventId(eventId);
    }

    @Transactional
    public ParticipantEntity updateParticipant(Long participantId, ParticipantEntity participantDetails) {
        participantDetails.setParticipantId(participantId);
        participantMapper.update(participantDetails);
        return participantMapper.findById(participantId).orElseThrow();
    }

    @Transactional
    public void deleteParticipant(Long eventId, Long participantId) {
        participantMapper.deleteEventParticipant(eventId, participantId);
        participantMapper.deleteById(participantId);
    }
}
