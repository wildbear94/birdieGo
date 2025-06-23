package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.entity.ParticipantEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ParticipantMapper {

    void save(ParticipantEntity participant);
    Optional<ParticipantEntity> findById(Long participantId);
    void update(ParticipantEntity participant);
    void deleteById(Long participantId);
    List<ParticipantEntity> findByEventId(Long eventId);
    void registerEventParticipant(@Param("eventId") Long eventId, @Param("participantId") Long participantId);
    void deleteEventParticipant(@Param("eventId") Long eventId, @Param("participantId") Long participantId);

}
