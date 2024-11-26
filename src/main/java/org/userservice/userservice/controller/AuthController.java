package org.userservice.userservice.controller;

import feign.FeignException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.userservice.userservice.controller.feignclient.BlogServiceClient;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.dto.auth.SignupRequest;
import org.userservice.userservice.dto.auth.SignupResponse;
import org.userservice.userservice.error.exception.CreationException;
import org.userservice.userservice.jwt.JwtToken;
import org.userservice.userservice.service.AuthService;
import org.userservice.userservice.utils.CookieUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final BlogServiceClient blogServiceClient;

    @Override
    @GetMapping("/cookie-to-header")
    public ResponseEntity<?> cookieToHeader(
            @CookieValue(name = "Authorization", required = false) String accessToken,
            HttpServletResponse response) {

        Claims claims = extractClaims(accessToken, AuthRole.ROLE_USER_B, "access");
        invalidateAuthorizationCookie(response);
        addTokensToResponse(response, claims.getSubject(), AuthRole.ROLE_USER_B);
        return ResponseEntity.ok(new JwtToken(response.getHeader("Authorization"), response.getHeader("Refresh")));
    }

    @Override
    @PostMapping("/signup/test")
    public ResponseEntity<?> signupTest(
            @Validated @RequestBody SignupRequest signupRequest,
            @CookieValue(name = "Authorization", required = false) String accessToken,
            HttpServletResponse response) {

        Claims claims = extractClaims(accessToken, AuthRole.ROLE_USER_A, "access");
        String userId = claims.getSubject();
        AuthRole newRole = authService.signup(signupRequest, userId);
        invalidateAuthorizationCookie(response);
        addTokensToResponse(response, userId, newRole);
        return ResponseEntity.ok(new SignupResponse(userId, newRole, new JwtToken(response.getHeader("Authorization"), response.getHeader("Refresh"))));
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Validated @RequestBody SignupRequest signupRequest,
            @RequestHeader(name = "Authorization", required = false) String accessToken,
            HttpServletResponse response) {

        Claims claims = extractClaims(accessToken, AuthRole.ROLE_USER_A, "access");
        String userId = claims.getSubject();
        try {
            blogServiceClient.createBlog(userId);
        } catch (FeignException e) {
            throw new CreationException("블로그 생성 요청 중 예외 발생: " + e.getMessage());
        }
        AuthRole newRole = authService.signup(signupRequest, userId);
        invalidateAuthorizationCookie(response);
        addTokensToResponse(response, userId, newRole);
        return ResponseEntity.ok(new SignupResponse(userId, newRole, new JwtToken(response.getHeader("Authorization"), response.getHeader("Refresh"))));
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(
            @RequestHeader(name = "Refresh", required = false) String refreshToken,
            HttpServletResponse response) {

        Claims claims = extractClaims(refreshToken, AuthRole.ROLE_USER_B, "refresh");
        addTokensToResponse(response, claims.getSubject(), AuthRole.ROLE_USER_B);
        return ResponseEntity.ok(new JwtToken(response.getHeader("Authorization"), response.getHeader("Refresh")));
    }






    private void addTokensToResponse(HttpServletResponse response, String userId, AuthRole role) {
        String bearerAccessToken = authService.createBearerToken(userId, "access", role, 1000 * 60 * 10L); // 10분
        String bearerRefreshToken = authService.createBearerToken(userId, "refresh", role, 1000 * 60 * 60 * 24L); // 24시간
        response.addHeader("Authorization", bearerAccessToken);
        response.addHeader("Refresh", bearerRefreshToken);
    }
    private void invalidateAuthorizationCookie(HttpServletResponse response) {
        response.addCookie(CookieUtils.createCookie("Authorization", null, 0)); // Authorization 쿠키 만료
    }
    private Claims extractClaims(String token, AuthRole requiredRole, String tokenType) {
        return authService.validateAndExtractClaims(token, requiredRole, tokenType);
    }
}
