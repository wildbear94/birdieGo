package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.entity.SponsorEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SponsorMapper {

    void save(SponsorEntity sponsorEntity);
    List<SponsorEntity> findByEventId(Long eventId);
    void deleteByEventId(Long eventId);

}
