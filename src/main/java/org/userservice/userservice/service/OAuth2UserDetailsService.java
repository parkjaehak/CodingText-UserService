package org.userservice.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.domain.Gender;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.dto.auth.KakaoResponse;
import org.userservice.userservice.dto.auth.GoogleResponse;
import org.userservice.userservice.dto.auth.OAuth2UserDetails;
import org.userservice.userservice.dto.auth.NaverResponse;
import org.userservice.userservice.dto.auth.OAuth2Response;
import org.userservice.userservice.dto.auth.UserDto;
import org.userservice.userservice.repository.UserRepository;

import java.time.LocalDate;
import java.util.Map;

/**
 * 클래스 요약:
 * OAuth2 리소스 서버에서 받은 사용자 정보(OAuth2UserRequest)를 바탕으로, 사용자 정보를 로드하고 가공하는 단계
 * <p>
 * 설명:
 * 인증이 완료되면 return 되는 OAuth2UserDetails 객체는 OAuth2UserDetails 객체는 Authentication 객체로 래핑되어 인증완료
 * Authentication 객체는 SecurityContextHolder(인증 객체를 저장하는 컨테이너) 에 저장
 * 이 후 customSuccessHandler 에서는 SecurityContext 에 저장된 인증 객체를 가져와 추가 작업(JWT 생성)을 수행가능
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserDetailsService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("리소스 서버로부터 인증된 유저는? = {}", oAuth2User);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = getOAuth2Response(registrationId, oAuth2User.getAttributes());
        if (oAuth2Response == null) {
            throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
        }
        String providerName = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        User user = userRepository.findById(providerName).orElse(null);
        String dayOfBirth = normalizeDayOfBirth(oAuth2Response.getBirthday(), oAuth2Response.getBirthYear());
        Gender genderEnum = normalizeGender(oAuth2Response.getGender());

        if (user == null) {
            userRepository.save(User.builder()
                    .userId(providerName)
                    .email(oAuth2Response.getEmail())
                    .userName(oAuth2Response.getName())
                    .phoneNumber(oAuth2Response.getMobile())
                    .profileUrl(oAuth2Response.getProfileImage())
                    .dayOfBirth(LocalDate.parse(dayOfBirth))
                    .role(AuthRole.ROLE_USER_A)
                    .gender(genderEnum)
                    .build());

            UserDto userDto = UserDto.builder()
                    .providerName(providerName)
                    .name(oAuth2Response.getName())
                    .role(AuthRole.ROLE_USER_A)
                    .build();
            return new OAuth2UserDetails(userDto);
        } else {
            userRepository.save(user.toBuilder()
                    .email(oAuth2Response.getEmail())
                    .userName(oAuth2Response.getName())
                    .phoneNumber(oAuth2Response.getMobile())
                    .build());

            UserDto userDto = UserDto.builder()
                    .providerName(user.getUserId())
                    .name(oAuth2Response.getName())
                    .role(user.getRole()) //ROLE_USER_B
                    .build();
            return new OAuth2UserDetails(userDto);
        }
    }

    private OAuth2Response getOAuth2Response(String registrationId, Map<String, Object> attributes) {
        switch (registrationId) {
            case "naver":
                return new NaverResponse(attributes);
            case "google":
                return new GoogleResponse(attributes);
            case "kakao":
                return new KakaoResponse(attributes);
            default:
                return null;
        }
    }

    private String normalizeDayOfBirth(String birthday, String birthyear) {
        String dayOfBirth = "1900-01-01";
        if (birthday != null && birthyear != null) {
            if (birthday.length() == 4) {// MMDD -> MM-DD
                String month = birthday.substring(0, 2);
                String day = birthday.substring(2, 4);
                dayOfBirth = birthyear + "-" + month + "-" + day;
            } else {
                dayOfBirth = birthyear + "-" + birthday;
            }
        }
        return dayOfBirth;
    }

    private Gender normalizeGender(String gender) {
        if ("male".equals(gender) || "M".equals(gender)) {
            return Gender.MALE;
        } else if ("female".equals(gender) || "F".equals(gender)) {
            return Gender.FEMALE;
        }
        return Gender.NONE;
    }
}