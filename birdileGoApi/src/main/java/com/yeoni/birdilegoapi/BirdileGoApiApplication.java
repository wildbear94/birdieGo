package com.yeoni.birdilegoapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.yeoni.birdilegoapi.mapper")
public class BirdileGoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BirdileGoApiApplication.class, args);
    }

}
