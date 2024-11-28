package org.userservice.userservice.controller;

import feign.FeignException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.userservice.userservice.controller.feignclient.BlogServiceClient;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.dto.auth.SignupRequest;
import org.userservice.userservice.dto.auth.SignupResponse;
import org.userservice.userservice.error.ErrorCode;
import org.userservice.userservice.error.exception.BlogCreationException;
import org.userservice.userservice.error.exception.RefreshTokenDoesNotMatchException;
import org.userservice.userservice.jwt.JwtToken;
import org.userservice.userservice.service.AuthService;
import org.userservice.userservice.utils.CookieUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
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
    @PostMapping("/prod/signup")
    public ResponseEntity<?> signupProd(
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
            throw new BlogCreationException("블로그 생성 요청 중 예외 발생: " + e.getMessage());
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
        log.info("현재 refresh token: {}", refreshToken);
        String storedRefreshToken = authService.getRefreshToken(claims.getSubject());
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            log.info("여기서 토큰이 불일치하는가?");
            log.info("여기서 refreshToken={}", refreshToken);
            log.info("여기서 storedRefreshToken={}",storedRefreshToken);
            throw new RefreshTokenDoesNotMatchException(ErrorCode.REFRESH_TOKEN_NOT_FOUND,"Refresh Token 이 일치하지 않습니다.");
        }
        addTokensToResponse(response, claims.getSubject(), AuthRole.ROLE_USER_B);
        return ResponseEntity.ok(new JwtToken(response.getHeader("Authorization"), response.getHeader("Refresh")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(name = "UserId") String userId,
            HttpServletResponse response) {

        authService.deleteRefreshToken(userId);
        response.addHeader("Authorization", null);
        response.addHeader("Refresh", null);
        return ResponseEntity.ok(new JwtToken(null,null));
    }




    private void addTokensToResponse(HttpServletResponse response, String userId, AuthRole role) {
        String bearerAccessToken = authService.createBearerToken(userId, "access", role, 1000 * 60 * 10L); // 10분
        String bearerRefreshToken = authService.createBearerToken(userId, "refresh", role, 1000 * 60 * 60 * 24L); // 24시간

        authService.saveRefreshToken(userId, bearerRefreshToken);

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
