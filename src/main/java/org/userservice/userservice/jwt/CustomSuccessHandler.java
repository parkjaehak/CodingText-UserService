package org.userservice.userservice.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.dto.auth.OAuth2UserDetails;
import org.userservice.userservice.utils.CookieUtils;

import java.io.IOException;

/**
 * 클래스 요약:
 * OAuth2 인증 성공시 JWT 발급하는 핸들러
 *
 * 설명:
 * customSuccessHandler 에서는 SecurityContext 에 저장된 인증 객체를 가져와 추가 작업(JWT 생성)을 수행가능
 * 여기서는 SecurityContext 에서 직접 가져오는 것이 아니라, onAuthenticationSuccess 메서드의 매개변수로 전달된 Authentication 객체에서 바로 가져옴
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Value("${social.login.profile}")
    private  String socialLoginProfile;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        //OAuth2User (인증정보)
        String providerName = ((OAuth2UserDetails) authentication.getPrincipal()).getProviderName();
        String role = authentication.getAuthorities().stream().findFirst().get().getAuthority();

        //토큰 발급
        String accessToken = jwtProvider.createToken(providerName, "access", role, 1000 * 60 * 10L); //10분
        String refreshToken = jwtProvider.createToken(providerName, "refresh", role,1000 * 60 * 60 * 24L); //24시간

        if (socialLoginProfile.equals("dev")) {
            if (role.equals(String.valueOf(AuthRole.ROLE_USER_A))) {
                response.sendRedirect("http://localhost:3000/auth?access=" + accessToken + "&signedIn=false");
            } else {
                response.sendRedirect("http://localhost:3000/auth?access=Bearer " + accessToken + "$refresh=Bearer "+ refreshToken + "&signedIn=true");
            }

        } else if (socialLoginProfile.equals("local")) {
            response.addCookie(CookieUtils.createCookie("Authorization", accessToken, 60*60*24)); //24시간
            if (role.equals(String.valueOf(AuthRole.ROLE_USER_A))) {
                response.sendRedirect("http://localhost:8080/auth/signup/test");
            } else {
                response.sendRedirect("http://localhost:8080/auth/cookie-to-header");
            }
        }
    }
}
