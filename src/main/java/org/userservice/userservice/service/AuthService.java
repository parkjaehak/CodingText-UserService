package org.userservice.userservice.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.dto.auth.SignupRequest;
import org.userservice.userservice.error.exception.UnauthenticatedException;
import org.userservice.userservice.error.exception.UnauthorizedException;
import org.userservice.userservice.error.exception.UserNotFoundException;
import org.userservice.userservice.jwt.JwtProvider;
import org.userservice.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public Claims validateAndExtractClaims(String token, AuthRole requiredRole) {
        if (token == null) {
            throw new UnauthenticatedException("토큰 존재하지 않습니다.");
        }
        if (!jwtProvider.validateToken(token)) {
            throw new UnauthenticatedException("토큰 유효하지 않습니다.");
        }
        Claims claims = jwtProvider.getUserInfoFromToken(token);
        String role = claims.get("role", String.class);
        if (!role.equals(String.valueOf(requiredRole))) {
            throw new UnauthorizedException("권한이 없는 사용자");
        }
        return claims;
    }

    public String createBearerToken(String userId, String type, AuthRole role, Long expireMs) {
        return "Bearer " + jwtProvider.createToken(userId, type, String.valueOf(role), expireMs);
    }

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
}
