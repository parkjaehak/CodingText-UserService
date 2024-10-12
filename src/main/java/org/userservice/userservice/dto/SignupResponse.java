package org.userservice.userservice.dto;

import lombok.*;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.jwt.JwtToken;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupResponse {

    private String userId;
    private AuthRole role;
    private JwtToken jwtToken;
}
