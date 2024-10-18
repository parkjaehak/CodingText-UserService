package org.userservice.userservice.dto.auth;

import org.userservice.userservice.dto.auth.OAuth2Response;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
        this.kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        this.profile = (Map<String, Object>) kakaoAccount.get("profile");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }
    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }
    @Override
    public String getEmail() {
        return kakaoAccount.get("email").toString();
    }
    @Override
    public String getName() {
        return kakaoAccount.get("name").toString();
    }
    @Override
    public String getProfileImage() {
        return profile.get("profile_image_url").toString();
    }
    @Override
    public String getGender() {
        return kakaoAccount.get("gender").toString(); //female: 여성, male: 남성
    }
    @Override
    public String getBirthday() {
        return kakaoAccount.get("birthday").toString();
    }
    @Override
    public String getBirthYear() {
        return kakaoAccount.get("birthyear").toString(); //YYYY
    }
    @Override
    public String getMobile() {
        String phoneNumber = kakaoAccount.get("phone_number").toString();
        // 형식 통일
        return normalizePhoneNumber(phoneNumber);
    }

    private String normalizePhoneNumber(String phoneNumber) {
        // 정규 표현식을 사용하여 국가 코드와 나머지 번호를 분리
        String regex = "\\+82\\s*(10)-(\\d{4})-(\\d{4})";
        String replacement = "010-$2-$3";

        // 정규 표현식을 사용하여 변환
        return phoneNumber.replaceAll(regex, replacement);
    }
}
