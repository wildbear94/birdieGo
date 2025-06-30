package com.yeoni.birdilegoapi.domain.dto.user;

import com.yeoni.birdilegoapi.domain.entity.User;
import com.yeoni.birdilegoapi.domain.entity.UserFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDetail extends User {
    private List<UserFile> files;
}
