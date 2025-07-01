package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.dto.group.MatchGroupDetailDto;
import com.yeoni.birdilegoapi.domain.entity.MatchGroup;
import com.yeoni.birdilegoapi.domain.entity.MatchGroupTeam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MatchGroupMapper {

    void saveGroup(MatchGroup matchGroup);
    void saveGroupTeams(@Param("teams") List<MatchGroupTeam> teams);
    void deleteGroupById(Long groupId);
    Optional<MatchGroupDetailDto> findGroupDetailsById(Long groupId);
    List<MatchGroup> findAllGroupsByEventId(Long eventId);
    /**
     * 특정 대회의 특정 종목에 해당하는 리그/풀리그 조 목록을 조회하는 메서드
     */
    List<MatchGroup> findGroupsByEventTypeAndLeagueTypes(
        @Param("eventId") Long eventId,
        @Param("eventType") String eventType,
        @Param("leagueTypes") List<String> leagueTypes
    );

}
