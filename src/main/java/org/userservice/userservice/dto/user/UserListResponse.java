package org.userservice.userservice.dto.user;

import lombok.*;
import org.userservice.userservice.domain.Tier;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserListResponse {
    private String userId;
    private String nickName;
    private String email;
    private Tier tier;
    private LocalDate registerDate;
}

