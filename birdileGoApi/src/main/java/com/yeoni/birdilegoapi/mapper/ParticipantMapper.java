package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.dto.participant.ParticipantStats;
import com.yeoni.birdilegoapi.domain.entity.ParticipantEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ParticipantMapper {

    void save(ParticipantEntity participantEntity);

    void saveBatch(List<ParticipantEntity> participantEntityList);

    Optional<ParticipantEntity> findById(Long uploadId);

    List<ParticipantEntity> findByEventId(Long eventId);

    List<ParticipantEntity> findByEventIdAndEventType(@Param("eventId") Long eventId, @Param("eventType") String eventType);

    void update(ParticipantEntity participantEntity);

    void deleteById(Long participantId);

    // 통계 조회를 위한 메서드 추가
    List<ParticipantStats> getStatsByEventType(@Param("eventId") Long eventId, @Param("eventType") String eventType);

    // 전체 통계 조회를 위한 메서드 추가
    List<ParticipantStats> getStatsForAllEventTypes(Long eventId);

}
