package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.entity.EventFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EventFileMapper {

    void save(EventFile eventFile);
    List<EventFile> findByEventId(Long eventId);
}
