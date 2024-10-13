package org.userservice.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.dto.SignupRequest;
import org.userservice.userservice.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
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
}
