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
import org.userservice.userservice.jwt.JwtFilter;
import org.userservice.userservice.jwt.JwtToken;
import org.userservice.userservice.jwt.JwtUtil;
import org.userservice.userservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final JwtFilter jwtFilter;
    private final UserService userService;

    @GetMapping("/cookie-to-header")
    public ResponseEntity<?> cookieToHeader(
            @CookieValue(name = "Authorization", required = false) String token
            , HttpServletResponse response) {
        // 쿠키에서 JWT 토큰을 추출
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization cookie not found");
        }
        if (!jwtUtil.validateToken(token)) {
            jwtFilter.jwtExceptionHandler(response, "AccessToken이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED.value());
        }
        //TODO: role b인지 여부 검증

        // 응답 헤더에 토큰을 추가
        response.addHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok("Bearer " + token);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody SignupRequest signupRequest,
            @CookieValue(name = "Authorization", required = false) String token,
            HttpServletResponse response) {

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization cookie not found");
        }
        if (!jwtUtil.validateToken(token)) {
            jwtFilter.jwtExceptionHandler(response, "AccessToken이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED.value());
        }
        Claims claims = jwtUtil.getUserInfoFromToken(token);
        String userId = claims.getSubject();
        //TODO: role a 여부 검증

        AuthRole role = userService.signup(signupRequest, userId);
        String bearerToken = "Bearer " + jwtUtil.createToken(userId, String.valueOf(role));
        response.addHeader("Authorization", bearerToken);

        return ResponseEntity.ok(new SignupResponse(userId, role,  new JwtToken(bearerToken, null)));
    }
}
