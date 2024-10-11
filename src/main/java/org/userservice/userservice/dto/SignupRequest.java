package org.userservice.userservice.dto;

import lombok.*;
import org.userservice.userservice.domain.CodeLanguage;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequest {

    private String nickname;
    private CodeLanguage codeLanguage;
    private Boolean useSocialProfile; //소셜 계정 프로필 사진 사용 여부
}
