package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.entity.MatchGroup;
import com.yeoni.birdilegoapi.domain.entity.MatchGroupTeam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatchGroupMapper {

    void saveGroup(MatchGroup matchGroup);
    void saveGroupTeams(@Param("teams") List<MatchGroupTeam> teams);

}
