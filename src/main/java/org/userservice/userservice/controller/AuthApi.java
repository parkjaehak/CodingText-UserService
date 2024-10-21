package org.userservice.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.userservice.userservice.dto.auth.SignupRequest;
import org.userservice.userservice.dto.auth.SignupResponse;
import org.userservice.userservice.jwt.JwtToken;

@Tag(name = "Auth", description = "인증관리 API")
public interface AuthApi {

    @Operation(summary = "쿠키 JWT를 헤더 JWT로 변환",
            description = "쿠키 속 JWT 추출하고 검증하여 Authorization header 에 Bearer token 형태로 담아 전달한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token successfully extracted and added to header.",
                    content = @Content(schema = @Schema(implementation = JwtToken.class)))
    })
    ResponseEntity<?> cookieToHeader(String token, HttpServletResponse response);


    @Operation(summary = "회원가입 및 쿠키 JWT를 헤더 JWT로 변환",
            description =
                    "1. 회원가입 전달받은 데이터를 소셜로그인 시 생성된 사용자 정보에 추가한다. \n" +
                    "2. 쿠키 속 JWT 추출하고 검증하여 Authorization header 에 Bearer token 형태로 담아 전달한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully signed up.",
                    content = @Content(schema = @Schema(implementation = SignupResponse.class)))
    })
    ResponseEntity<?> signup(SignupRequest signupRequest, String token, HttpServletResponse response);
}
