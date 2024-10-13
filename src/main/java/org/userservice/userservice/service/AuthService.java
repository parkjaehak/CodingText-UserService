package org.userservice.userservice.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.error.exception.UnauthenticatedException;
import org.userservice.userservice.error.exception.UnauthorizedException;
import org.userservice.userservice.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;

    public Claims validateAndExtractClaims(String token, AuthRole requiredRole) {
        if (token == null) {
            throw new UnauthenticatedException("토큰 존재하지 않습니다.");
        }
        if (!jwtUtil.validateToken(token)) {
            throw new UnauthenticatedException("토큰 유효하지 않습니다.");
        }
        Claims claims = jwtUtil.getUserInfoFromToken(token);
        String role = claims.get("role", String.class);
        if (!role.equals(String.valueOf(requiredRole))) {
            throw new UnauthorizedException("권한이 없는 사용자");
        }
        return claims;
    }

    public String createBearerToken(String userId, AuthRole role) {
        return "Bearer " + jwtUtil.createToken(userId, String.valueOf(role));
    }
}
