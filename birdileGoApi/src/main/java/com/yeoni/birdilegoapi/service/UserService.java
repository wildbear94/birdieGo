package com.yeoni.birdilegoapi.service;

import com.yeoni.birdilegoapi.domain.dto.user.UserDetail;
import com.yeoni.birdilegoapi.domain.entity.User;
import com.yeoni.birdilegoapi.domain.entity.UserFile;
import com.yeoni.birdilegoapi.mapper.UserFileMapper;
import com.yeoni.birdilegoapi.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserFileMapper userFileMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Transactional
    public User registerUser(User user, List<MultipartFile> files) {
        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.save(user);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String storedFilePath = fileStorageService.storeFile(file);

                    // 파일 종류(MIME 타입)에 따른 분기 처리 예시
                    String fileType = file.getContentType();
                    if (fileType != null && fileType.startsWith("image")) {
                        // 이미지 파일일 경우 썸네일 생성 등의 추가 로직 수행 가능
                        System.out.println("Image file detected: " + file.getOriginalFilename());
                    } else {
                        // 문서 또는 기타 파일일 경우 다른 로직 수행 가능
                        System.out.println("Non-image file detected: " + file.getOriginalFilename());
                    }

                    UserFile userFile = UserFile.builder()
                        .userId(user.getUserId())
                        .fileType(file.getContentType()) // MIME 타입 저장
                        .originalFileName(file.getOriginalFilename())
                        .storedFilePath(storedFilePath)
                        .fileSize(file.getSize())
                        .build();
                    userFileMapper.save(userFile);
                }
            }
        }

        return user;
    }

    public Optional<User> getUserById(Long userId) {
        return userMapper.findById(userId);
    }

    public Optional<UserDetail> getUserDetailById(Long userId) {
        return userMapper.findDetailById(userId);
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