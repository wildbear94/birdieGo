package com.yeoni.birdilegoapi.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AddressEntity {

    private Long addressId;
    private String roadAddress;
    private String landlotAddress;
    private String extraAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long userId;
}
