package org.userservice.userservice.dto.auth;

import org.userservice.userservice.dto.auth.OAuth2Response;

import java.util.Map;

public class GoogleResponse implements OAuth2Response {
    private final Map<String, Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }
    @Override
    public String getProvider() {

        return "google";
    }
    @Override
    public String getProviderId() {

        return attribute.get("sub").toString();
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
        return attribute.get("picture").toString();
    }
    @Override
    public String getGender() {
        return null;
    }
    @Override
    public String getBirthday() {
        return null;
    }
    @Override
    public String getBirthYear() {
        return null;
    }
    @Override
    public String getMobile() {
        return null;
    }
}
