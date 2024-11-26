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
import org.userservice.userservice.error.ErrorCode;
import org.userservice.userservice.error.exception.CreationException;
import org.userservice.userservice.jwt.JwtToken;
import org.userservice.userservice.service.AuthService;
import org.userservice.userservice.service.UserService;
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
        Claims claims = authService.validateAndExtractClaims(accessToken, AuthRole.ROLE_USER_B);
        String bearerAccessToken = authService.createBearerToken(claims.getSubject(), "access", AuthRole.ROLE_USER_B, 1000 * 60 * 10L); //10분
        String bearerRefreshToken = authService.createBearerToken(claims.getSubject(), "refresh", AuthRole.ROLE_USER_B, 1000 * 60 * 60 * 24L); //24시간

        response.addCookie(CookieUtils.createCookie("Authorization", null, 0)); //access 만료
        response.addHeader("Authorization", bearerAccessToken);
        response.addHeader("Refresh", bearerRefreshToken);
        return ResponseEntity.ok(new JwtToken(bearerAccessToken, bearerRefreshToken));
    }

    @Override
    @PostMapping("/signup/test")
    public ResponseEntity<?> signupTest(
            @Validated @RequestBody SignupRequest signupRequest,
            @CookieValue(name = "Authorization", required = false) String accessToken,
            HttpServletResponse response) {

        Claims claims = authService.validateAndExtractClaims(accessToken, AuthRole.ROLE_USER_A);
        String userId = claims.getSubject();
        //TODO: prod provisioning 시에는 주석 제거
//        // 블로그 생성 요청 및 응답 확인
//        if (!blogServiceClient.createBlog(userId).getStatusCode().is2xxSuccessful()) {
//            throw new CreationException("블로그 생성에 실패했습니다.");
//        }

        AuthRole newRole = authService.signup(signupRequest, userId);
        String bearerAccessToken = authService.createBearerToken(claims.getSubject(), "access", newRole, 1000 * 60 * 10L);
        String bearerRefreshToken = authService.createBearerToken(claims.getSubject(), "refresh", newRole, 1000 * 60 * 60 * 24L);

        response.addCookie(CookieUtils.createCookie("Authorization", null, 0));
        response.addHeader("Authorization", bearerAccessToken);
        response.addHeader("Refresh", bearerRefreshToken);
        return ResponseEntity.ok(new SignupResponse(userId, newRole, new JwtToken(bearerAccessToken, bearerRefreshToken)));
    }


    @Override
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Validated @RequestBody SignupRequest signupRequest,
            @RequestHeader(name = "Authorization", required = false) String token,
            HttpServletResponse response) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거 (공백 포함 7글자)
        }

        Claims claims = authService.validateAndExtractClaims(token, AuthRole.ROLE_USER_A);
        String userId = claims.getSubject();

        try {
            blogServiceClient.createBlog(userId);
        } catch (FeignException e) {
            throw new CreationException("블로그 생성 요청 중 예외 발생: " + e.getMessage());
        }

        AuthRole newRole = authService.signup(signupRequest, userId);
        String bearerToken = authService.createBearerToken(userId, newRole);

        response.addHeader("Authorization", bearerToken);
        return ResponseEntity.ok(new SignupResponse(userId, newRole, new JwtToken(bearerToken, null)));
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(
            @RequestHeader(name = "Refresh", required = false) String refreshToken,
            HttpServletResponse response) {

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        //response
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
