package org.userservice.userservice.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {
    private String name;
    private String providerName;
    private String role;
}
