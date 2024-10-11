package org.userservice.userservice.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.userservice.userservice.dto.error.SecurityExceptionDto;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationEntryPointCustom implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private static final SecurityExceptionDto exceptionDto =
            new SecurityExceptionDto("인증되지 않은 사용자 요청, 로그인이 필요합니다.", HttpStatus.UNAUTHORIZED.value());

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {

        String json = objectMapper.writeValueAsString(exceptionDto);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}