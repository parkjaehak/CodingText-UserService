package org.userservice.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.dto.adminclient.AnnounceResponse;
import org.userservice.userservice.dto.codebankclient.UserScoreRequest;
import org.userservice.userservice.dto.user.UserInfoRequest;
import org.userservice.userservice.dto.user.UserInfoResponse;
import org.userservice.userservice.dto.user.UserStatisticResponse;
import org.userservice.userservice.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserApi {
    private final UserService userService;

    @Override
    @GetMapping("/statistics")
    public ResponseEntity<?> findUserStatistics(@RequestHeader("UserId") String userId) {
        log.info("userId ={} ", userId);
        UserStatisticResponse response = userService.findUserStatisticsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/userInfo")
    public ResponseEntity<?> findUserInfos(@RequestHeader("UserId") String userId) {
        UserInfoResponse response = userService.findUserInfoByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping(value = "/userInfo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserInfos(
            @Validated @RequestPart UserInfoRequest userInfoRequest,
            @RequestPart(required = false) MultipartFile file,
            @RequestHeader("UserId") String userId) {
        UserInfoResponse response = userService.updateUserInfoByUserId(userInfoRequest, file, userId);
        return ResponseEntity.ok(response);
    }

    //blog-service -> user-service
    @Override
    @GetMapping("/info")
    public ResponseEntity<?> findUserInfoForBlogService(@RequestHeader("UserId") String userId) {
        return ResponseEntity.ok(userService.findUserInfoForBlogService(userId));
    }

    //user-service -> admin-service
    @Override
    @GetMapping("/announce")
    public ResponseEntity<Page<AnnounceResponse>> getAnnouncementsFromAdminService(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAnnouncementsFromAdminService(page,size));
    }
    @Override
    @GetMapping("/announce/{announceId}")
    public ResponseEntity<?> getAnnouncementDetailsFromAdminService(@PathVariable long announceId){
        return ResponseEntity.ok(userService.getAnnouncementDetailsFromAdminService(announceId));
    }

    @Override
    @PutMapping("/score")
    public ResponseEntity<?> updateScore(@RequestBody UserScoreRequest userScoreRequest) {
        userService.calculateUserTier(userScoreRequest);
        return ResponseEntity.ok("점수 및 티어 업데이트 완료");
    }

    @Override
    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestHeader("UserId") String userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
