package org.userservice.userservice.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.domain.RefreshToken;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.dto.auth.SignupRequest;
import org.userservice.userservice.error.exception.*;
import org.userservice.userservice.jwt.JwtProvider;
import org.userservice.userservice.repository.RedisRepository;
import org.userservice.userservice.repository.RefreshTokenRepository;
import org.userservice.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisRepository redisRepository;

    public Claims validateAndExtractClaims(String token, AuthRole requiredRole, String tokenType) {
        if (token == null) {
            throw new UnauthenticatedException("토큰 존재하지 않습니다.");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거 (공백 포함 7글자)
        }
        if (!jwtProvider.validateToken(token)) {
            throw new UnauthenticatedException("토큰 유효하지 않습니다.");
        }
        Claims claims = jwtProvider.getUserInfoFromToken(token);
        String role = claims.get("role", String.class);
        String type = claims.get("type", String.class);
        if (!role.equals(String.valueOf(requiredRole))) {
            throw new UnauthorizedException("권한이 없는 사용자");
        }
        if (!type.equals(tokenType)) {
            throw new TokenTypeMismatchException("토큰 타입이 맞지 않음");
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

        redisRepository.updateScore(userId, user.getTotalScore(), user.getSolvedCount());
        long userRank = redisRepository.getUserRank(userId);

        User updateUser = null;
        if (signupRequest.getUseSocialProfile()) {
            //소셜계정의 프로필을 사용
            updateUser = user.toBuilder()
                    .nickName(signupRequest.getNickName())
                    .codeLanguage(signupRequest.getCodeLanguage())
                    .role(AuthRole.ROLE_USER_B)
                    .userRank(userRank)
                    .build();
        } else {
            updateUser = user.toBuilder()
                    .nickName(signupRequest.getNickName())
                    .codeLanguage(signupRequest.getCodeLanguage())
                    .role(AuthRole.ROLE_USER_B)
                    .profileUrl(signupRequest.getBasicProfileUrl())
                    .userRank(userRank)
                    .build();
        }
        userRepository.save(updateUser);
        return updateUser.getRole();
    }

    // RefreshToken 저장
    @Transactional
    public void saveRefreshToken(String userId, String refreshToken) {
        RefreshToken token = new RefreshToken(userId, refreshToken);
        refreshTokenRepository.save(token);
    }

    // RefreshToken 조회
    public String getRefreshToken(String userId) {
        RefreshToken token = refreshTokenRepository.findById(userId).orElse(null);
        return token != null ? token.getRefreshToken() : null;
    }
    @Transactional
    // RefreshToken 삭제 (로그아웃)
    public void deleteRefreshToken(String userId) {
        refreshTokenRepository.deleteById(userId);
    }
}
