package org.userservice.userservice.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.dto.auth.OAuth2UserDetails;
import org.userservice.userservice.dto.SecurityExceptionDto;
import org.userservice.userservice.dto.UserDto;

import java.io.IOException;
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1. 토큰 추출
        String accessToken = jwtProvider.resolveTokenHeader(request);

        //2. 토큰 검증
        if (accessToken != null) {
            if (!jwtProvider.validateToken(accessToken)) {
                jwtExceptionHandler(response, "AccessToken이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED.value());
                return;
            }
            //3. 검증 성공시 인증 객체 생성
            Claims claims = jwtProvider.getUserInfoFromToken(accessToken);
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            OAuth2UserDetails oAuth2UserDetails = new OAuth2UserDetails(
                    UserDto.builder()
                            .providerName(userId)
                            .role(AuthRole.fromString(role))
                            .build());

            Authentication authToken = new UsernamePasswordAuthenticationToken(oAuth2UserDetails, null, oAuth2UserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String message, int statusCode) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // ObjectMapper를 사용하여 SecurityExceptionDto 객체를 json 문자열로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(new SecurityExceptionDto(message, statusCode));
            response.getWriter().write(json); //json 문자열을 응답으로 작성
        } catch (IOException e) {
            throw new RuntimeException("Error while processing JSON", e);
        }
    }
}