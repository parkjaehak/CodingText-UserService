package org.userservice.userservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.userservice.controller.feignclient.BlogServiceClient;
import org.userservice.userservice.domain.Tier;
import org.userservice.userservice.dto.adminclient.CustomPageImpl;
import org.userservice.userservice.controller.feignclient.AdminServiceClient;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.dto.adminclient.AnnounceDetailResponse;
import org.userservice.userservice.dto.adminclient.AnnounceResponse;
import org.userservice.userservice.dto.codebankclient.UserScoreRequest;
import org.userservice.userservice.dto.user.UserInfoForBlogResponse;
import org.userservice.userservice.dto.user.UserInfoRequest;
import org.userservice.userservice.dto.user.UserInfoResponse;
import org.userservice.userservice.dto.user.UserStatisticResponse;
import org.userservice.userservice.dto.user.*;
import org.userservice.userservice.error.ErrorCode;
import org.userservice.userservice.error.exception.BusinessException;
import org.userservice.userservice.error.exception.CreationException;
import org.userservice.userservice.error.exception.UserNotFoundException;
import org.userservice.userservice.repository.RedisRepository;
import org.userservice.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final MinioFileUploadService minioFileUploadService;
    private final AdminServiceClient adminServiceClient;
    private final BlogServiceClient blogServiceClient;
    @Value("${social.login.profile}")
    private  String socialLoginProfile;
    private final RedisRepository redisRepository;

    public UserStatisticResponse findUserStatisticsAndUpdateRankByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        Long newUserRank = redisRepository.getUserRank(userId);
        userRepository.save(user.toBuilder().userRank(newUserRank).build());

        return UserStatisticResponse.builder()
                .userId(user.getUserId())
                .nickName(user.getNickName())
                .totalScore(user.getTotalScore())
                .registerCount(user.getRegisterCount())
                .solvedCount(user.getSolvedCount())
                .rank(newUserRank)
                .profileUrl(user.getProfileUrl())
                .tier(user.getTier())
                .build();
    }

    public UserInfoResponse findUserInfoByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return UserInfoResponse.builder()
                .userId(user.getUserId())
                .nickName(user.getNickName())
                .profileUrl(user.getProfileUrl())
                .profileMessage(user.getProfileMessage())
                .codeLanguage(user.getCodeLanguage())
                .tier(user.getTier())
                .build();
    }

    @Transactional
    public UserInfoResponse updateUserInfoByUserId(UserInfoRequest userInfoRequest, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        String dbUrl = user.getProfileUrl();
        String inputUrl = userInfoRequest.getProfileUrl();
        log.info("기존 이미지 ={}", dbUrl);
        log.info("새로 들어온 이미지={}", inputUrl);

        // 정규표현식: " /profileImg1.png" ~ " /profileImg6.png"와 일치하는지 확인
        String defaultProfileRegex = "/profileImg[1-6]\\.png";
        String saveUrl;
        if (dbUrl.matches(defaultProfileRegex) && inputUrl.matches(defaultProfileRegex)) {
            log.info("기본 -> 기본");
            saveUrl = minioFileUploadService.handleDefaultToDefault(dbUrl, inputUrl);
        } else if (dbUrl.matches(defaultProfileRegex)) {
            log.info("기본 -> 임시");
            saveUrl = minioFileUploadService.handleDefaultToTemp(inputUrl);
        } else if (inputUrl.matches(defaultProfileRegex)) {
            log.info("영구 -> 기본");
            saveUrl = minioFileUploadService.handlePermanentToDefault(inputUrl, dbUrl);
        }else{
            log.info("영구 -> 임시");
            saveUrl = minioFileUploadService.handlePermanentToTemp(inputUrl, dbUrl);
        }
        User updateUser = userRepository.save(user.toBuilder()
                .nickName(userInfoRequest.getNickName())
                .profileUrl(saveUrl)
                .profileMessage(userInfoRequest.getProfileMessage())
                .codeLanguage(userInfoRequest.getCodeLanguage())
                .build());
        return UserInfoResponse.builder()
                .userId(updateUser.getUserId())
                .nickName(updateUser.getNickName())
                .profileUrl(saveUrl)
                .profileMessage(updateUser.getProfileMessage())
                .codeLanguage(updateUser.getCodeLanguage())
                .build();
    }

    public UserInfoForBlogResponse findUserInfoForBlogService(String userId) {
        UserInfoForBlogResponse userInfo = userRepository.findUserInfoForBlogByUserId(userId);
        if (userInfo == null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }
        return userInfo;
    }

    public Page<AnnounceResponse> getAnnouncementsFromAdminService(int page, int size) {
        ResponseEntity<CustomPageImpl<AnnounceResponse>> response = adminServiceClient.getAnnouncements(page, size);

        // 상태 코드가 200 OK인 경우
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            //관리자 서비스로부터 어떤 에러가 전달되어도 하나의 에러로 전달한다.
            throw new BusinessException("공지사항을 조회하지 못했습니다.", ErrorCode.ANNOUNCEMENT_NOT_FOUNT);
        }
    }

    public AnnounceDetailResponse getAnnouncementDetailsFromAdminService(long announceId) {
        ResponseEntity<AnnounceDetailResponse> response = adminServiceClient.getAnnouncementDetails(announceId);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new BusinessException("공지사항을 조회하지 못했습니다.", ErrorCode.ANNOUNCEMENT_NOT_FOUNT);
        }
    }

    @Transactional
    public void calculateTierAndUpdateScore(UserScoreRequest userScoreRequest) {
        User user = userRepository.findById(userScoreRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        int updatedScore = user.getTotalScore() + userScoreRequest.getScore();
        Tier newTier = Tier.fromScore(updatedScore);

        //redis 에서 사용자 점수 업데이트 후 전체 순위를 다시 계산
        redisRepository.updateScore(userScoreRequest.getUserId(), updatedScore);
        long userRank = redisRepository.getUserRank(userScoreRequest.getUserId());

        userRepository.save(user.toBuilder()
                .totalScore(updatedScore)
                .userRank(userRank)
                .tier(newTier)
                .build());
    }

    @Transactional
    public UserDeletionResponse deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        if (socialLoginProfile.equals("dev")) {
            try {
                blogServiceClient.deleteBlog(userId);
            } catch (FeignException e) {
                throw new CreationException("블로그 생성 요청 중 예외 발생: " + e.getMessage());
            }
        }

        userRepository.delete(user);
        return new UserDeletionResponse(user.getUserId(), user.getUserName());
    }
}
