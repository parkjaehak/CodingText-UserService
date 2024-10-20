package org.userservice.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.dto.auth.SignupRequest;
import org.userservice.userservice.dto.user.UserInfoRequest;
import org.userservice.userservice.dto.user.UserInfoResponse;
import org.userservice.userservice.dto.user.UserStatisticResponse;
import org.userservice.userservice.error.exception.UserNotFoundException;
import org.userservice.userservice.repository.UserRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    public AuthRole signup(SignupRequest signupRequest, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with provider: " + userId));

        User updateUser = null;
        if (signupRequest.getUseSocialProfile()) {
            //소셜계정의 프로필을 사용
            updateUser = user.toBuilder()
                    .nickName(signupRequest.getNickname())
                    .codeLanguage(signupRequest.getCodeLanguage())
                    .role(AuthRole.ROLE_USER_B)
                    .build();
        } else {
            updateUser = user.toBuilder()
                    .nickName(signupRequest.getNickname())
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

    public UserInfoResponse updateUserInfoByUserId(UserInfoRequest userInfoRequest, String userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with userId: " + userId));

        MultipartFile file = userInfoRequest.getFile();
        String profileUrl = null;
        if (file != null) {
            //TODO: file이 없다면 default 사진이다.
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
}
