package org.userservice.userservice.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.jwt.JwtToken;

@Schema(description = "회원가입시 업데이트 된 유저권한과 새롭게 생성된 토큰을 전달한다.")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupResponse {

    @Schema(description = "사용자 아이디", example = "naver_3LSKMZKXkTXpf2D9vEVLK8osUI")
    private String userId;

    @Schema(description = "유저 권한", example = "ROLE_USER_B")
    private AuthRole role;

    private JwtToken jwtToken;
}
