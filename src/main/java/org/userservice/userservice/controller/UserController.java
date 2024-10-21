package org.userservice.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.dto.user.UserInfoRequest;
import org.userservice.userservice.dto.user.UserInfoResponse;
import org.userservice.userservice.dto.user.UserStatisticResponse;
import org.userservice.userservice.service.UserService;
import org.userservice.userservice.utils.SecurityUtils;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/statistics")
    public ResponseEntity<UserStatisticResponse> findUserStatistics() {
        String userId = SecurityUtils.getCurrentUserId();
        UserStatisticResponse response = userService.findUserStatisticsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/userInfo")
    public ResponseEntity<UserInfoResponse> findUserInfos() {
        String userId = SecurityUtils.getCurrentUserId();
        UserInfoResponse response = userService.findUserInfoByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/userInfo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserInfoResponse> updateUserInfos(
            @RequestPart UserInfoRequest userInfoRequest,
            @RequestPart MultipartFile file) {
        String userId = SecurityUtils.getCurrentUserId();
        UserInfoResponse response = userService.updateUserInfoByUserId(userInfoRequest, file, userId);
        return ResponseEntity.ok(response);
    }
}
