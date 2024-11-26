package org.userservice.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.userservice.userservice.dto.auth.SignupRequest;
import org.userservice.userservice.dto.auth.SignupResponse;
import org.userservice.userservice.dto.user.UserDeletionResponse;
import org.userservice.userservice.error.ErrorResponse;
import org.userservice.userservice.jwt.JwtToken;

@Tag(name = "Auth", description = "인증관리 API")
public interface AuthApi {

    @Operation(summary = "쿠키 JWT를 헤더 JWT로 변환",
            description = "쿠키 속 JWT 추출하고 검증하여 Authorization header 에 Bearer token 형태로 담아 전달한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "헤더로 정상적으로 담아 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtToken.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class), examples = {
                            @ExampleObject(name = "토큰이 존재하지 않는 경우", value = """
                                    {
                                        "status":"401",
                                        "code": "C004",
                                        "message": "인증되지 않은 사용자입니다."
                                    }
                                    """),
                            @ExampleObject(name = "토큰이 유효하지 않는 경우", value = """
                                    {
                                        "status":"401",
                                        "code": "C004",
                                        "message": "인증되지 않은 사용자입니다."
                                    }
                                    """),
                    }
                    )),
            @ApiResponse(responseCode = "403", description = "권한이 적절하지 않아 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class), examples = {
                            @ExampleObject(name = "권한이 일치하지 않은 경우", description = "cookie-to-header 엔드포인트 진입시에는 ROLE_USER_B인 경우만 허가한다.", value = """
                                    {
                                        "status":"403",
                                        "code": "C005",
                                        "message": "권한이 없는 사용자입니다."
                                    }
                                    """),
                    }
                    ))
    })
    ResponseEntity<?> cookieToHeader(
            @Parameter(description = "Authorization token from cookie", required = false) String token,
            HttpServletResponse response);


    @Operation(summary = "회원가입 및 쿠키 JWT를 헤더 JWT로 변환",
            description =
                    "1. 회원가입 전달받은 데이터를 소셜로그인 시 생성된 사용자 정보에 추가한다. \n" + "2. 쿠키 속 JWT 추출하고 검증하여 Authorization header 에 Bearer token 형태로 담아 전달한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SignupResponse.class))),
            @ApiResponse(responseCode = "401", description = "회원가입 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class), examples = {
                            @ExampleObject(name = "토큰이 존재하지 않는 경우", value = """
                                    {
                                        "status":"401",
                                        "code": "C004",
                                        "message": "인증되지 않은 사용자입니다."
                                    }
                                    """),
                            @ExampleObject(name = "토큰이 유효하지 않는 경우", value = """
                                    {
                                        "status":"401",
                                        "code": "C004",
                                        "message": "인증되지 않은 사용자입니다."
                                    }
                                    """),
                    }
                    )),
            @ApiResponse(responseCode = "403", description = "회원가입 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class), examples = {
                            @ExampleObject(name = "권한이 일치하지 않은 경우",
                                    description = "회원가입 엔드포인트 진입시 ROLE_USER_A인 경우만 회원가입을 허용한다.", value = """
                                    {
                                        "status":"403",
                                        "code": "C005",
                                        "message": "권한이 없는 사용자입니다."
                                    }
                                    """),
                    }
                    ))
    })
    ResponseEntity<?> signupTest(SignupRequest signupRequest,
                             @Parameter(description = "Authorization token from cookie")String token,
                             HttpServletResponse response);



    @Operation(summary = "회원가입 (on-prem)",
            description = "개발환경에서 쿠키를 이용한 토큰 전달불가로 소셜로그인 시 query parameter 로 토큰 전달 후 최초로그인 유저가 접근하는 엔드포인트.<br>" +
                    "Bearer prefix 와 함께 header 로 전달받은 후 새로운 ROLE 을 적용하여 JWT 를 발급한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignupResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(토큰이 없는 경우, 토큰이 유효하지 않은 경우)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "권한이 없는 사용자(ROLE 일치하지 않은 경우)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    ResponseEntity<?> signup(SignupRequest signupRequest, String token, HttpServletResponse response);

    @Operation(summary = "토큰 재발급",
            description = "Access token 이 만료되었을 경우 Refresh token 을 전달받아 인증이 되면 새로운 Access, Refresh token 을 발급한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "새 토큰 발급 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtToken.class))),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(토큰이 없는 경우, 토큰이 유효하지 않은 경우)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "권한이 없는 사용자(ROLE 일치하지 않은 경우)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "토큰 타입이 맞지 않음(access, refresh)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    ResponseEntity<?> reissueToken(String refreshToken, HttpServletResponse response);
}
