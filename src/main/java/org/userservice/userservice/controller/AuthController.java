package org.userservice.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/convert")
    public ResponseEntity<?> cookieToHeader(
            @CookieValue(name = "Authorization", required = false) String token
            ,HttpServletResponse response) {
        // 쿠키에서 JWT 토큰을 추출
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization cookie not found");
        }
        // 응답 헤더에 토큰을 추가
        response.addHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok("Bearer " + token);
    }
}
