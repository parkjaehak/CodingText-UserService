package org.userservice.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.dto.auth.OAuth2UserDetails;
import org.userservice.userservice.dto.SignupRequest;
import org.userservice.userservice.jwt.JwtUtil;
import org.userservice.userservice.service.AuthService;
import org.userservice.userservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/cookie-to-header")
    public ResponseEntity<?> cookieToHeader(
            @CookieValue(name = "Authorization", required = false) String token
            , HttpServletResponse response) {
        // 쿠키에서 JWT 토큰을 추출
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization cookie not found");
        }
        //TODO: 쿠키러 잔딜된 토큰 검증하여 헤더로 전달




        // 응답 헤더에 토큰을 추가
        response.addHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok("Bearer " + token);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody SignupRequest signupRequest,
            @RequestPart("file") MultipartFile multipartFile,
            @CookieValue(name = "Authorization", required = false) String token,
            HttpServletResponse response) {

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization cookie not found");
        }
        authService.checkCookieTokenValid(token);

//
//        OAuth2UserDetails oAuth2UserDetails = (OAuth2UserDetails) authentication.getPrincipal();
//        String providerName = oAuth2UserDetails.getProviderName();
//        userService.updateUserRole(providerName, "ROLE_USER_B");
//        String newJwt = jwtUtil.createToken(providerName, "ROLE_USER_B");

        response.addHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok("Bearer " + token);
    }
}
