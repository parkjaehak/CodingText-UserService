package org.userservice.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.userservice.userservice.dto.UserInfoResponse;
import org.userservice.userservice.dto.UserStatisticResponse;
import org.userservice.userservice.service.UserService;
import org.userservice.userservice.utils.SecurityUtils;

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
}
