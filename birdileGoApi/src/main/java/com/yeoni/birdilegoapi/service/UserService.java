package com.yeoni.birdilegoapi.service;

import com.yeoni.birdilegoapi.domain.entity.User;
import com.yeoni.birdilegoapi.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(User user) {
        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.save(user);
        return user;
    }

    public Optional<User> getUserById(Long userId) {
        return userMapper.findById(userId);
    }

    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    @Transactional
    public User updateUser(Long userId, User userDetails) {
        User existingUser = userMapper.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 필요한 필드 업데이트
        if (userDetails.getDisplayName() != null) {
            existingUser.setDisplayName(userDetails.getDisplayName());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        // ... 기타 업데이트할 필드들
        existingUser.setContactPhone(userDetails.getContactPhone());
        existingUser.setEmail(userDetails.getEmail());


        userMapper.update(existingUser);
        return existingUser;
    }

    @Transactional
    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }
}