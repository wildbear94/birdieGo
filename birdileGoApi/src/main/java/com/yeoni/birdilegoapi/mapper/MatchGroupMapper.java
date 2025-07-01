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

}
