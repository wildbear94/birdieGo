package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.entity.AddressEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface AddressMapper {

    void save(AddressEntity addressEntity);
    Optional<AddressEntity> findById(Long addressId);
    void update(AddressEntity addressEntity);
    void deleteById(Long addressId);

}
