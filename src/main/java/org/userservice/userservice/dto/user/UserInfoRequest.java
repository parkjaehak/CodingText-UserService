package org.userservice.userservice.dto.user;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.domain.CodeLanguage;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoRequest {
    private String nickName;
    private String profileMessage;
    private CodeLanguage codeLanguage;
}
