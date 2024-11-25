package org.userservice.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.domain.Tier;
import org.userservice.userservice.dto.adminclient.CustomPageImpl;
import org.userservice.userservice.controller.feignclient.AdminServiceClient;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.dto.adminclient.AnnounceDetailResponse;
import org.userservice.userservice.dto.adminclient.AnnounceResponse;
import org.userservice.userservice.dto.auth.SignupRequest;
import org.userservice.userservice.dto.codebankclient.UserScoreRequest;
import org.userservice.userservice.dto.user.UserInfoForBlogResponse;
import org.userservice.userservice.dto.user.UserInfoRequest;
import org.userservice.userservice.dto.user.UserInfoResponse;
import org.userservice.userservice.dto.user.UserStatisticResponse;
import org.userservice.userservice.dto.user.*;
import org.userservice.userservice.error.ErrorCode;
import org.userservice.userservice.error.exception.BusinessException;
import org.userservice.userservice.error.exception.UserNotFoundException;
import org.userservice.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final MinioFileUploadService minioFileUploadService;
    private final AdminServiceClient adminServiceClient;


    @Transactional
    public AuthRole signup(SignupRequest signupRequest, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        User updateUser = null;
        if (signupRequest.getUseSocialProfile()) {
            //소셜계정의 프로필을 사용
            updateUser = user.toBuilder()
                    .nickName(signupRequest.getNickName())
                    .codeLanguage(signupRequest.getCodeLanguage())
                    .role(AuthRole.ROLE_USER_B)
                    .build();
        } else {
            updateUser = user.toBuilder()
                    .nickName(signupRequest.getNickName())
                    .codeLanguage(signupRequest.getCodeLanguage())
                    .role(AuthRole.ROLE_USER_B)
                    .profileUrl(signupRequest.getBasicProfileUrl())
                    .build();
        }
        userRepository.save(updateUser);
        return updateUser.getRole();
    }

    public UserStatisticResponse findUserStatisticsByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return UserStatisticResponse.builder()
                .userId(user.getUserId())
                .nickName(user.getNickName())
                .totalScore(user.getTotalScore())
                .registerCount(user.getRegisterCount())
                .solvedCount(user.getSolvedCount())
                .rank(user.getUserRank())
                .profileUrl(user.getProfileUrl())
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
                .build();
    }

    @Transactional
    public UserInfoResponse updateUserInfoByUserId(UserInfoRequest userInfoRequest, String userId) {
        // 사용자 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        String saveUrl;

        // URL이 변경되지 않았을 경우 기존 URL을 그대로 사용
        String prevUrl = user.getProfileUrl();
        String inputUrl = userInfoRequest.getProfileUrl();

        if (prevUrl.equals(inputUrl)) {
            saveUrl = prevUrl; // 기존 데이터베이스 URL 사용
        } else {
            // URL이 변경되었을 경우
            String updatedUrl = minioFileUploadService.updateProfileImageByUrl(inputUrl, prevUrl);

            // 만약 URL이 null이면 기본 URL 사용
            if (updatedUrl != null) {
                saveUrl = updatedUrl; // 저장된 영구 스토리지 URL
            } else {
                saveUrl = inputUrl; // 기본 URL
            }
        }

        // 사용자 정보 업데이트
        User updateUser = userRepository.save(user.toBuilder()
                .nickName(userInfoRequest.getNickName())
                .profileUrl(saveUrl)
                .profileMessage(userInfoRequest.getProfileMessage())
                .codeLanguage(userInfoRequest.getCodeLanguage())
                .build());

        // 응답 반환
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
    public void calculateUserTier(UserScoreRequest userScoreRequest) {
        User user = userRepository.findById(userScoreRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        int updatedScore = user.getTotalScore() + userScoreRequest.getScore();
        Tier newTier = Tier.fromScore(updatedScore);

        userRepository.save(user.toBuilder()
                .totalScore(updatedScore)
                .tier(newTier)
                .build());
    }

    @Transactional
    public UserDeletionResponse deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        //TODO: userId와 연관된 blog, coding 정보 삭제 요청
        userRepository.delete(user);
        return new UserDeletionResponse(user.getUserId(), user.getUserName());
    }
}
