package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.dto.group.GroupGenerationRequestDto;
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

    /**
     * 여러 참가자 정보를 한 번에 수정하는 메서드
     */
    void updateMultiple(@Param("participants") List<ParticipantEntity> participants);


    void deleteById(Long participantId);

    // 통계 조회를 위한 메서드 추가
    List<ParticipantStats> getStatsByEventType(@Param("eventId") Long eventId, @Param("eventType") String eventType);

    // 전체 통계 조회를 위한 메서드 추가
    List<ParticipantStats> getStatsForAllEventTypes(Long eventId);

    List<ParticipantEntity> findParticipantsForGrouping(
        @Param("eventId") Long eventId,
        @Param("eventType") String eventType,
        @Param("ageGroups") List<String> ageGroups,
        @Param("skillLevels") List<String> skillLevels
    );

    /**
     * 특정 대회의 모든 참가자를 삭제하는 메서드
     */
    void deleteAllByEventId(Long eventId);

    /**
     * 여러 참가자를 ID 목록을 기반으로 한 번에 삭제하는 메서드
     */
    void deleteByIds(@Param("uploadIds") List<Long> uploadIds);

}
