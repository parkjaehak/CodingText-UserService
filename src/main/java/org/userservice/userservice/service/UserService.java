package org.userservice.userservice.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.config.CustomPageImpl;
import org.userservice.userservice.controller.feignclient.AdminServiceClient;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.dto.adminclient.AnnounceDetailResponse;
import org.userservice.userservice.dto.adminclient.AnnounceResponse;
import org.userservice.userservice.dto.auth.SignupRequest;
import org.userservice.userservice.dto.user.UserInfoForBlogResponse;
import org.userservice.userservice.dto.user.UserInfoRequest;
import org.userservice.userservice.dto.user.UserInfoResponse;
import org.userservice.userservice.dto.user.UserStatisticResponse;
import org.userservice.userservice.error.ErrorCode;
import org.userservice.userservice.error.exception.BusinessException;
import org.userservice.userservice.error.exception.UserNotFoundException;
import org.userservice.userservice.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final AdminServiceClient adminServiceClient;

    public UserService(UserRepository userRepository, @Qualifier("minio") FileUploadService fileUploadService, AdminServiceClient adminServiceClient) {
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
        this.adminServiceClient = adminServiceClient;
    }

    public AuthRole signup(SignupRequest signupRequest, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with provider: " + userId));

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
                    .profileUrl(null)      //TODO: default 프로필 사진은 어떻게 가져올 것인지 확인
                    .build();
        }
        userRepository.save(updateUser);
        return updateUser.getRole();
    }

    public UserStatisticResponse findUserStatisticsByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with userId: " + userId));

        return UserStatisticResponse.builder()
                .userId(user.getUserId())
                .nickName(user.getNickName())
                .totalScore(user.getTotalScore())
                .registerCount(user.getRegisterCount())
                .solvedCount(user.getSolvedCount())
                .rank(user.getUserRank())
                .build();
    }

    public UserInfoResponse findUserInfoByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with userId: " + userId));

        return UserInfoResponse.builder()
                .userId(user.getUserId())
                .nickName(user.getNickName())
                .profileUrl(user.getProfileUrl())
                .profileMessage(user.getProfileMessage())
                .codeLanguage(user.getCodeLanguage())
                .build();
    }

    public UserInfoResponse updateUserInfoByUserId(UserInfoRequest userInfoRequest, MultipartFile file, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with userId: " + userId));

        String profileUrl = null;
        if (file != null) {
            //TODO: default 사진일 경우 null
            //이미지 url을 입력 받아 minio에 있으면 중복저장하지 않음
            // 기존 이미지가 그대로라면 중복저장을 방지하기 위해 변경여부를 받아야 할 것
            profileUrl = fileUploadService.saveImageFile(file);
        }

        User updateUser = userRepository.save(user.toBuilder()
                .nickName(userInfoRequest.getNickName())
                .profileUrl(profileUrl)
                .profileMessage(userInfoRequest.getProfileMessage())
                .codeLanguage(userInfoRequest.getCodeLanguage())
                .build());

        return UserInfoResponse.builder()
                .userId(updateUser.getUserId())
                .nickName(updateUser.getNickName())
                .profileUrl(updateUser.getProfileUrl())
                .profileMessage(updateUser.getProfileMessage())
                .codeLanguage(updateUser.getCodeLanguage())
                .build();
    }

    public UserInfoForBlogResponse findUserInfoForBlogService(String userId) {
        UserInfoForBlogResponse userInfo = userRepository.findUserInfoForBlogByUserId(userId);
        if (userInfo == null) {
            throw new UserNotFoundException("User with userId " + userId + " not found");
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
}
