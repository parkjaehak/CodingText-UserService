package org.userservice.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PutMapping("/userInfo")
    public ResponseEntity<UserInfoResponse> updateUserInfos(
            @ModelAttribute UserInfoRequest userInfoRequest) throws IOException {
        String userId = SecurityUtils.getCurrentUserId();
        UserInfoResponse response = userService.updateUserInfoByUserId(userInfoRequest, userId);
        return ResponseEntity.ok(response);
    }
}
