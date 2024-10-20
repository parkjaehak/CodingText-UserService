package org.userservice.userservice.dto.user;

import lombok.*;
import org.userservice.userservice.domain.CodeLanguage;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoResponse {
    private String userId;
    private String nickName;
    private String profileUrl;
    private String profileMessage;
    private CodeLanguage codeLanguage;
}
