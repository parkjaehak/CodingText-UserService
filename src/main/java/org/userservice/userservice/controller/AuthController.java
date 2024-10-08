package org.userservice.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/convert-cookie-to-header")
    public ResponseEntity<?> cookieToHeader(@CookieValue(name = "Authorization", required = false) String token) {
        //최초 토큰 검증절차 진행한다.

        return ResponseEntity.ok("ok");
    }
}
