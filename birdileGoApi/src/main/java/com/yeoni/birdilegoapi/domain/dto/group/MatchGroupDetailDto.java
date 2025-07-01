package com.yeoni.birdilegoapi.domain.dto.group;

import com.yeoni.birdilegoapi.domain.entity.MatchGroupTeam;
import lombok.Data;

import java.util.List;

@Data
public class MatchGroupDetailDto {
    private List<MatchGroupTeam> teams;
}
