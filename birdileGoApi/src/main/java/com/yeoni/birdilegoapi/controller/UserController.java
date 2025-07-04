package com.yeoni.birdilegoapi.controller;

import com.yeoni.birdilegoapi.domain.dto.CommonResponse;
import com.yeoni.birdilegoapi.domain.dto.user.UserDetail;
import com.yeoni.birdilegoapi.domain.entity.User;
import com.yeoni.birdilegoapi.exception.CustomException;
import com.yeoni.birdilegoapi.exception.ErrorCode;
import com.yeoni.birdilegoapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 사용자 등록 (회원가입)
    @PostMapping(value = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommonResponse<User>> registerUser(
        @RequestBody User user,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        User registeredUser = userService.registerUser(user, files);
        // 비밀번호 필드는 응답에서 제외
        registeredUser.setPassword(null);

        CommonResponse<User> response = CommonResponse.<User>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("사용자 등록 및 파일 업로드가 성공적으로 완료되었습니다.")
            .data(registeredUser)
            .build();

        return ResponseEntity.ok(response);
    }

    // 모든 사용자 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // 비밀번호 필드는 응답에서 제외
        users.forEach(user -> user.setPassword(null));

        CommonResponse<List<User>> response = CommonResponse.<List<User>>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("사용자 목록 조회가 성공적으로 완료되었습니다.")
            .data(users)
            .build();

        return ResponseEntity.ok(response);
    }

    // 특정 사용자 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<UserDetail>> getUserById(@PathVariable Long id) {
        UserDetail userDetail = userService.getUserDetailById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userDetail.setPassword(null); // 비밀번호 필드 제외

        return ResponseEntity.ok(CommonResponse.<UserDetail>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("사용자 상세 정보 조회가 성공적으로 완료되었습니다.")
            .data(userDetail)
            .build());
    }

    // 사용자 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<User>> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            updatedUser.setPassword(null); // 비밀번호 필드 제외
            CommonResponse<User> response = CommonResponse.<User>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message("사용자 정보 수정이 성공적으로 완료되었습니다.")
                .data(updatedUser)
                .build();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CommonResponse<User> response = CommonResponse.<User>builder()
                .status(HttpStatus.NOT_FOUND.value())
                .code("USER_NOT_FOUND")
                .message(e.getMessage())
                .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 사용자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        CommonResponse<Void> response = CommonResponse.<Void>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("사용자 삭제가 성공적으로 완료되었습니다.")
            .build();
        return ResponseEntity.ok(response);
    }
}