package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.entity.Match;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MatchMapper {

    void saveMatches(@Param("matches") List<Match> matches);

    List<Match> findAllByEventId(Long eventId);
    Optional<Match> findById(Long matchId);
    void update(Match match);
    void deleteById(Long matchId);

}
