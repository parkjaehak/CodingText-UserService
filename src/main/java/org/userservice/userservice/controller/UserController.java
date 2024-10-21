package org.userservice.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.dto.user.UserInfoRequest;
import org.userservice.userservice.dto.user.UserInfoResponse;
import org.userservice.userservice.dto.user.UserStatisticResponse;
import org.userservice.userservice.service.UserService;
import org.userservice.userservice.utils.SecurityUtils;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserApi {
    private final UserService userService;

    @Override
    @GetMapping("/statistics")
    public ResponseEntity<?> findUserStatistics() {
        String userId = SecurityUtils.getCurrentUserId();
        UserStatisticResponse response = userService.findUserStatisticsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/userInfo")
    public ResponseEntity<?> findUserInfos() {
        String userId = SecurityUtils.getCurrentUserId();
        UserInfoResponse response = userService.findUserInfoByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping(value = "/userInfo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserInfos(
            @Validated @RequestPart UserInfoRequest userInfoRequest,
            @RequestPart(required = false) MultipartFile file) {
        String userId = SecurityUtils.getCurrentUserId();
        UserInfoResponse response = userService.updateUserInfoByUserId(userInfoRequest, file, userId);
        return ResponseEntity.ok(response);
    }
}
