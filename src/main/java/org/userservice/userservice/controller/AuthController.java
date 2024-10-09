package org.userservice.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.userservice.userservice.dto.OAuth2UserDetails;
import org.userservice.userservice.dto.SignupRequest;
import org.userservice.userservice.jwt.JwtUtil;
import org.userservice.userservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @GetMapping("/convert")
    public ResponseEntity<?> cookieToHeader(
            @CookieValue(name = "Authorization", required = false) String token
            , HttpServletResponse response) {
        // 쿠키에서 JWT 토큰을 추출
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization cookie not found");
        }
        //TODO: 쿠키에 대한 검증




        // 응답 헤더에 토큰을 추가
        response.addHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok("Bearer " + token);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody SignupRequest signupRequest,
            Authentication authentication) {

        OAuth2UserDetails oAuth2UserDetails = (OAuth2UserDetails) authentication.getPrincipal();
        String providerName = oAuth2UserDetails.getProviderName();
        userService.updateUserRole(providerName, "ROLE_USER_B");
        String newJwt = jwtUtil.createToken(providerName, "ROLE_USER_B");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + newJwt);

        return ResponseEntity.ok().headers(headers).body("Signup completed and new JWT issued");
    }
}
