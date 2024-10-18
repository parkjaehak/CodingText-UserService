package org.userservice.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.userservice.userservice.service.UserService;
import org.userservice.userservice.utils.SecurityUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/statistics")
    public void findUserInfo() {
        String userId = SecurityUtils.getCurrentUserId();


    }
}
