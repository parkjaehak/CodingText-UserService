package org.userservice.userservice.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.dto.SignupRequest;
import org.userservice.userservice.dto.SignupResponse;
import org.userservice.userservice.error.exception.UnauthenticatedException;
import org.userservice.userservice.error.exception.UnauthorizedException;
import org.userservice.userservice.jwt.JwtFilter;
import org.userservice.userservice.jwt.JwtToken;
import org.userservice.userservice.jwt.JwtUtil;
import org.userservice.userservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @GetMapping("/cookie-to-header")
    public ResponseEntity<?> cookieToHeader(@CookieValue(name = "Authorization", required = false) String token, HttpServletResponse response) {
        if (token == null) {
            throw new UnauthenticatedException("토큰 존재하지 않습니다.");
        }
        if (!jwtUtil.validateToken(token)) {
            throw new UnauthenticatedException("토큰 유효하지 않습니다.");
        }
        Claims claims = jwtUtil.getUserInfoFromToken(token);
        String role = claims.get("role", String.class);
        if (!role.equals(String.valueOf(AuthRole.ROLE_USER_B))) {
           throw new UnauthorizedException("권한이 없는 사용자");
        }
        response.addHeader("Authorization", "Bearer " + token); //응답헤더에 토큰 추가
        return ResponseEntity.ok("Bearer " + token);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest, @CookieValue(name = "Authorization", required = false) String token, HttpServletResponse response) {
        if (token == null) {
            throw new UnauthenticatedException("토큰 존재하지 않습니다.");
        }
        if (!jwtUtil.validateToken(token)) {
            throw new UnauthenticatedException("토큰 유효하지 않습니다.");
        }
        Claims claims = jwtUtil.getUserInfoFromToken(token);
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);
        if (!role.equals(String.valueOf(AuthRole.ROLE_USER_A))) {
            throw new UnauthorizedException("권한이 없는 사용자");
        }
        AuthRole newRole = userService.signup(signupRequest, userId);
        String bearerToken = "Bearer " + jwtUtil.createToken(userId, String.valueOf(newRole));
        //TODO: refresh token 발급 로직 추가
        response.addHeader("Authorization", bearerToken);
        return ResponseEntity.ok(new SignupResponse(userId, newRole,  new JwtToken(bearerToken, null)));
    }
}
