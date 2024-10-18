package org.userservice.userservice.dto.auth;

import java.util.Map;

public class NaverResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }

    @Override
    public String getProfileImage() {
        return attribute.get("profile_image").toString();
    }

    @Override
    public String getGender() {
        return attribute.get("gender").toString(); //F: 여성 M: 남성 U: 확인불가
    }

    @Override
    public String getBirthday() {
        return attribute.get("birthday").toString();
    }

    @Override
    public String getBirthYear() {
        return attribute.get("birthyear").toString();
    }

    @Override
    public String getMobile() {
        return attribute.get("mobile").toString();
    }
}