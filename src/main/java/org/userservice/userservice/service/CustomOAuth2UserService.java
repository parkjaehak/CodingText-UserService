package org.userservice.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.dto.CustomOAuth2User;
import org.userservice.userservice.dto.NaverResponse;
import org.userservice.userservice.dto.OAuth2Response;
import org.userservice.userservice.dto.UserDto;
import org.userservice.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {
            //oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {
            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String providerName = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        User user = userRepository.findByProviderName(providerName);

        if (user == null) {
            userRepository.save(User.builder()
                    .name(oAuth2Response.getName())
                    .providerName(providerName)
                    .email(oAuth2Response.getEmail())
                    .role("ROLE_USER")
                    .build());

            UserDto userDto = UserDto.builder()
                    .name(oAuth2Response.getName())
                    .providerName(providerName)
                    .role("ROLE_USER")
                    .build();
            return new CustomOAuth2User(userDto);
        } else {
            userRepository.save(user.toBuilder()
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .build());

            UserDto userDto = UserDto.builder()
                    .name(oAuth2Response.getName())
                    .providerName(user.getProviderName())
                    .role(user.getRole())
                    .build();
            return new CustomOAuth2User(userDto);
        }
    }
}