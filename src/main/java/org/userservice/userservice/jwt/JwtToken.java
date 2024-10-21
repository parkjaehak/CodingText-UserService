package org.userservice.userservice.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
public class JwtToken {

    @Schema(description = "Access token", example = "Bearer eyJhbGciOiJIUzUxMiJ9...")
    private final String accessToken;

    @Schema(description = "Refresh token", example = "Bearer eyJhbGciOiJIUzUxMiJ9...", nullable = true)
    private final String refreshToken;
}