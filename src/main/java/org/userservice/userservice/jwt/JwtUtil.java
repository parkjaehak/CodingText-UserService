package org.userservice.userservice.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 클래스:
 * JWT 인증 및 인가 작업을 위한 메서드 모음
 * <p>
 * 설명:
 * Authentication: 소셜 로그인 성공시 SuccessHandler 에서 JWT 를 생성해 프론트로 전달
 * Authorization: permit 이 필요한 엔드포인트에 대해 JWT 검증하여 성공시 인증 객체 및 세션 생성
 * <p>
 * secret: JWT 생성을 위한 비밀키 생성에 사용되는 베이스 문자열
 * secretKey:
 * secret 문자열을 이용해 HS256 알고리즘에 맞게 생성된 객체로 JWT 를 해당알고리즘에 맞게 암호화하여 생성
 * HS256(HMAC-SHA256, 해시알고리즘)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24L; //액세스토큰 유효시간: 24 시간

    @Value("${spring.jwt.secret}")
    private String secret;
    private SecretKey secretKey;


    @PostConstruct
    public void init() {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    //Authentication: JWT 생성
    public String createToken(String providerName, String role) {
        //TODO: 쿠키 값에는 blank 삽입 불가
        return BEARER_PREFIX + Jwts.builder()
                .subject(providerName)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();
    }

    //Authorization: JWT 검증
    public String resolveTokenHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String resolveTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization")) {
                String bearerToken = cookie.getValue();
                if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
                    return bearerToken.substring(7);
                }
            }
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;

        } catch (SignatureException e) {
            log.error("Invalid JWT signature, signature 가 유효하지 않은 토큰 입니다.");
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            log.error("Invalid JWT token, 유효하지 않은 jwt 토큰 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token. 만료된 jwt 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    public Claims getUserInfoFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}