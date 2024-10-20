package org.userservice.userservice.dto.auth;

import lombok.*;
import org.userservice.userservice.domain.AuthRole;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {
    private String providerName;
    private String name;
    private AuthRole role;
}
