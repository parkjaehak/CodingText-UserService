package org.userservice.userservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthRole {

    ROLE_USER_A("최초 로그인 후 회원가입 대기하는 사용자 권한"),
    ROLE_USER_B("회원가입까지 마친 사용자 권한"),
    ROLE_ADMIN( "관리자 권한");


    private final String displayName;

    public static AuthRole fromString(String role) {
        for (AuthRole authRole : AuthRole.values()) {
            if (authRole.name().equals(role)) {
                return authRole;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
}
