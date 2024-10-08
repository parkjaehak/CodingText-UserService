package org.userservice.userservice.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.userservice.userservice.dto.OAuth2UserDetails;
import org.userservice.userservice.dto.SecurityExceptionDto;
import org.userservice.userservice.dto.UserDto;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //TODO: 최초 토큰은 pass 시켜서 cookie->header 서비스에서 처리. shouldNotFilter 메서드 사용
        //shouldNotFilter();
        //1. 토큰 추출
        String accessToken = jwtUtil.resolveTokenHeader(request);

        //2. 토큰 검증
        if (accessToken != null) {
            if (!jwtUtil.validateToken(accessToken)) {
                jwtExceptionHandler(response, "AccessToken이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED.value());
                return;
            }
            //3. 검증 성공시 인증 객체 생성
            Claims claims = jwtUtil.getUserInfoFromToken(accessToken);
            String providerName = claims.getSubject();
            String role = claims.get("role", String.class);

            OAuth2UserDetails oAuth2UserDetails = new OAuth2UserDetails(
                    UserDto.builder()
                            .providerName(providerName)
                            .role(role)
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
            String json = objectMapper.writeValueAsString(new SecurityExceptionDto(message, statusCode)); //TODO: 필터 추가
            response.getWriter().write(json); //json 문자열을 응답으로 작성
        } catch (IOException e) {
            throw new RuntimeException("Error while processing JSON", e);
        }
    }
}